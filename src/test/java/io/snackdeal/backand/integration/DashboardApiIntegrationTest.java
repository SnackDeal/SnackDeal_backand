package io.snackdeal.backand.integration;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 관리자 대시보드 차트 API 를 HTTP 스택 전 구간(컨트롤러→서비스→리포지토리→H2)으로 검증
 * 오늘 발생한 회원가입/주문/쿠폰 발급·사용을 직접 저장해두고, 오늘 하루를 기간으로 조회했을 때
 * 일자별 집계 결과가 실제 데이터와 일치하는지 확인
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
@Transactional
class DashboardApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private UserCouponRepository userCouponRepository;

    private final LocalDate today = LocalDate.now();

    private Member saveMember(String email, MemberRole role) {
        return memberRepository.save(Member.builder()
                .email(email).password("x").name("홍길동").phone("01011112222").role(role).build());
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

    private RequestPostProcessor as(Member member) {
        UserDetails principal = new MemberDetails(
                member.getId(), member.getEmail(), member.getPassword(), member.getRole());
        return authentication(new UsernamePasswordAuthenticationToken(
                principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("회원 차트: 오늘 가입한 회원 수가 정확히 집계된다")
    void memberChart() throws Exception {
        Member admin = saveMember("admin@dashboard.test", MemberRole.ADMIN);
        saveMember("newbie1@dashboard.test", MemberRole.USER);
        saveMember("newbie2@dashboard.test", MemberRole.USER);

        mockMvc.perform(get("/admin/main/chart/members")
                        .param("startDate", today.toString())
                        .param("endDate", today.toString())
                        .with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].date").value(today.toString()))
                .andExpect(jsonPath("$.data.items[0].count").value(3)); // admin 포함 3명
    }

    @Test
    @DisplayName("주문 차트: 오늘 발생한 주문 수가 정확히 집계된다")
    void orderChart() throws Exception {
        Member admin = saveMember("admin2@dashboard.test", MemberRole.ADMIN);
        Member buyer = saveMember("buyer@dashboard.test", MemberRole.USER);
        saveOrder(buyer.getId(), 10000L, OrderStatus.PAYMENT_COMPLETED);
        saveOrder(buyer.getId(), 20000L, OrderStatus.PAYMENT_COMPLETED);

        mockMvc.perform(get("/admin/main/chart/orders")
                        .param("startDate", today.toString())
                        .param("endDate", today.toString())
                        .with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].count").value(2));
    }

    @Test
    @DisplayName("상품판매 차트: 취소된 주문은 제외하고 매출액/판매수량이 집계된다")
    void productSalesChart() throws Exception {
        Member admin = saveMember("admin3@dashboard.test", MemberRole.ADMIN);
        Member buyer = saveMember("buyer2@dashboard.test", MemberRole.USER);

        Orders paid = saveOrder(buyer.getId(), 10000L, OrderStatus.PAYMENT_COMPLETED);
        orderItemRepository.save(OrderItem.builder()
                .productName("허니버터칩").price(5000L).quantity(2).productId(1L).orderId(paid.getId()).build());

        Orders cancelled = saveOrder(buyer.getId(), 30000L, OrderStatus.CANCELLED);
        orderItemRepository.save(OrderItem.builder()
                .productName("허니버터칩").price(30000L).quantity(5).productId(1L).orderId(cancelled.getId()).build());

        mockMvc.perform(get("/admin/main/chart/sales")
                        .param("startDate", today.toString())
                        .param("endDate", today.toString())
                        .with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].salesAmount").value(10000))
                .andExpect(jsonPath("$.data.items[0].soldQuantity").value(2));
    }

    @Test
    @DisplayName("쿠폰 차트: 오늘 발급/사용된 쿠폰 건수가 각각 집계된다")
    void couponChart() throws Exception {
        Member admin = saveMember("admin4@dashboard.test", MemberRole.ADMIN);
        Member buyer = saveMember("buyer3@dashboard.test", MemberRole.USER);

        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("여름맞이 쿠폰").discountType(DiscountType.FIXED).discountValue(1000L)
                .minOrderPrice(0L).validFrom(today.atStartOfDay().minusDays(1))
                .validUntil(today.atStartOfDay().plusDays(30))
                .totalQuantity(100).issueType(IssueType.EVENT).couponBoardId(null).isActive(true).build());

        UserCoupon used = userCouponRepository.save(UserCoupon.builder()
                .memberId(buyer.getId()).couponId(coupon.getId()).build());
        used.use();
        userCouponRepository.save(used);

        userCouponRepository.save(UserCoupon.builder()
                .memberId(buyer.getId()).couponId(coupon.getId()).build());

        mockMvc.perform(get("/admin/main/chart/coupons")
                        .param("startDate", today.toString())
                        .param("endDate", today.toString())
                        .with(as(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].issuedCount").value(2))
                .andExpect(jsonPath("$.data.items[0].usedCount").value(1));
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦으면 400 + DA001")
    void invalidDateRange() throws Exception {
        Member admin = saveMember("admin5@dashboard.test", MemberRole.ADMIN);

        mockMvc.perform(get("/admin/main/chart/orders")
                        .param("startDate", today.toString())
                        .param("endDate", today.minusDays(1).toString())
                        .with(as(admin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DA001"));
    }

    @Test
    @DisplayName("일반 사용자는 차트 API 접근 불가 (403)")
    void userCannotAccessChart() throws Exception {
        Member user = saveMember("plain@dashboard.test", MemberRole.USER);

        mockMvc.perform(get("/admin/main/chart/orders")
                        .param("startDate", today.toString())
                        .param("endDate", today.toString())
                        .with(as(user)))
                .andExpect(status().isForbidden());
    }
}
