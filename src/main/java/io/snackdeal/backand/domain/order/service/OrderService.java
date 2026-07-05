package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.user.order.dto.OrderCompleteRequest;
import io.snackdeal.backand.api.user.order.dto.OrderCompleteResponse;
import io.snackdeal.backand.api.user.order.dto.OrderItemRequest;
import io.snackdeal.backand.api.user.order.dto.OrderItemResponse;
import io.snackdeal.backand.api.user.order.dto.OrderListResponse;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareRequest;
import io.snackdeal.backand.api.user.order.dto.OrderPrepareResponse;
import io.snackdeal.backand.api.user.order.dto.OrderResponse;
import io.snackdeal.backand.api.user.order.dto.OrderSummaryResponse;
import io.snackdeal.backand.api.user.order.dto.PaymentResponse;
import io.snackdeal.backand.api.user.order.dto.RefundRequest;
import io.snackdeal.backand.api.user.order.dto.RefundResponse;
import io.snackdeal.backand.api.user.order.dto.ShippingRequest;
import io.snackdeal.backand.api.user.order.dto.ShippingResponse;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.domain.delivery.entity.Delivery;
import io.snackdeal.backand.domain.delivery.repository.DeliveryRepository;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.client.PortOneClient;
import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;
import io.snackdeal.backand.domain.order.entity.OrderItem;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.entity.Payment;
import io.snackdeal.backand.domain.order.entity.Shipping;
import io.snackdeal.backand.domain.order.repository.OrderItemRepository;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import io.snackdeal.backand.domain.order.repository.PaymentRepository;
import io.snackdeal.backand.domain.order.repository.ShippingPolicyRepository;
import io.snackdeal.backand.domain.order.repository.ShippingRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 사용자 주문/결제 서비스.
 *
 * 결제 흐름(포트원 SDK → 토스페이먼츠 테스트결제):
 *   prepare(주문 PENDING 임시생성 + 금액 확정, 재고는 체크만) → 프론트 결제 → complete(서버 금액 재검증 + 확정)
 *
 * 재고 차감/쿠폰 사용은 결제 검증이 성공한 complete 시점에 트랜잭션 안에서 원자적으로 처리한다.
 * 검증 실패(미결제/금액 위변조) 시 예외를 던져 트랜잭션을 롤백하고 포트원 결제를 취소한다
 * → 주문은 PENDING 으로 남고 재고/쿠폰은 건드리지 않으므로 되돌릴 상태가 없다.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    // 배송비 정책 기본값: 정책 행이 없을 때만 사용 (무료기준 20,000 / 배송비 0). 실제 값은 shipping_policy(관리자 변경).
    private static final long SHIPPING_POLICY_ID = 1L;
    private static final long DEFAULT_FREE_THRESHOLD = 20000L;
    private static final long DEFAULT_BASE_FEE = 0L;
    private static final String PG_PROVIDER = "tosspayments";
    private static final DateTimeFormatter ORDER_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final PortOneClient portOneClient;

    /*
     * 주문 준비. 주문을 PENDING 으로 임시 생성하고 결제 예정 금액을 확정한다.
     * 재고는 미리 체크(SELECT)만 하고 차감하지 않는다 → 결제창 진입 전 품절이면 즉시 차단.
     */
    @Transactional
    public OrderPrepareResponse prepare(String email, OrderPrepareRequest request) {
        Member member = findMember(email);
        ShippingRequest shipping = resolveShipping(member, request);

        // 상품 조회 + 재고 확인 + 상품 총액 계산
        long productAmount = 0L;
        List<OrderItemLine> lines = new ArrayList<>();
        for (OrderItemRequest item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));
            if (product.getStock() < item.quantity()) {
                throw new BusinessException(ResponseCode.ORDER_OUT_OF_STOCK);
            }
            productAmount += product.getPrice() * item.quantity();
            lines.add(new OrderItemLine(product, item.quantity()));
        }

        long shippingFee = resolveShippingFee(productAmount);
        long discountAmount = calculateDiscount(member, request.userCouponId(), productAmount);
        long finalAmount = Math.max(0L, productAmount + shippingFee - discountAmount);

        // 주문 저장 (PENDING_PAYMENT)
        Orders order = ordersRepository.save(Orders.builder()
                .orderNumber(generateOrderNumber())
                .productAmount(productAmount)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .memberId(member.getId())
                .userCouponId(request.userCouponId())
                .build());

        // 주문 항목 저장 (주문 시점 상품명/가격을 스냅샷으로 보관)
        for (OrderItemLine line : lines) {
            orderItemRepository.save(OrderItem.builder()
                    .productName(line.product().getName())
                    .price(line.product().getPrice())
                    .quantity(line.quantity())
                    .productId(line.product().getId())
                    .orderId(order.getId())
                    .build());
        }

        // 배송지 + 결제(READY) 생성. merchant_uid 는 주문번호와 동일하게 둔다.
        shippingRepository.save(Shipping.builder()
                .orderId(order.getId())
                .receiverName(shipping.receiverName())
                .receiverPhone(shipping.receiverPhone())
                .zipcode(shipping.zipcode())
                .address(shipping.address())
                .detailAddress(shipping.detailAddress())
                .deliveryRequest(shipping.deliveryRequest())
                .build());
        paymentRepository.save(Payment.builder()
                .amount(finalAmount)
                .pgProvider(PG_PROVIDER)
                .orderId(order.getId())
                .merchantUid(order.getOrderNumber())
                .build());

        return new OrderPrepareResponse(
                order.getOrderNumber(), finalAmount,
                member.getEmail(), member.getName(), member.getPhone());
    }

    /*
     * 결제 검증 및 주문 확정.
     * imp_uid 로 포트원 실제 결제금액을 조회해 DB 예정금액과 비교한다.
     *  - 일치: 재고 차감 + 쿠폰 사용 + 주문 PAYMENT_COMPLETED + 결제 PAID (트랜잭션 원자 처리)
     *  - 불일치/미결제: 포트원 결제취소 호출 후 예외 → 롤백(주문은 PENDING 유지, 재고/쿠폰 미변경)
     */
    @Transactional
    public OrderCompleteResponse complete(String email, OrderCompleteRequest request) {
        Member member = findMember(email);
        Orders order = ordersRepository.findByOrderNumber(request.merchantUid())
                .orElseThrow(() -> new BusinessException(ResponseCode.ORDER_NOT_FOUND));
        if (!order.getMemberId().equals(member.getId())) {
            throw new BusinessException(ResponseCode.ORDER_ACCESS_DENIED);
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(ResponseCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        // 포트원에서 실제 결제 내역 조회
        PortOnePayment paid = portOneClient.getPayment(request.impUid());
        if (!paid.isPaid()) {
            portOneClient.cancelPayment(request.impUid(), "결제가 완료되지 않았습니다");
            throw new BusinessException(ResponseCode.PAYMENT_NOT_PAID);
        }
        if (!order.getFinalAmount().equals(paid.amount())) {
            portOneClient.cancelPayment(request.impUid(), "결제 금액 불일치(위변조 의심)");
            throw new BusinessException(ResponseCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 재고 재확인 + 차감
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(ResponseCode.ORDER_OUT_OF_STOCK);
            }
            product.decreaseStock(item.getQuantity());
        }

        // 사용 쿠폰 처리 (USED)
        if (order.getUserCouponId() != null) {
            userCouponRepository.findById(order.getUserCouponId()).ifPresent(UserCoupon::use);
        }

        // 주문/결제 확정
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new BusinessException(ResponseCode.ORDER_NOT_FOUND));
        order.markPaymentCompleted();
        payment.markPaid(paid.impUid(), paid.payMethod(), paid.pgProvider(), paid.receiptUrl(), paid.paidAt());

        return new OrderCompleteResponse(
                order.getId(), order.getOrderNumber(), order.getStatus(),
                order.getProductAmount(), order.getShippingFee(), order.getDiscountAmount(), order.getFinalAmount(),
                new OrderCompleteResponse.Payment(
                        payment.getImpUid(), payment.getPayMethod(), payment.getPgProvider(),
                        payment.getStatus(), payment.getReceiptUrl()),
                payment.getPaidAt());
    }

    /*
     * 내 주문내역 (최신순 페이징). 대표 상품명/상품 종류 수를 함께 계산한다.
     */
    @Transactional(readOnly = true)
    public OrderListResponse findList(String email, int page, int size) {
        Member member = findMember(email);
        Page<Orders> orders = ordersRepository.findByMemberIdOrderByOrderedAtDesc(
                member.getId(), PageRequest.of(page, size));

        List<OrderSummaryResponse> summaries = orders.getContent().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    String mainProductName = items.isEmpty() ? null : items.get(0).getProductName();
                    return new OrderSummaryResponse(
                            order.getId(), order.getOrderNumber(), order.getOrderedAt(),
                            mainProductName, items.size(), order.getFinalAmount(), order.getStatus());
                })
                .toList();

        return new OrderListResponse(summaries, page, size, orders.getTotalElements());
    }

    /*
     * 주문 상세. 본인 주문만 조회 가능(타인 접근 403). 상품별 내역 + 배송지 + 결제 정보를 반환한다.
     */
    @Transactional(readOnly = true)
    public OrderResponse findById(String email, Long orderId) {
        Member member = findMember(email);
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ResponseCode.ORDER_NOT_FOUND));
        if (!order.getMemberId().equals(member.getId())) {
            throw new BusinessException(ResponseCode.ORDER_ACCESS_DENIED);
        }

        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(), item.getProductName(), item.getPrice(),
                        item.getQuantity(), item.getPrice() * item.getQuantity()))
                .toList();

        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElse(null);
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        String couponName = resolveCouponName(order.getUserCouponId());

        return new OrderResponse(
                order.getId(), order.getOrderNumber(), order.getOrderedAt(), order.getStatus(),
                items,
                shipping == null ? null : new ShippingResponse(
                        shipping.getReceiverName(), shipping.getReceiverPhone(), shipping.getZipcode(),
                        shipping.getAddress(), shipping.getDetailAddress(), shipping.getDeliveryRequest(),
                        shipping.getCourier(), shipping.getTrackingNumber(), shipping.getStatus()),
                payment == null ? null : new PaymentResponse(
                        order.getProductAmount(), order.getShippingFee(), couponName, order.getDiscountAmount(),
                        order.getFinalAmount(), payment.getPayMethod(), payment.getPgProvider(),
                        payment.getStatus(), payment.getReceiptUrl(), payment.getPaidAt()));
    }

    /*
     * 환불 요청. 결제완료/배송준비중 상태에서만 가능하며 주문을 REFUND_REQUESTED 로 바꾼다.
     * 배송중/배송완료 등은 고객센터 문의 대상이므로 요청을 차단한다(422). 승인/거절은 관리자 API 담당.
     */
    @Transactional
    public RefundResponse refund(String email, Long orderId, RefundRequest request) {
        Member member = findMember(email);
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ResponseCode.ORDER_NOT_FOUND));
        if (!order.getMemberId().equals(member.getId())) {
            throw new BusinessException(ResponseCode.ORDER_ACCESS_DENIED);
        }
        if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED
                && order.getStatus() != OrderStatus.PREPARING_SHIPMENT) {
            throw new BusinessException(ResponseCode.REFUND_NOT_ALLOWED);
        }

        order.requestRefund();
        return new RefundResponse(order.getId(), order.getOrderNumber(), order.getStatus());
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────

    private Member findMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));
    }

    // 관리자 설정(shipping_policy)에 따라 배송비를 계산한다. 정책 행이 없으면 기본값으로 대체.
    private long resolveShippingFee(long productAmount) {
        return shippingPolicyRepository.findById(SHIPPING_POLICY_ID)
                .map(policy -> policy.calculateFee(productAmount))
                .orElseGet(() -> (productAmount >= DEFAULT_FREE_THRESHOLD) ? 0L : DEFAULT_BASE_FEE);
    }

    /*
     * 배송지 확정. deliveryId 가 있으면 주소록에서 채우고(본인 것만),
     * 없으면 요청 shipping 을 그대로 쓴다. 둘 다 없으면 입력값 누락(400).
     */
    private ShippingRequest resolveShipping(Member member, OrderPrepareRequest request) {
        if (request.deliveryId() != null) {
            Delivery delivery = deliveryRepository.findById(request.deliveryId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.DELIVERY_NOT_FOUND));
            if (!delivery.getMemberId().equals(member.getId())) {
                throw new BusinessException(ResponseCode.DELIVERY_NOT_FOUND);
            }
            // 배송 요청사항은 주소록에 없으므로, 요청에 있으면 그 값을 함께 쓴다.
            String deliveryRequest = (request.shipping() != null) ? request.shipping().deliveryRequest() : null;
            return new ShippingRequest(
                    delivery.getReceiverName(), delivery.getReceiverPhone(), delivery.getZipcode(),
                    delivery.getAddress(), delivery.getDetailAddress(), deliveryRequest);
        }
        if (request.shipping() == null) {
            throw new BusinessException(ResponseCode.INPUT_REQUIRED);
        }
        return request.shipping();
    }

    /*
     * 쿠폰 할인 계산 + 유효성 검증.
     * 본인 쿠폰/ACTIVE/유효기간/최소주문금액을 모두 만족해야 하며, 하나라도 어기면 409(조건 미달).
     * PERCENT 는 상품총액 기준, 할인액은 상품총액을 넘지 않도록 캡을 씌운다.
     */
    private long calculateDiscount(Member member, Long userCouponId, long productAmount) {
        if (userCouponId == null) {
            return 0L;
        }
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));
        if (!userCoupon.getMemberId().equals(member.getId())
                || userCoupon.getStatus() != UserCouponStatus.ACTIVE) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if ((coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom()))
                || (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil()))
                || productAmount < coupon.getMinOrderPrice()) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }

        long discount = switch (coupon.getDiscountType()) {
            case FIXED -> coupon.getDiscountValue();
            case PERCENT -> productAmount * coupon.getDiscountValue() / 100L;
        };
        return Math.min(discount, productAmount);
    }

    private String resolveCouponName(Long userCouponId) {
        if (userCouponId == null) {
            return null;
        }
        return userCouponRepository.findById(userCouponId)
                .flatMap(uc -> couponRepository.findById(uc.getCouponId()))
                .map(Coupon::getName)
                .orElse(null);
    }

    // 주문번호: ORD-yyyyMMdd-XXXXX (5자리 난수). unique 제약으로 충돌 시 재시도한다.
    private String generateOrderNumber() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String candidate = "ORD-" + LocalDate.now().format(ORDER_DATE)
                    + "-" + String.format("%05d", ThreadLocalRandom.current().nextInt(100000));
            if (ordersRepository.findByOrderNumber(candidate).isEmpty()) {
                return candidate;
            }
        }
        // 극히 드문 연속 충돌 대비: 타임스탬프 꼬리를 덧붙여 유일성을 보장한다.
        return "ORD-" + LocalDate.now().format(ORDER_DATE) + "-" + System.nanoTime();
    }

    // 주문 항목 계산용 임시 홀더 (상품 엔티티 + 수량).
    private record OrderItemLine(Product product, int quantity) {
    }
}
