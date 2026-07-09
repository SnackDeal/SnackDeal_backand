package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserCouponRepository 클래스의")
class UserCouponRepositoryTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Nested
    @DisplayName("Describe: findByIdAndMemberIdForUpdate() 메서드는")
    class Describe_findByIdAndMemberIdForUpdate {

        @Nested
        @DisplayName("Context: 주문에서 회원 쿠폰을 조회하는 경우")
        class Context_with_member_coupon {

            @Test
            @DisplayName("It: userCouponId와 memberId가 모두 일치하는 쿠폰만 조회한다")
            void It_userCouponId와_memberId가_모두_일치하는_쿠폰만_조회() {
                // given
                UserCoupon memberCoupon = userCouponRepository.save(createUserCoupon(1L, 10L));
                userCouponRepository.save(createUserCoupon(2L, 11L));

                // when
                Optional<UserCoupon> matchedResult =
                        userCouponRepository.findByIdAndMemberIdForUpdate(memberCoupon.getId(), 1L);
                Optional<UserCoupon> otherMemberResult =
                        userCouponRepository.findByIdAndMemberIdForUpdate(memberCoupon.getId(), 2L);

                // then
                assertThat(matchedResult).isPresent();
                assertThat(matchedResult.get().getId()).isEqualTo(memberCoupon.getId());
                assertThat(matchedResult.get().getMemberId()).isEqualTo(1L);
                assertThat(otherMemberResult).isEmpty();
            }
        }
    }

    private UserCoupon createUserCoupon(Long memberId, Long couponId) {
        return UserCoupon.builder()
                .memberId(memberId)
                .couponId(couponId)
                .build();
    }
}
