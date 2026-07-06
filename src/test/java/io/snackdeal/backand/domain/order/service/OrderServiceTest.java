package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.user.order.dto.OrderCompleteRequest;
import io.snackdeal.backand.api.user.order.dto.OrderCompleteResponse;
import io.snackdeal.backand.api.user.order.dto.OrderItemRequest;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareRequest;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareResponse;
import io.snackdeal.backand.api.user.order.dto.RefundRequest;
import io.snackdeal.backand.api.user.order.dto.RefundResponse;
import io.snackdeal.backand.api.user.order.dto.ShippingRequest;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.domain.delivery.repository.DeliveryRepository;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.client.PortOneClient;
import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;
import io.snackdeal.backand.domain.order.entity.OrderItem;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.entity.Payment;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.snackdeal.backand.domain.order.repository.OrderItemRepository;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import io.snackdeal.backand.domain.order.repository.PaymentRepository;
import io.snackdeal.backand.domain.order.repository.ShippingPolicyRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock private OrdersRepository ordersRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private ShippingRepository shippingRepository;
    @Mock private ShippingPolicyRepository shippingPolicyRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserCouponRepository userCouponRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private DeliveryRepository deliveryRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private PortOneClient portOneClient;

    private static final String EMAIL = "u@test.com";

    private Member member(long id) {
        Member member = Member.builder().email(EMAIL).name("홍길동").phone("01011112222").build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product product(long id, long price, int stock) {
        Product product = Product.builder().name("허니버터 프레첼").price(price).stock(stock).categoryId(1L).build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Orders order(long id, long finalAmount, long memberId, OrderStatus status) {
        Orders order = Orders.builder()
                .orderNumber("ORD-1").productAmount(finalAmount).shippingFee(0L).discountAmount(0L)
                .finalAmount(finalAmount).memberId(memberId).build();
        ReflectionTestUtils.setField(order, "id", id);
        if (status != OrderStatus.PENDING_PAYMENT) {
            order.changeStatus(status);
        }
        return order;
    }

    private ShippingRequest shipping() {
        return new ShippingRequest("홍길동", "01012345678", "06133", "서울 강남구", "456호", "문 앞");
    }

    @Test
    @DisplayName("prepare - 재고 확인 후 금액을 확정하고 주문/결제/배송을 생성")
    void prepare_success() {
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 4500L, 10)));
        when(ordersRepository.findByOrderNumber(any())).thenReturn(Optional.empty());
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> {
            Orders o = inv.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 100L);
            return o;
        });

        OrderPrepareRequest request = new OrderPrepareRequest(
                List.of(new OrderItemRequest(1L, 2)), null, shipping(), null);

        OrderPrepareResponse response = orderService.prepare(EMAIL, request);

        // 정책 행 없음 → 기본값(무료기준 20,000 / 배송비 0) 상품 9,000 < 20,000 이지만 기본 배송비 0 → 9,000
        assertEquals(9000L, response.amount());
        assertEquals(EMAIL, response.buyerEmail());
        assertTrue(response.paymentId().startsWith("ORD-"));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(shippingRepository).save(any());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("prepare - 배송비 정책이 설정돼 있으면 그 값으로 배송비를 계산")
    void prepare_appliesShippingPolicy() {
        io.snackdeal.backand.domain.order.entity.ShippingPolicy policy =
                io.snackdeal.backand.domain.order.entity.ShippingPolicy.builder()
                        .id(1L).baseFee(3000L).freeThreshold(50000L).build();

        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 4500L, 10)));
        when(shippingPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(ordersRepository.findByOrderNumber(any())).thenReturn(Optional.empty());
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> {
            Orders o = inv.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 100L);
            return o;
        });

        OrderPrepareRequest request = new OrderPrepareRequest(
                List.of(new OrderItemRequest(1L, 2)), null, shipping(), null);

        OrderPrepareResponse response = orderService.prepare(EMAIL, request);

        // 상품 9,000 < 무료기준 50,000 → 배송비 3,000 → 12,000
        assertEquals(12000L, response.amount());
    }

    @Test
    @DisplayName("prepare - 재고 부족이면 결제창 진입 전에 차단")
    void prepare_outOfStock() {
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, 4500L, 1)));

        OrderPrepareRequest request = new OrderPrepareRequest(
                List.of(new OrderItemRequest(1L, 2)), null, shipping(), null);

        BusinessException e = assertThrows(BusinessException.class, () -> orderService.prepare(EMAIL, request));
        assertEquals(ResponseCode.ORDER_OUT_OF_STOCK, e.getResponseCode());
        verify(ordersRepository, never()).save(any());
    }

    @Test
    @DisplayName("complete - 결제 금액 일치 시 재고 차감 + 결제 확정")
    void complete_success() {
        Orders order = order(100L, 12000L, 1L, OrderStatus.PENDING_PAYMENT);
        Product product = product(1L, 4500L, 10);
        Payment payment = Payment.builder().amount(12000L).pgProvider("tosspayments").orderId(100L)
                .merchantUid("ORD-1").build();
        OrderItem item = OrderItem.builder().productName("허니버터").price(4500L).quantity(2)
                .productId(1L).orderId(100L).build();

        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findByOrderNumberForUpdate("ORD-1")).thenReturn(Optional.of(order));
        when(portOneClient.getPayment("ORD-1")).thenReturn(new PortOnePayment(
                "ORD-1", 12000L, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));
        when(orderItemRepository.findByOrderId(100L)).thenReturn(List.of(item));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));

        OrderCompleteResponse response = orderService.complete(EMAIL, new OrderCompleteRequest("ORD-1"));

        assertEquals(OrderStatus.PAYMENT_COMPLETED, response.status());
        assertEquals(PaymentStatus.PAID, response.payment().status());
        assertEquals("ORD-1", response.payment().paymentId());
        assertEquals(8, product.getStock()); // 10 - 2 차감
    }

    @Test
    @DisplayName("complete - 결제 금액 불일치 시 포트원 취소 호출 + 위변조 예외")
    void complete_amountMismatch() {
        Orders order = order(100L, 12000L, 1L, OrderStatus.PENDING_PAYMENT);

        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findByOrderNumberForUpdate("ORD-1")).thenReturn(Optional.of(order));
        when(portOneClient.getPayment("ORD-1")).thenReturn(new PortOnePayment(
                "ORD-1", 5000L, "PAID", "Card", "TOSSPAYMENTS", "http://receipt", LocalDateTime.now()));

        BusinessException e = assertThrows(BusinessException.class,
                () -> orderService.complete(EMAIL, new OrderCompleteRequest("ORD-1")));
        assertEquals(ResponseCode.PAYMENT_AMOUNT_MISMATCH, e.getResponseCode());
        verify(portOneClient).cancelPayment(eq("ORD-1"), any());
        verify(productRepository, never()).findByIdForUpdate(any());
    }

    @Test
    @DisplayName("complete - 미결제 상태면 결제 취소 + 미결제 예외")
    void complete_notPaid() {
        Orders order = order(100L, 12000L, 1L, OrderStatus.PENDING_PAYMENT);

        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findByOrderNumberForUpdate("ORD-1")).thenReturn(Optional.of(order));
        when(portOneClient.getPayment("ORD-1")).thenReturn(new PortOnePayment(
                "ORD-1", 12000L, "READY", null, null, null, null));

        BusinessException e = assertThrows(BusinessException.class,
                () -> orderService.complete(EMAIL, new OrderCompleteRequest("ORD-1")));
        assertEquals(ResponseCode.PAYMENT_NOT_PAID, e.getResponseCode());
        verify(portOneClient).cancelPayment(eq("ORD-1"), any());
    }

    @Test
    @DisplayName("findById - 타인의 주문에 접근하면 403")
    void findById_accessDenied() {
        Orders order = order(100L, 12000L, 2L, OrderStatus.PAYMENT_COMPLETED); // 주인은 memberId=2
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class, () -> orderService.findById(EMAIL, 100L));
        assertEquals(ResponseCode.ORDER_ACCESS_DENIED, e.getResponseCode());
    }

    @Test
    @DisplayName("refund - 결제완료 주문은 환불 요청 상태로 전환된다")
    void refund_success() {
        Orders order = order(100L, 12000L, 1L, OrderStatus.PAYMENT_COMPLETED);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        RefundResponse response = orderService.refund(EMAIL, 100L, new RefundRequest("단순 변심"));

        assertEquals(OrderStatus.REFUND_REQUESTED, response.status());
        assertEquals(OrderStatus.REFUND_REQUESTED, order.getStatus());
    }

    @Test
    @DisplayName("refund - 배송중 주문은 환불 요청이 차단된다")
    void refund_notAllowed() {
        Orders order = order(100L, 12000L, 1L, OrderStatus.SHIPPED);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(1L)));
        when(ordersRepository.findById(100L)).thenReturn(Optional.of(order));

        BusinessException e = assertThrows(BusinessException.class,
                () -> orderService.refund(EMAIL, 100L, new RefundRequest("변심")));
        assertEquals(ResponseCode.REFUND_NOT_ALLOWED, e.getResponseCode());
    }
}
