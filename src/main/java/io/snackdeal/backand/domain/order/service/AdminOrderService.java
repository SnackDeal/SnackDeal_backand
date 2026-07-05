package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.admin.order.dto.AdminOrderDetailResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderListResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderStatusResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminOrderSummaryResponse;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundRequest;
import io.snackdeal.backand.api.admin.order.dto.AdminRefundResponse;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.entity.OrderItem;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.entity.Payment;
import io.snackdeal.backand.domain.order.entity.Shipping;
import io.snackdeal.backand.domain.order.repository.OrderItemRepository;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import io.snackdeal.backand.domain.order.repository.PaymentRepository;
import io.snackdeal.backand.domain.order.repository.ShippingRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 관리자 주문 관리 서비스. 주문 리스트/상세 조회, 상태 변경, 환불 처리(승인/거절)를 담당한다.
 *
 * 이 API 로 상태를 바꾸면 manualOverride 가 켜져 (추후 도입될) 스케줄러 자동 진행 대상에서 제외된다.
 * 취소/환불 승인 시 재고·쿠폰 복구는 트랜잭션 안에서 처리한다(테스트 결제라 실제 PG 환불은 생략).
 */
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    // 관리자 상태 변경 API 로 지정 가능한 상태값. 결제/환불 상태는 각 전용 API 가 관리하므로 제외한다.
    private static final Set<OrderStatus> ASSIGNABLE_STATUSES = EnumSet.of(
            OrderStatus.PREPARING_SHIPMENT, OrderStatus.SHIPPED,
            OrderStatus.COMPLETED, OrderStatus.CANCELLED);

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final ProductRepository productRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    /*
     * 주문 리스트 검색(최신순 페이징).
     * keyword 는 주문번호 또는 구매자(email/이름)로 매칭한다. 구매자 검색은 회원 검색으로 id 목록을 풀어
     * 주문 쿼리의 IN 절에 넘긴다(주문 테이블에는 memberId 만 있으므로).
     */
    @Transactional(readOnly = true)
    public AdminOrderListResponse findList(String keyword, OrderStatus status,
                                           LocalDateTime dateFrom, LocalDateTime dateTo,
                                           int page, int size) {
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        // 구매자 키워드 → 회원 id 목록. IN 절이 비면 안 되므로 최소 sentinel(-1)을 채운다.
        List<Long> memberIds = List.of(-1L);
        if (normalizedKeyword != null) {
            List<Long> matched = memberRepository.search(normalizedKeyword, null, Pageable.unpaged())
                    .map(Member::getId).getContent();
            if (!matched.isEmpty()) {
                memberIds = matched;
            }
        }

        Page<Orders> orders = ordersRepository.search(
                normalizedKeyword, memberIds, status, dateFrom, dateTo, PageRequest.of(page, size));

        List<AdminOrderSummaryResponse> summaries = orders.getContent().stream()
                .map(order -> {
                    Member buyer = memberRepository.findById(order.getMemberId()).orElse(null);
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    String mainProductName = items.isEmpty() ? null : items.get(0).getProductName();
                    return new AdminOrderSummaryResponse(
                            order.getId(), order.getOrderNumber(),
                            buyer == null ? null : buyer.getEmail(),
                            buyer == null ? null : buyer.getName(),
                            mainProductName, order.getFinalAmount(), order.getStatus(), order.getOrderedAt());
                })
                .toList();

        return new AdminOrderListResponse(summaries, page, size, orders.getTotalElements());
    }

    /*
     * 주문 상세. 사용자용과 달리 스케줄러 상태(미도입 → null), imp_uid, 사용 쿠폰 상세를 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public AdminOrderDetailResponse findById(Long id) {
        Orders order = findOrder(id);
        Member buyer = memberRepository.findById(order.getMemberId()).orElse(null);
        long totalOrderCount = ordersRepository.countByMemberId(order.getMemberId());

        List<AdminOrderDetailResponse.Item> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(item -> new AdminOrderDetailResponse.Item(
                        item.getProductId(), item.getProductName(), item.getPrice(),
                        item.getQuantity(), item.getPrice() * item.getQuantity()))
                .toList();

        Shipping shipping = shippingRepository.findByOrderId(order.getId()).orElse(null);
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        AdminOrderDetailResponse.UsedCoupon usedCoupon = resolveUsedCoupon(order.getUserCouponId());

        return new AdminOrderDetailResponse(
                order.getId(), order.getOrderNumber(), order.getStatus(), order.getOrderedAt(),
                payment == null ? null : payment.getPaidAt(), order.getCancelledAt(),
                null, null, order.isManualOverride(),
                new AdminOrderDetailResponse.Buyer(
                        buyer == null ? null : buyer.getId(),
                        buyer == null ? null : buyer.getEmail(),
                        buyer == null ? null : buyer.getName(),
                        totalOrderCount),
                items,
                shipping == null ? null : new AdminOrderDetailResponse.Shipping(
                        shipping.getReceiverName(), shipping.getReceiverPhone(), shipping.getZipcode(),
                        shipping.getAddress(), shipping.getDetailAddress(), shipping.getDeliveryRequest(),
                        shipping.getCourier(), shipping.getTrackingNumber(), shipping.getStatus()),
                payment == null ? null : new AdminOrderDetailResponse.Payment(
                        order.getProductAmount(), order.getShippingFee(), usedCoupon, order.getDiscountAmount(),
                        order.getFinalAmount(), payment.getPayMethod(), payment.getPgProvider(),
                        payment.getStatus(), payment.getImpUid()));
    }

    /*
     * 주문 상태 변경.
     * 허용 상태값·전이 규칙을 검증하고, CANCELLED 시 재고·쿠폰을 복구한다.
     * 변경 시 manualOverride 가 true 가 된다.
     */
    @Transactional
    public AdminOrderStatusResponse changeStatus(Long id, AdminOrderStatusRequest request) {
        Orders order = findOrder(id);
        OrderStatus target = request.status();

        if (!ASSIGNABLE_STATUSES.contains(target)) {
            throw new BusinessException(ResponseCode.INVALID_ORDER_STATUS);
        }
        if (!isValidTransition(order.getStatus(), target)) {
            throw new BusinessException(ResponseCode.INVALID_ORDER_STATUS_TRANSITION);
        }

        // 배송 전 취소: 재고 + 사용 쿠폰 복구
        if (target == OrderStatus.CANCELLED) {
            restoreStock(order);
            restoreCoupon(order);
        }

        // SHIPPED 전환 시 택배사/송장번호 저장
        if (target == OrderStatus.SHIPPED) {
            shippingRepository.findByOrderId(order.getId()).ifPresent(shipping ->
                    shipping.updateTracking(request.courier(), request.trackingNumber()));
        }

        order.changeStatusByAdmin(target);
        return new AdminOrderStatusResponse(
                order.getId(), order.getOrderNumber(), order.getStatus(),
                order.isManualOverride(), order.getUpdatedAt(),
                request.courier(), request.trackingNumber(), request.memo());
    }

    /*
     * 환불 처리. REFUND_REQUESTED 상태의 주문만 대상.
     *  - 승인: 주문 REFUND_COMPLETED + 결제 CANCELLED + (기본) 재고 복구. 쿠폰은 MVP 정책상 복구하지 않음.
     *  - 거절: 요청 직전 상태로 복귀 (거절 사유 필수).
     * 실제 PG 환불 API 는 호출하지 않는다(테스트 결제).
     */
    @Transactional
    public AdminRefundResponse refund(Long id, AdminRefundRequest request) {
        Orders order = findOrder(id);
        if (order.getStatus() != OrderStatus.REFUND_REQUESTED) {
            throw new BusinessException(ResponseCode.REFUND_NOT_REQUESTED);
        }

        // 거절
        if (!request.approve()) {
            if (request.rejectReason() == null || request.rejectReason().isBlank()) {
                throw new BusinessException(ResponseCode.REFUND_REJECT_REASON_REQUIRED);
            }
            order.rejectRefund();
            return AdminRefundResponse.rejected(
                    order.getId(), order.getOrderNumber(), order.getStatus(),
                    LocalDateTime.now(), request.rejectReason());
        }

        // 승인
        boolean stockRestored = false;
        if (request.restoreStockOrDefault()) {
            restoreStock(order);
            stockRestored = true;
        }
        // 쿠폰은 유효기간이 남아 있을 때만 복구한다.
        boolean couponRestored = restoreCoupon(order);
        paymentRepository.findByOrderId(order.getId()).ifPresent(Payment::markCancelled);
        order.approveRefund();
        return AdminRefundResponse.approved(
                order.getId(), order.getOrderNumber(), order.getCancelledAt(), stockRestored, couponRestored);
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────────

    private Orders findOrder(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.ORDER_NOT_FOUND));
    }

    /*
     * 허용되는 상태 전이:
     *   PAYMENT_COMPLETED → PREPARING_SHIPMENT / CANCELLED
     *   PREPARING_SHIPMENT → SHIPPED / CANCELLED
     *   SHIPPED → COMPLETED   (배송 시작 후 취소는 환불 프로세스로)
     */
    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PAYMENT_COMPLETED -> to == OrderStatus.PREPARING_SHIPMENT || to == OrderStatus.CANCELLED;
            case PREPARING_SHIPMENT -> to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED -> to == OrderStatus.COMPLETED;
            default -> false;
        };
    }

    private void restoreStock(Orders order) {
        for (OrderItem item : orderItemRepository.findByOrderId(order.getId())) {
            productRepository.findById(item.getProductId())
                    .ifPresent(product -> product.increaseStock(item.getQuantity()));
        }
    }

    /*
     * 사용 쿠폰 복구. 정책: 쿠폰 유효기간(validUntil)이 남아 있으면 재사용 가능하도록 되돌리고,
     * 이미 만료됐으면 복구하지 않는다. 복구 여부를 반환한다.
     */
    private boolean restoreCoupon(Orders order) {
        if (order.getUserCouponId() == null) {
            return false;
        }
        UserCoupon userCoupon = userCouponRepository.findById(order.getUserCouponId()).orElse(null);
        if (userCoupon == null) {
            return false;
        }
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId()).orElse(null);
        boolean stillValid = coupon != null
                && (coupon.getValidUntil() == null || !LocalDateTime.now().isAfter(coupon.getValidUntil()));
        if (stillValid) {
            userCoupon.restore();
            return true;
        }
        return false;
    }

    private AdminOrderDetailResponse.UsedCoupon resolveUsedCoupon(Long userCouponId) {
        if (userCouponId == null) {
            return null;
        }
        return userCouponRepository.findById(userCouponId)
                .flatMap(uc -> couponRepository.findById(uc.getCouponId())
                        .map(coupon -> new AdminOrderDetailResponse.UsedCoupon(
                                uc.getId(), coupon.getName(), coupon.getDiscountType(), coupon.getDiscountValue())))
                .orElse(null);
    }
}
