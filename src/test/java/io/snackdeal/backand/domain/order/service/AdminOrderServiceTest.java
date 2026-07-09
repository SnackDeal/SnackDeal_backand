package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.admin.order.dto.AdminOrderListResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundResponse;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.entity.OrderItem;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.entity.Payment;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.snackdeal.backand.domain.order.repository.OrderItemRepository;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import io.snackdeal.backand.domain.order.repository.PaymentRepository;
import io.snackdeal.backand.domain.order.repository.ShippingRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminOrderServiceTest {

    @InjectMocks
    private AdminOrderService adminOrderService;

    @Mock private OrdersRepository ordersRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private ShippingRepository shippingRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserCouponRepository userCouponRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private MemberRepository memberRepository;

    private Member member(long id) {
        Member member = Member.builder().email("hong@test.com").name("홍길동").build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product product(long id, int stock) {
        Product product = Product.builder().name("허니버터").price(4500L).stock(stock).categoryId(1L).build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Orders order(long id, long memberId, OrderStatus status, Long userCouponId) {
        Orders order = Orders.builder()
                .orderNumber("ORD-1").productAmount(12000L).shippingFee(0L).discountAmount(0L)
                .finalAmount(12000L).memberId(memberId).userCouponId(userCouponId).build();
        ReflectionTestUtils.setField(order, "id", id);
        if (status != OrderStatus.PENDING_PAYMENT) {
            order.changeStatus(status);
        }
        return order;
    }

    private OrderItem item() {
        return OrderItem.builder().productName("허니버터").price(4500L).quantity(2)
                .productId(1L).orderId(100L).build();
    }

    private Coupon coupon(java.time.LocalDateTime validUntil) {
        return Coupon.builder()
                .name("신규가입 3천원").discountType(DiscountType.FIXED).discountValue(3000L).minOrderPrice(0L)
                .validFrom(java.time.LocalDateTime.now().minusDays(1)).validUntil(validUntil)
                .totalQuantity(100).issueType(IssueType.SIGNIN).couponBoardId(1L).build();
    }

    private Coupon validCoupon() {
        return coupon(java.time.LocalDateTime.now().plusDays(10));
    }

    @Test
    @DisplayName("findList - 검색 결과를 구매자 정보와 함께 매핑")
    void findList_success() {
        Orders order = order(100L, 1L, OrderStatus.PREPARING_SHIPMENT, null);
        Page<Orders> page = new PageImpl<>(List.of(order));
        when(ordersRepository.search(any(), any(), any(), any(), any(), any())).thenReturn(page);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member(1L)));
        when(orderItemRepository.findByOrderId(100L)).thenReturn(List.of(item()));

        AdminOrderListResponse response = adminOrderService.findList(null, null, null, null, 0, 20);

        assertEquals(1, response.orders().size());
        assertEquals("hong@test.com", response.orders().get(0).buyerEmail());
        assertEquals(1L, response.total());
    }

    @Test
    @DisplayName("changeStatus - 허용된 전이는 성공하고 manualOverride 가 켜진다")
    void changeStatus_success() {
        Orders order = order(100L, 1L, OrderStatus.PAYMENT_COMPLETED, null);
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        AdminOrderStatusResponse response = adminOrderService.changeStatus(
                100L, new AdminOrderStatusRequest(OrderStatus.PREPARING_SHIPMENT, null, null, null));

        assertEquals(OrderStatus.PREPARING_SHIPMENT, response.status());
        assertTrue(response.manualOverride());
    }

    @Test
    @DisplayName("changeStatus - 이 API 로 지정 불가한 상태값은 400")
    void changeStatus_invalidStatusValue() {
        Orders order = order(100L, 1L, OrderStatus.PAYMENT_COMPLETED, null);
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class, () -> adminOrderService.changeStatus(
                100L, new AdminOrderStatusRequest(OrderStatus.PENDING_PAYMENT, null, null, null)));
        assertEquals(ResponseCode.INVALID_ORDER_STATUS, e.getResponseCode());
    }

    @Test
    @DisplayName("changeStatus - 배송완료를 이전 상태로 되돌리면 422")
    void changeStatus_invalidTransition() {
        Orders order = order(100L, 1L, OrderStatus.COMPLETED, null);
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class, () -> adminOrderService.changeStatus(
                100L, new AdminOrderStatusRequest(OrderStatus.PREPARING_SHIPMENT, null, null, null)));
        assertEquals(ResponseCode.INVALID_ORDER_STATUS_TRANSITION, e.getResponseCode());
    }

    @Test
    @DisplayName("changeStatus - 취소 시 재고와 쿠폰을 복구")
    void changeStatus_cancelRestoresStockAndCoupon() {
        Orders order = order(100L, 1L, OrderStatus.PAYMENT_COMPLETED, 78L);
        Product product = product(1L, 5);
        UserCoupon userCoupon = UserCoupon.builder().memberId(1L).couponId(9L).build();
        userCoupon.use(); // USED 상태로 만들어 둔다
        Coupon coupon = validCoupon(); // 유효기간이 남은 쿠폰

        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(100L)).thenReturn(List.of(item()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userCouponRepository.findById(78L)).thenReturn(Optional.of(userCoupon));
        when(couponRepository.findById(9L)).thenReturn(Optional.of(coupon));

        adminOrderService.changeStatus(100L, new AdminOrderStatusRequest(OrderStatus.CANCELLED, null, null, "관리자 취소"));

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, product.getStock()); // 5 + 2 복구
        assertEquals(UserCouponStatus.ACTIVE, userCoupon.getStatus()); // 유효기간 남아 복구됨
    }

    @Test
    @DisplayName("refund - 승인 시 환불완료 + 결제취소 + 재고 복구 + 기간 남은 쿠폰 복구")
    void refund_approve() {
        Orders order = order(100L, 1L, OrderStatus.REFUND_REQUESTED, 78L);
        Product product = product(1L, 5);
        Payment payment = Payment.builder().amount(12000L).pgProvider("tosspayments").orderId(100L)
                .merchantUid("ORD-1").build();
        UserCoupon userCoupon = UserCoupon.builder().memberId(1L).couponId(9L).build();
        userCoupon.use();

        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(100L)).thenReturn(List.of(item()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));
        when(userCouponRepository.findById(78L)).thenReturn(Optional.of(userCoupon));
        when(couponRepository.findById(9L)).thenReturn(Optional.of(validCoupon()));

        AdminRefundResponse response = adminOrderService.refund(100L, new AdminRefundRequest(true, null, null));

        assertEquals(OrderStatus.REFUND_COMPLETED, response.status());
        assertEquals(PaymentStatus.CANCELLED, response.paymentStatus());
        assertTrue(response.stockRestored());
        assertTrue(response.couponRestored());
        assertEquals(7, product.getStock());
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        assertEquals(UserCouponStatus.ACTIVE, userCoupon.getStatus());
    }

    @Test
    @DisplayName("refund - 승인해도 유효기간이 지난 쿠폰은 복구하지 않음")
    void refund_approve_expiredCouponNotRestored() {
        Orders order = order(100L, 1L, OrderStatus.REFUND_REQUESTED, 78L);
        Payment payment = Payment.builder().amount(12000L).pgProvider("tosspayments").orderId(100L)
                .merchantUid("ORD-1").build();
        UserCoupon userCoupon = UserCoupon.builder().memberId(1L).couponId(9L).build();
        userCoupon.use();

        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(100L)).thenReturn(List.of(item()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 5)));
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));
        when(userCouponRepository.findById(78L)).thenReturn(Optional.of(userCoupon));
        when(couponRepository.findById(9L)).thenReturn(Optional.of(coupon(java.time.LocalDateTime.now().minusDays(1))));

        AdminRefundResponse response = adminOrderService.refund(
                100L, new AdminRefundRequest(true, null, false));

        assertTrue(!response.couponRestored());
        assertEquals(UserCouponStatus.USED, userCoupon.getStatus()); // 만료 → 복구 안 됨
    }

    @Test
    @DisplayName("refund - 거절인데 사유가 없으면 400")
    void refund_rejectWithoutReason() {
        Orders order = order(100L, 1L, OrderStatus.REFUND_REQUESTED, null);
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class,
                () -> adminOrderService.refund(100L, new AdminRefundRequest(false, null, null)));
        assertEquals(ResponseCode.REFUND_REJECT_REASON_REQUIRED, e.getResponseCode());
    }

    @Test
    @DisplayName("refund - 거절 시 요청 직전 상태로 되돌린다")
    void refund_reject() {
        Orders order = order(100L, 1L, OrderStatus.PREPARING_SHIPMENT, null);
        order.requestRefund(); // previousStatus=PREPARING_SHIPMENT, status=REFUND_REQUESTED
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        AdminRefundResponse response = adminOrderService.refund(
                100L, new AdminRefundRequest(false, "배송이 이미 시작됨", null));

        assertEquals(OrderStatus.PREPARING_SHIPMENT, response.status());
        assertEquals("배송이 이미 시작됨", response.rejectReason());
    }

    @Test
    @DisplayName("refund - 환불요청 상태가 아니면 422")
    void refund_notRequested() {
        Orders order = order(100L, 1L, OrderStatus.PAYMENT_COMPLETED, null);
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class,
                () -> adminOrderService.refund(100L, new AdminRefundRequest(true, null, null)));
        assertEquals(ResponseCode.REFUND_NOT_REQUESTED, e.getResponseCode());
    }
}
