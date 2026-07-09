package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.api.user.coupon.dto.CouponDownloadResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardListResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponListResponse;
import io.snackdeal.backand.domain.coupon.dto.CouponDiscountResult;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.repository.CouponBoardRepository;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService 클래스의")
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponBoardRepository couponBoardRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Nested
    @DisplayName("Describe: issueSigninCoupons() 메서드는")
    class Describe_issueSigninCoupons {

        @Nested
        @DisplayName("Context: 발급 가능한 회원가입 쿠폰이 있는 경우")
        class Context_with_issuable_signin_coupon {

            @Test
            @DisplayName("It: 신규 회원에게 쿠폰을 지급")
            void It_신규_회원에게_쿠폰을_지급() {
                // given
                Long memberId = 1L;
                Coupon coupon = signinCoupon(10L, 10, 0);

                when(couponRepository.findIssuableCouponsForUpdate(eq(IssueType.SIGNIN), any(LocalDateTime.class)))
                        .thenReturn(List.of(coupon));
                when(userCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId()))
                        .thenReturn(false);

                // when
                couponService.issueSigninCoupons(memberId);

                // then
                assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("Context: 이미 지급된 쿠폰이 있는 경우")
        class Context_with_already_issued_coupon {

            @Test
            @DisplayName("It: 해당 쿠폰은 저장하지 않고 건너띈다")
            void It_이미_지급된_쿠폰은_건너띈다() {
                // given
                Long memberId = 1L;
                Coupon coupon = signinCoupon(10L, 10, 0);

                when(couponRepository.findIssuableCouponsForUpdate(eq(IssueType.SIGNIN), any(LocalDateTime.class)))
                        .thenReturn(List.of(coupon));
                when(userCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId()))
                        .thenReturn(true);

                // when
                couponService.issueSigninCoupons(memberId);

                // then
                assertThat(coupon.getIssuedQuantity()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Describe: findEventCouponBoards() 메서드는")
    class Describe_findEventCouponBoards {

        @Nested
        @DisplayName("Context: 진행 중인 이벤트 쿠폰보드가 있는 경우")
        class Context_with_open_coupon_board {

            @Test
            @DisplayName("It: 이벤트 쿠폰보드 목록을 반환")
            void It_이벤트_쿠폰보드_목록을_반환() {
                // given
                LocalDateTime startAt = LocalDateTime.now().minusDays(1);
                LocalDateTime endAt = LocalDateTime.now().plusDays(7);
                CouponBoard board = couponBoard(1L, "여름 쿠폰보드", "여름 쿠폰보드 내용",
                        "https://image.test/summer.png", startAt, endAt);

                when(couponBoardRepository.findOpenBoards(any(LocalDateTime.class)))
                        .thenReturn(List.of(board));

                // when
                EventCouponBoardListResponse response = couponService.findEventCouponBoards();

                // then
                assertThat(response.boards()).hasSize(1);
                assertThat(response.boards().get(0).id()).isEqualTo(1L);
                assertThat(response.boards().get(0).title()).isEqualTo("여름 쿠폰보드");
                assertThat(response.boards().get(0).thumbnailUrl()).isEqualTo("https://image.test/summer.png");
                assertThat(response.boards().get(0).startAt()).isEqualTo(startAt);
                assertThat(response.boards().get(0).endAt()).isEqualTo(endAt);
            }
        }
    }

    @Nested
    @DisplayName("Describe: findEventCouponDetail() 메서드는")
    class Describe_findEventCouponDetail {

        @Nested
        @DisplayName("Context: 로그인 사용자가 이벤트 쿠폰보드 상세를 조회하는 경우")
        class Context_with_logged_in_member {

            @Test
            @DisplayName("It: 쿠폰보드 상세와 쿠폰 목록을 반환하고 이미 받은 쿠폰 여부를 계산")
            void It_쿠폰보드_상세와_쿠폰_목록을_반환하고_이미_받은_쿠폰_여부를_계산() {
                // given
                Long memberId = 1L;
                Long boardId = 1L;
                LocalDateTime startAt = LocalDateTime.now().minusDays(1);
                LocalDateTime endAt = LocalDateTime.now().plusDays(7);
                CouponBoard board = couponBoard(boardId, "여름 쿠폰보드", "여름 쿠폰보드 내용",
                        "https://image.test/summer.png", startAt, endAt);
                Coupon downloadedCoupon = eventCoupon(10L, 10, 2, true);
                Coupon notDownloadedCoupon = eventCoupon(11L, 10, 3, true);
                UserCoupon userCoupon = userCoupon(100L, memberId, downloadedCoupon.getId());

                when(couponBoardRepository.findOpenBoardById(eq(boardId), any(LocalDateTime.class)))
                        .thenReturn(Optional.of(board));
                when(couponRepository.findByCouponBoardIdAndIssueTypeAndIsActiveTrueAndDeletedAtIsNull(
                        boardId,
                        IssueType.EVENT
                )).thenReturn(List.of(downloadedCoupon, notDownloadedCoupon));
                when(userCouponRepository.findByMemberIdAndCouponIdIn(eq(memberId), any()))
                        .thenReturn(List.of(userCoupon));

                // when
                EventCouponDetailResponse response = couponService.findEventCouponDetail(memberId, boardId);

                // then
                assertThat(response.couponBoard().id()).isEqualTo(boardId);
                assertThat(response.couponBoard().title()).isEqualTo("여름 쿠폰보드");
                assertThat(response.couponBoard().content()).isEqualTo("여름 쿠폰보드 내용");
                assertThat(response.couponBoard().thumbnailUrl()).isEqualTo("https://image.test/summer.png");

                assertThat(response.coupons()).hasSize(2);
                assertThat(response.coupons().get(0).alreadyDownloaded()).isTrue();
                assertThat(response.coupons().get(0).remainingQuantity()).isEqualTo(8);
                assertThat(response.coupons().get(0).state()).isEqualTo("open");
                assertThat(response.coupons().get(1).alreadyDownloaded()).isFalse();
                assertThat(response.coupons().get(1).remainingQuantity()).isEqualTo(7);
                assertThat(response.coupons().get(1).state()).isEqualTo("open");
            }
        }
    }

    @Nested
    @DisplayName("Describe: findMyCoupons() 메서드는")
    class Describe_findMyCoupons {

        @Nested
        @DisplayName("Context: 회원이 보유한 쿠폰이 있는 경우")
        class Context_with_member_coupons {

            @Test
            @DisplayName("It: 내 쿠폰 목록을 반환")
            void It_내_쿠폰_목록을_반환() {
                // given
                Long memberId = 1L;
                Long couponId = 10L;
                UserCoupon userCoupon = userCoupon(100L, memberId, couponId);
                Coupon coupon = signinCoupon(couponId, 10, 1);

                when(userCouponRepository.findByMemberId(memberId)).thenReturn(List.of(userCoupon));
                when(couponRepository.findAllById(any())).thenReturn(List.of(coupon));

                // when
                MyCouponListResponse response = couponService.findMyCoupons(memberId, UserCouponStatus.ACTIVE);

                // then
                assertThat(response.coupons()).hasSize(1);
                assertThat(response.coupons().get(0).userCouponId()).isEqualTo(100L);
                assertThat(response.coupons().get(0).couponId()).isEqualTo(couponId);
                assertThat(response.coupons().get(0).name()).isEqualTo("회원가입 쿠폰");
                assertThat(response.coupons().get(0).discountType()).isEqualTo(DiscountType.FIXED);
                assertThat(response.coupons().get(0).discountValue()).isEqualTo(1000L);
                assertThat(response.coupons().get(0).issueType()).isEqualTo(IssueType.SIGNIN);
                assertThat(response.coupons().get(0).status()).isEqualTo(UserCouponStatus.ACTIVE);
                assertThat(response.coupons().get(0).issuedAt()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Describe: resolveCouponName() 메서드는")
    class Describe_resolveCouponName {

        @Nested
        @DisplayName("Context: userCouponId에 해당하는 쿠폰이 존재하는 경우")
        class Context_with_existing_user_coupon {

            @Test
            @DisplayName("It: 쿠폰명을 반환한다")
            void It_쿠폰명을_반환한다() {
                // given
                Long userCouponId = 100L;
                Long couponId = 10L;
                UserCoupon userCoupon = userCoupon(userCouponId, 1L, couponId);
                Coupon coupon = fixedCoupon(couponId, 3000L, 10000L);

                when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.of(userCoupon));
                when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));

                // when
                String result = couponService.resolveCouponName(userCouponId);

                // then
                assertThat(result).isEqualTo("정액 쿠폰");
            }
        }
    }

    @Nested
    @DisplayName("Describe: download() 메서드는")
    class Describe_download {

        @Nested
        @DisplayName("Context: 다운로드 가능한 이벤트 쿠폰인 경우")
        class Context_with_downloadable_event_coupon {

            @Test
            @DisplayName("It: 쿠폰을 발급하고 응답을 반환")
            void It_쿠폰을_발급하고_응답을_반환() {
                // given
                Long memberId = 1L;
                Long couponId = 10L;
                Coupon coupon = eventCoupon(couponId, 10, 0, true);
                UserCoupon savedUserCoupon = userCoupon(100L, memberId, couponId);

                when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));
                when(userCouponRepository.existsByMemberIdAndCouponId(memberId, couponId)).thenReturn(false);
                when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(savedUserCoupon);

                // when
                CouponDownloadResponse response = couponService.download(memberId, couponId);

                // then
                assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
                assertThat(response.couponId()).isEqualTo(couponId);
                assertThat(response.name()).isEqualTo("이벤트 쿠폰");
                assertThat(response.status()).isEqualTo(UserCouponStatus.ACTIVE);
            }
        }

        @Nested
        @DisplayName("Context: 이미 받은 쿠폰인 경우")
        class Context_with_already_downloaded_coupon {

            @Test
            @DisplayName("It: COUPON_ALREADY_ISSUED 예외 발생")
            void It_COUPON_ALREADY_ISSUED_예외_발생() {
                // given
                Long memberId = 1L;
                Long couponId = 10L;
                Coupon coupon = eventCoupon(couponId, 10, 0, true);

                when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));
                when(userCouponRepository.existsByMemberIdAndCouponId(memberId, couponId)).thenReturn(true);

                // when & then
                assertThatThrownBy(() -> couponService.download(memberId, couponId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.COUPON_ALREADY_ISSUED.getMessage());

                assertThat(coupon.getIssuedQuantity()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("Context: 비활성 쿠폰인 경우")
        class Context_with_inactive_coupon {

            @Test
            @DisplayName("It: COUPON_NOT_ACTIVE 예외 발생")
            void It_COUPON_NOT_ACTIVE_예외_발생() {
                // given
                Long memberId = 1L;
                Long couponId = 10L;
                Coupon coupon = eventCoupon(couponId, 10, 0, false);

                when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));

                // when & then
                assertThatThrownBy(() -> couponService.download(memberId, couponId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.COUPON_NOT_ACTIVE.getMessage());

                assertThat(coupon.getIssuedQuantity()).isZero();
            }
        }

        @Nested
        @DisplayName("Context: 쿠폰 수량이 소진된 경우")
        class Context_with_sold_out_coupon {

            @Test
            @DisplayName("It: COUPON_SOLD_OUT 예외 발생")
            void It_COUPON_SOLD_OUT_예외_발생() {
                // given
                Long memberId = 1L;
                Long couponId = 10L;
                Coupon coupon = eventCoupon(couponId, 1, 1, true);

                when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));

                // when & then
                assertThatThrownBy(() -> couponService.download(memberId, couponId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.COUPON_SOLD_OUT.getMessage());

                assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("Describe: calculateDiscountForOrder() 메서드는")
    class Describe_calculateDiscountForOrder {

        @Nested
        @DisplayName("Context: 사용 가능한 정액 쿠폰인 경우")
        class Context_with_available_fixed_coupon {

            @Test
            @DisplayName("It: 주문 할인 금액 계산")
            void It_주문_할인_금액_계산() {
                // given
                Long memberId = 1L;
                Long userCouponId = 100L;
                Long couponId = 10L;
                UserCoupon userCoupon = userCoupon(userCouponId, memberId, couponId);
                Coupon coupon = fixedCoupon(couponId, 3000L, 10000L);

                when(userCouponRepository.findByIdAndMemberId(userCouponId, memberId))
                        .thenReturn(Optional.of(userCoupon));
                when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
                        .thenReturn(Optional.of(coupon));

                // when
                CouponDiscountResult result =
                        couponService.calculateDiscountForOrder(memberId, userCouponId, 20000L);

                // then
                assertThat(result.userCouponId()).isEqualTo(userCouponId);
                assertThat(result.couponId()).isEqualTo(couponId);
                assertThat(result.couponName()).isEqualTo("정액 쿠폰");
                assertThat(result.discountAmount()).isEqualTo(3000L);
                assertThat(result.finalAmount()).isEqualTo(17000L);
            }
        }
    }

    @Nested
    @DisplayName("Describe: useCouponForOrder() 메서드는")
    class Describe_useCouponForOrder {

        @Nested
        @DisplayName("Context: 사용 가능한 쿠폰인 경우")
        class Context_with_available_coupon {

            @Test
            @DisplayName("It: 할인 계산 후 UserCoupon을 USED로 변경")
            void It_할인_계산_후_UserCoupon을_USED로_변경() {
                // given
                Long memberId = 1L;
                Long userCouponId = 100L;
                Long couponId = 10L;
                UserCoupon userCoupon = userCoupon(userCouponId, memberId, couponId);
                Coupon coupon = fixedCoupon(couponId, 5000L, 10000L);

                when(userCouponRepository.findByIdAndMemberIdForUpdate(userCouponId, memberId))
                        .thenReturn(Optional.of(userCoupon));
                when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
                        .thenReturn(Optional.of(coupon));

                // when
                CouponDiscountResult result =
                        couponService.useCouponForOrder(memberId, userCouponId, 20000L);

                // then
                assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
                assertThat(userCoupon.getUsedAt()).isNotNull();
                assertThat(result.discountAmount()).isEqualTo(5000L);
                assertThat(result.finalAmount()).isEqualTo(15000L);
            }
        }
    }

    private Coupon eventCoupon(Long id, Integer totalQuantity, Integer issuedQuantity, Boolean isActive) {
        Coupon coupon = Coupon.builder()
                .name("이벤트 쿠폰")
                .discountType(DiscountType.FIXED)
                .discountValue(1000L)
                .minOrderPrice(0L)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .totalQuantity(totalQuantity)
                .issueType(IssueType.EVENT)
                .couponBoardId(1L)
                .isActive(isActive)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        ReflectionTestUtils.setField(coupon, "issuedQuantity", issuedQuantity);
        return coupon;
    }

    private Coupon signinCoupon(Long id, Integer totalQuantity, Integer issuedQuantity) {
        Coupon coupon = Coupon.builder()
                .name("회원가입 쿠폰")
                .discountType(DiscountType.FIXED)
                .discountValue(1000L)
                .minOrderPrice(0L)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .totalQuantity(totalQuantity)
                .issueType(IssueType.SIGNIN)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        ReflectionTestUtils.setField(coupon, "issuedQuantity", issuedQuantity);
        return coupon;
    }

    private Coupon fixedCoupon(Long id, Long discountValue, Long minOrderPrice) {
        Coupon coupon = Coupon.builder()
                .name("정액 쿠폰")
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .minOrderPrice(minOrderPrice)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .totalQuantity(10)
                .issueType(IssueType.EVENT)
                .couponBoardId(1L)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        return coupon;
    }

    private CouponBoard couponBoard(Long id,
                                    String title,
                                    String content,
                                    String thumbnailUrl,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt) {
        CouponBoard board = CouponBoard.builder()
                .title(title)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .startAt(startAt)
                .endAt(endAt)
                .build();
        ReflectionTestUtils.setField(board, "id", id);
        return board;
    }

    private UserCoupon userCoupon(Long id, Long memberId, Long couponId) {
        UserCoupon userCoupon = UserCoupon.builder()
                .memberId(memberId)
                .couponId(couponId)
                .build();
        ReflectionTestUtils.setField(userCoupon, "id", id);
        return userCoupon;
    }
}
