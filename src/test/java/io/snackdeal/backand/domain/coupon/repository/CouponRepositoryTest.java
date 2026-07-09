package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CouponRepository 클래스의")
class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Nested
    @DisplayName("Describe: findByIdForUpdate() 메서드는")
    class Describe_findByIdForUpdate {

        @Nested
        @DisplayName("Context: 삭제되지 않은 쿠폰을 id로 조회하는 경우")
        class Context_with_active_and_deleted_coupon {

            @Test
            @DisplayName("It: 해당 쿠폰만 조회")
            void It_삭제되지_않은_쿠폰만_조회() {
                // given
                Coupon activeCoupon = couponRepository.save(createCoupon("활성 쿠폰", IssueType.EVENT, true,
                        LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7), 10, 0));

                Coupon deletedCoupon = createCoupon("삭제 쿠폰", IssueType.EVENT, true,
                        LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(7), 10, 0);
                ReflectionTestUtils.setField(deletedCoupon, "deletedAt", LocalDateTime.now());
                deletedCoupon = couponRepository.save(deletedCoupon);

                // when
                Optional<Coupon> activeResult = couponRepository.findByIdForUpdate(activeCoupon.getId());
                Optional<Coupon> deletedResult = couponRepository.findByIdForUpdate(deletedCoupon.getId());

                // then
                assertThat(activeResult).isPresent();
                assertThat(activeResult.get().getId()).isEqualTo(activeCoupon.getId());
                assertThat(activeResult.get().getName()).isEqualTo("활성 쿠폰");
                assertThat(deletedResult).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Describe: searchAdminCoupons() 메서드는")
    class Describe_searchAdminCoupons {

        @Nested
        @DisplayName("Context: 관리자 쿠폰 목록에서 issueType=EVENT 조건으로 조회하는 경우")
        class Context_with_event_issue_type {

            @Test
            @DisplayName("It: 이벤트 쿠폰만 조회하고 회원가입 쿠폰은 제외")
            void It_이벤트_쿠폰만_조회() {
                // given
                LocalDateTime now = LocalDateTime.now();
                Coupon eventCoupon = createCoupon("이벤트 쿠폰", IssueType.EVENT, true,
                        now.minusDays(1), now.plusDays(7), 10, 0);
                Coupon signinCoupon = createCoupon("회원가입 쿠폰", IssueType.SIGNIN, true,
                        now.minusDays(1), now.plusDays(7), 10, 0);

                couponRepository.saveAll(List.of(eventCoupon, signinCoupon));

                // when
                Page<Coupon> result = couponRepository.searchAdminCoupons(
                        null,
                        IssueType.EVENT,
                        CouponStatus.ACTIVE.name(),
                        now,
                        PageRequest.of(0, 10)
                );

                // then
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getName()).isEqualTo("이벤트 쿠폰");
            }
        }
    }

    @Nested
    @DisplayName("Describe: findIssuableCouponsForUpdate() 메서드는")
    class Describe_findIssuableCouponsForUpdate {

        @Nested
        @DisplayName("Context: 회원가입 쿠폰 자동 발급 대상 쿠폰을 조회하는 경우")
        class Context_with_signin_issue_type {

            @Test
            @DisplayName("It: 발급 가능한 회원가입 쿠폰만 조회")
            void It_발급_가능한_회원가입_쿠폰만_조회() {
                // given
                LocalDateTime now = LocalDateTime.now();
                Coupon issuable = createCoupon("발급 가능 회원가입 쿠폰", IssueType.SIGNIN, true,
                        now.minusDays(1), now.plusDays(7), 10, 0);
                Coupon eventCoupon = createCoupon("이벤트 쿠폰", IssueType.EVENT, true,
                        now.minusDays(1), now.plusDays(7), 10, 0);
                Coupon expired = createCoupon("만료 회원가입 쿠폰", IssueType.SIGNIN, true,
                        now.minusDays(10), now.minusDays(1), 10, 0);
                Coupon soldOut = createCoupon("소진 회원가입 쿠폰", IssueType.SIGNIN, true,
                        now.minusDays(1), now.plusDays(7), 1, 1);

                couponRepository.saveAll(List.of(issuable, eventCoupon, expired, soldOut));

                // when
                List<Coupon> result = couponRepository.findIssuableCouponsForUpdate(IssueType.SIGNIN, now);

                // then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getName()).isEqualTo("발급 가능 회원가입 쿠폰");
            }
        }
    }

    private Coupon createCoupon(String name,
                                IssueType issueType,
                                boolean isActive,
                                LocalDateTime validFrom,
                                LocalDateTime validUntil,
                                Integer totalQuantity,
                                Integer issuedQuantity) {
        Coupon coupon = Coupon.builder()
                .name(name)
                .discountType(DiscountType.FIXED)
                .discountValue(1000L)
                .minOrderPrice(0L)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .totalQuantity(totalQuantity)
                .issueType(issueType)
                .couponBoardId(issueType == IssueType.EVENT ? 1L : null)
                .isActive(isActive)
                .build();
        ReflectionTestUtils.setField(coupon, "issuedQuantity", issuedQuantity);
        return coupon;
    }
}
