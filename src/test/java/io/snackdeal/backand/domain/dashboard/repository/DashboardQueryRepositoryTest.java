package io.snackdeal.backand.domain.dashboard.repository;

import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.order.entity.OrderItem;
import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.repository.OrderItemRepository;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 대시보드 차트 집계 리포지토리 통합 테스트 (H2, 실제 JPQL 실행 검증).
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(DashboardQueryRepository.class)
class DashboardQueryRepositoryTest {

    @Autowired
    private DashboardQueryRepository dashboardQueryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    private final LocalDateTime start = LocalDate.now().atStartOfDay();
    private final LocalDateTime end = start.plusDays(1);

    private Member saveMember(String email) {
        return memberRepository.save(Member.builder()
                .email(email).password("x").name("홍길동")
                .birth(LocalDate.of(2000, 1, 1)).gender(Gender.MALE)
                .phone("01011112222").role(MemberRole.USER).build());
    }

    private Orders saveOrder(long memberId, long finalAmount, OrderStatus status) {
        Orders order = ordersRepository.save(Orders.builder()
                .orderNumber("ORD-" + System.nanoTime())
                .productAmount(finalAmount).shippingFee(0L).discountAmount(0L)
                .finalAmount(finalAmount).memberId(memberId).build());
        if (status != OrderStatus.PENDING_PAYMENT) {
            order.changeStatus(status);
        }
        return order;
    }

    @Test
    @DisplayName("findMemberCreatedAtBetween - 기간 내 회원 가입시각 목록을 반환")
    void findMemberCreatedAtBetween() {
        saveMember("a@test.com");
        saveMember("b@test.com");

        List<LocalDateTime> result = dashboardQueryRepository.findMemberCreatedAtBetween(start, end);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findOrderedAtBetween - 기간 내 주문 발생시각 목록을 반환")
    void findOrderedAtBetween() {
        Member member = saveMember("buyer@test.com");
        saveOrder(member.getId(), 10000L, OrderStatus.PAYMENT_COMPLETED);
        saveOrder(member.getId(), 20000L, OrderStatus.PAYMENT_COMPLETED);

        List<LocalDateTime> result = dashboardQueryRepository.findOrderedAtBetween(start, end);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findSalesBetween - 취소/환불 주문은 제외하고 (주문시각, 매출액)을 반환")
    void findSalesBetween() {
        Member member = saveMember("buyer2@test.com");
        saveOrder(member.getId(), 10000L, OrderStatus.PAYMENT_COMPLETED);
        saveOrder(member.getId(), 30000L, OrderStatus.CANCELLED);

        List<Object[]> result = dashboardQueryRepository.findSalesBetween(
                start, end, List.of(OrderStatus.CANCELLED, OrderStatus.REFUND_REQUESTED, OrderStatus.REFUND_COMPLETED));

        assertEquals(1, result.size());
        assertEquals(10000L, result.get(0)[1]);
    }

    @Test
    @DisplayName("findSoldQuantityBetween - 취소/환불 주문은 제외하고 (주문시각, 판매수량)을 반환")
    void findSoldQuantityBetween() {
        Member member = saveMember("buyer3@test.com");
        Orders paid = saveOrder(member.getId(), 10000L, OrderStatus.PAYMENT_COMPLETED);
        Orders cancelled = saveOrder(member.getId(), 30000L, OrderStatus.CANCELLED);

        orderItemRepository.save(OrderItem.builder()
                .productName("허니버터칩").price(10000L).quantity(3).productId(1L).orderId(paid.getId()).build());
        orderItemRepository.save(OrderItem.builder()
                .productName("허니버터칩").price(30000L).quantity(5).productId(1L).orderId(cancelled.getId()).build());

        List<Object[]> result = dashboardQueryRepository.findSoldQuantityBetween(
                start, end, List.of(OrderStatus.CANCELLED, OrderStatus.REFUND_REQUESTED, OrderStatus.REFUND_COMPLETED));

        assertEquals(1, result.size());
        assertEquals(3, result.get(0)[1]);
    }

    @Test
    @DisplayName("findCouponIssuedAtBetween / findCouponUsedAtBetween - 기간 내 쿠폰 발급/사용 시각 목록을 반환")
    void findCouponIssuedAndUsedAtBetween() {
        Member member = saveMember("couponer@test.com");
        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("여름맞이 쿠폰").discountType(DiscountType.FIXED).discountValue(1000L)
                .minOrderPrice(0L).validFrom(start.minusDays(1)).validUntil(start.plusDays(30))
                .totalQuantity(100).issueType(IssueType.EVENT).couponBoardId(null).isActive(true).build());

        UserCoupon used = userCouponRepository.save(UserCoupon.builder().memberId(member.getId()).couponId(coupon.getId()).build());
        used.use();
        userCouponRepository.save(used);

        userCouponRepository.save(UserCoupon.builder().memberId(member.getId()).couponId(coupon.getId()).build());

        List<LocalDateTime> issued = dashboardQueryRepository.findCouponIssuedAtBetween(start, end);
        List<LocalDateTime> usedList = dashboardQueryRepository.findCouponUsedAtBetween(start, end);

        assertEquals(2, issued.size());
        assertEquals(1, usedList.size());
    }

    @Test
    @DisplayName("기간 밖의 데이터는 집계되지 않는다")
    void excludesDataOutsideRange() {
        saveMember("outside@test.com");

        List<LocalDateTime> result = dashboardQueryRepository.findMemberCreatedAtBetween(
                start.minusDays(10), start.minusDays(9));

        assertTrue(result.isEmpty());
    }
}
