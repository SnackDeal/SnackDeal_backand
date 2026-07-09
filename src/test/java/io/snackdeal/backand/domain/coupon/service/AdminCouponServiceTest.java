package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardUpdateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusUpdateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponSummaryResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponUpdateRequest;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminCouponService 클래스의")
class AdminCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CouponBoardRepository couponBoardRepository;

    @InjectMocks
    private AdminCouponService adminCouponService;

    @Nested
    @DisplayName("Describe: findList() 메서드는")
    class Describe_findList {

        @Nested
        @DisplayName("Context: 관리자 전체 쿠폰 목록을 조회하는 경우")
        class Context_with_all_coupons {

            @Test
            @DisplayName("It: 쿠폰 목록과 페이지 정보를 반환한다")
            void It_쿠폰_목록과_페이지_정보를_반환() {
                // given
                Long boardId = 20L;
                Coupon coupon = eventCoupon(10L, "여름 이벤트 쿠폰", 1000L, boardId);
                Coupon signinCoupon = signinCoupon(11L, "신규가입 쿠폰", 0, 5);
                CouponBoard board = couponBoard(boardId, "여름 쿠폰보드");

                when(couponRepository.searchAdminCoupons(
                        isNull(),
                        isNull(),
                        eq(CouponStatus.ACTIVE.name()),
                        any(LocalDateTime.class),
                        any()
                )).thenReturn(new PageImpl<>(List.of(coupon, signinCoupon)));
                when(couponBoardRepository.findAllById(any())).thenReturn(List.of(board));
                when(userCouponRepository.countByCouponIdAndStatus(coupon.getId(), UserCouponStatus.USED))
                        .thenReturn(2L);
                when(userCouponRepository.countByCouponIdAndStatus(signinCoupon.getId(), UserCouponStatus.USED))
                        .thenReturn(1L);

                // when
                AdminCouponListResponse response =
                        adminCouponService.findList(null, null, CouponStatus.ACTIVE, 1, 10);

                // then
                assertThat(response.coupons()).hasSize(2);
                assertThat(response.page()).isEqualTo(1);
                assertThat(response.size()).isEqualTo(10);
                assertThat(response.total()).isEqualTo(2);

                AdminCouponSummaryResponse firstCoupon = response.coupons().get(0);
                assertThat(firstCoupon.id()).isEqualTo(10L);
                assertThat(firstCoupon.name()).isEqualTo("여름 이벤트 쿠폰");
                assertThat(firstCoupon.discountType()).isEqualTo(DiscountType.FIXED);
                assertThat(firstCoupon.discountValue()).isEqualTo(1000L);
                assertThat(firstCoupon.couponBoardId()).isEqualTo(boardId);
                assertThat(firstCoupon.couponBoardTitle()).isEqualTo("여름 쿠폰보드");
                assertThat(firstCoupon.usedCount()).isEqualTo(2L);
                assertThat(firstCoupon.isActive()).isTrue();
                assertThat(firstCoupon.status()).isEqualTo(CouponStatus.ACTIVE);

                AdminCouponSummaryResponse secondCoupon = response.coupons().get(1);
                assertThat(secondCoupon.id()).isEqualTo(11L);
                assertThat(secondCoupon.name()).isEqualTo("신규가입 쿠폰");
                assertThat(secondCoupon.couponBoardId()).isNull();
                assertThat(secondCoupon.couponBoardTitle()).isNull();
                assertThat(secondCoupon.usedCount()).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("Context: 관리자 쿠폰 목록 조회 조건이 주어진 경우")
        class Context_with_search_condition {

            @Test
            @DisplayName("It: 검색 조건을 정규화해서 repository에 전달한다")
            void It_검색_조건을_정규화해서_repository에_전달한다() {
                // given
                when(couponRepository.searchAdminCoupons(
                        eq("여름"),
                        eq(IssueType.EVENT),
                        eq(CouponStatus.ACTIVE.name()),
                        any(LocalDateTime.class),
                        any()
                )).thenReturn(new PageImpl<>(List.of()));

                // when
                AdminCouponListResponse response =
                        adminCouponService.findList(" 여름 ", IssueType.EVENT, CouponStatus.ACTIVE, 1, 10);

                // then
                assertThat(response.coupons()).isEmpty();
                assertThat(response.page()).isEqualTo(1);
                assertThat(response.size()).isEqualTo(10);
                assertThat(response.total()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Describe: save() 메서드는")
    class Describe_save {

        @Nested
        @DisplayName("Context: 유효한 이벤트 쿠폰 생성 요청인 경우")
        class Context_with_valid_event_coupon_request {

            @Test
            @DisplayName("It: 쿠폰을 생성하고 생성 응답을 반환")
            void It_쿠폰을_생성하고_생성_응답을_반환() {
                // given
                Long boardId = 20L;
                AdminCouponCreateRequest request = eventCouponCreateRequest(boardId);
                CouponBoard board = couponBoard(boardId, "이벤트 쿠폰보드");
                Coupon savedCoupon = eventCoupon(10L, "이벤트 쿠폰", 1000L, boardId);

                when(couponBoardRepository.findByIdAndDeletedAtIsNull(boardId)).thenReturn(Optional.of(board));
                when(couponRepository.save(any(Coupon.class))).thenReturn(savedCoupon);

                // when
                AdminCouponCreateResponse response = adminCouponService.save(request);

                // then
                assertThat(response.id()).isEqualTo(10L);
                assertThat(response.name()).isEqualTo("이벤트 쿠폰");
                assertThat(response.isActive()).isTrue();
                assertThat(response.status()).isEqualTo(CouponStatus.ACTIVE);
                assertThat(response.couponBoardId()).isEqualTo(boardId);
                assertThat(response.createdAt()).isNotNull();
            }
        }

        @Nested
        @DisplayName("Context: 이벤트 쿠폰인데 couponBoardId가 없는 경우")
        class Context_with_event_coupon_without_board_id {

            @Test
            @DisplayName("It: INVALID_COUPON_POLICY 예외 발생")
            void It_INVALID_COUPON_POLICY_예외_발생() {
                // given
                AdminCouponCreateRequest request = eventCouponCreateRequest(null);

                // when & then
                assertThatThrownBy(() -> adminCouponService.save(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("이벤트 쿠폰은 couponBoardId가 필수입니다.");
            }
        }

        @Nested
        @DisplayName("Context: 정률 쿠폰의 할인값이 100을 초과한 경우")
        class Context_with_invalid_percent_discount_value {

            @Test
            @DisplayName("It: INVALID_COUPON_POLICY 예외 발생")
            void It_INVALID_COUPON_POLICY_예외_발생() {
                // given
                AdminCouponCreateRequest request = percentCouponCreateRequest(101L);

                // when & then
                assertThatThrownBy(() -> adminCouponService.save(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("정률 할인값은 1~100 사이");
            }
        }
    }

    @Nested
    @DisplayName("Describe: update() 메서드는")
    class Describe_update {

        @Nested
        @DisplayName("Context: 쿠폰명을 수정하는 경우")
        class Context_with_name_update_request {

            @Test
            @DisplayName("It: 쿠폰명을 수정하고 요약 응답을 반환")
            void It_쿠폰명을_수정하고_요약_응답을_반환() {
                // given
                Long couponId = 10L;
                Coupon coupon = signinCoupon(couponId, "기존 쿠폰", 10, 0);
                AdminCouponUpdateRequest request = couponUpdateRequest("수정된 쿠폰", null, null);

                when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));
                when(userCouponRepository.countByCouponIdAndStatus(couponId, UserCouponStatus.USED))
                        .thenReturn(0L);

                // when
                AdminCouponSummaryResponse response = adminCouponService.update(couponId, request);

                // then
                assertThat(coupon.getName()).isEqualTo("수정된 쿠폰");
                assertThat(response.id()).isEqualTo(couponId);
                assertThat(response.name()).isEqualTo("수정된 쿠폰");
                assertThat(response.couponBoardId()).isNull();
                assertThat(response.couponBoardTitle()).isNull();
                assertThat(response.status()).isEqualTo(CouponStatus.ACTIVE);
            }
        }

        @Nested
        @DisplayName("Context: 발급 수량을 기존 수량보다 감소시키는 경우")
        class Context_with_total_quantity_decrease {

            @Test
            @DisplayName("It: VALIDATION_FAILED 예외 발생")
            void It_VALIDATION_FAILED_예외_발생() {
                // given
                Long couponId = 10L;
                Coupon coupon = signinCoupon(couponId, "수량 제한 쿠폰", 10, 0);
                AdminCouponUpdateRequest request = couponUpdateRequest(null, null, 5);

                when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));

                // when & then
                assertThatThrownBy(() -> adminCouponService.update(couponId, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining("발급 수량은 감소시킬 수 없습니다");
            }
        }
    }

    @Nested
    @DisplayName("Describe: changeStatus() 메서드는")
    class Describe_changeStatus {

        @Nested
        @DisplayName("Context: 쿠폰 활성 상태를 비활성으로 변경하는 경우")
        class Context_with_inactive_status_request {

            @Test
            @DisplayName("It: 변경된 활성 상태와 상태 응답을 반환")
            void It_변경된_활성_상태와_상태_응답을_반환() {
                // given
                Long couponId = 10L;
                Coupon coupon = signinCoupon(couponId, "회원가입 쿠폰", 10, 0);
                AdminCouponStatusUpdateRequest request = couponStatusUpdateRequest(false);

                when(couponRepository.findByIdAndDeletedAtIsNull(couponId)).thenReturn(Optional.of(coupon));

                // when
                AdminCouponStatusResponse response = adminCouponService.changeStatus(couponId, request);

                // then
                assertThat(coupon.isActive()).isFalse();
                assertThat(response.id()).isEqualTo(couponId);
                assertThat(response.isActive()).isFalse();
                assertThat(response.status()).isEqualTo(CouponStatus.STOPPED);
                assertThat(response.updatedAt()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Describe: findBoardList() 메서드는")
    class Describe_findBoardList {

        @Nested
        @DisplayName("Context: 등록된 쿠폰보드가 있는 경우")
        class Context_with_coupon_boards {

            @Test
            @DisplayName("It: 쿠폰보드 목록을 반환한다")
            void It_쿠폰보드_목록을_반환한다() {
                // given
                LocalDateTime startAt = LocalDateTime.now().minusDays(1);
                LocalDateTime endAt = LocalDateTime.now().plusDays(7);
                CouponBoard board = couponBoard(
                        20L,
                        "여름 쿠폰보드",
                        "여름 쿠폰보드 내용",
                        "https://image.test/summer-board.png",
                        true,
                        startAt,
                        endAt
                );

                when(couponBoardRepository.findByDeletedAtIsNull(any()))
                        .thenReturn(List.of(board));

                // when
                AdminCouponBoardListResponse response = adminCouponService.findBoardList();

                // then
                assertThat(response.boards()).hasSize(1);

                AdminCouponBoardResponse firstBoard = response.boards().get(0);
                assertThat(firstBoard.id()).isEqualTo(20L);
                assertThat(firstBoard.title()).isEqualTo("여름 쿠폰보드");
                assertThat(firstBoard.content()).isEqualTo("여름 쿠폰보드 내용");
                assertThat(firstBoard.thumbnailUrl()).isEqualTo("https://image.test/summer-board.png");
                assertThat(firstBoard.isActive()).isTrue();
                assertThat(firstBoard.startAt()).isEqualTo(startAt);
                assertThat(firstBoard.endAt()).isEqualTo(endAt);
                assertThat(firstBoard.createdAt()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Describe: saveBoard() 메서드는")
    class Describe_saveBoard {

        @Nested
        @DisplayName("Context: 유효한 쿠폰보드 생성 요청인 경우")
        class Context_with_valid_board_request {

            @Test
            @DisplayName("It: 쿠폰보드를 생성하고 응답을 반환")
            void It_쿠폰보드를_생성하고_응답을_반환() {
                // given
                LocalDateTime startAt = LocalDateTime.now().plusDays(1);
                LocalDateTime endAt = startAt.plusDays(7);
                AdminCouponBoardCreateRequest request = couponBoardCreateRequest(startAt, endAt);
                CouponBoard savedBoard = couponBoard(20L, "신규 쿠폰보드", "신규 쿠폰보드 내용",
                        "https://image.test/new-board.png", true, startAt, endAt);

                when(couponBoardRepository.save(any(CouponBoard.class))).thenReturn(savedBoard);

                // when
                AdminCouponBoardResponse response = adminCouponService.saveBoard(request);

                // then
                assertThat(response.id()).isEqualTo(20L);
                assertThat(response.title()).isEqualTo("신규 쿠폰보드");
                assertThat(response.content()).isEqualTo("신규 쿠폰보드 내용");
                assertThat(response.thumbnailUrl()).isEqualTo("https://image.test/new-board.png");
                assertThat(response.isActive()).isTrue();
                assertThat(response.startAt()).isEqualTo(startAt);
                assertThat(response.endAt()).isEqualTo(endAt);
                assertThat(response.createdAt()).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Describe: updateBoard() 메서드는")
    class Describe_updateBoard {

        @Nested
        @DisplayName("Context: 유효한 쿠폰보드 수정 요청인 경우")
        class Context_with_valid_board_update_request {

            @Test
            @DisplayName("It: 쿠폰보드를 수정하고 응답을 반환")
            void It_쿠폰보드를_수정하고_응답을_반환() {
                // given
                Long boardId = 20L;
                LocalDateTime oldStartAt = LocalDateTime.now().plusDays(1);
                LocalDateTime oldEndAt = oldStartAt.plusDays(7);
                CouponBoard board = couponBoard(boardId, "기존 쿠폰보드", "기존 내용",
                        "https://image.test/old-board.png", true, oldStartAt, oldEndAt);

                LocalDateTime newStartAt = LocalDateTime.now().plusDays(2);
                LocalDateTime newEndAt = newStartAt.plusDays(10);
                AdminCouponBoardUpdateRequest request = couponBoardUpdateRequest(newStartAt, newEndAt);

                when(couponBoardRepository.findByIdAndDeletedAtIsNull(boardId)).thenReturn(Optional.of(board));

                // when
                AdminCouponBoardResponse response = adminCouponService.updateBoard(boardId, request);

                // then
                assertThat(board.getTitle()).isEqualTo("수정된 쿠폰보드");
                assertThat(board.getContent()).isEqualTo("수정된 쿠폰보드 내용");
                assertThat(board.getThumbnailUrl()).isEqualTo("https://image.test/updated-board.png");
                assertThat(board.isActive()).isFalse();
                assertThat(response.id()).isEqualTo(boardId);
                assertThat(response.title()).isEqualTo("수정된 쿠폰보드");
                assertThat(response.content()).isEqualTo("수정된 쿠폰보드 내용");
                assertThat(response.thumbnailUrl()).isEqualTo("https://image.test/updated-board.png");
                assertThat(response.isActive()).isFalse();
                assertThat(response.startAt()).isEqualTo(newStartAt);
                assertThat(response.endAt()).isEqualTo(newEndAt);
            }
        }
    }

    @Nested
    @DisplayName("Describe: deleteBoard() 메서드는")
    class Describe_deleteBoard {

        @Nested
        @DisplayName("Context: 소속 쿠폰이 없는 쿠폰보드인 경우")
        class Context_with_board_without_coupons {

            @Test
            @DisplayName("It: 쿠폰보드를 삭제한다")
            void It_쿠폰보드를_삭제한다() {
                // given
                Long boardId = 20L;
                CouponBoard board = couponBoard(boardId, "삭제 대상 쿠폰보드");

                when(couponBoardRepository.findByIdAndDeletedAtIsNull(boardId)).thenReturn(Optional.of(board));
                when(couponRepository.existsByCouponBoardIdAndDeletedAtIsNull(boardId)).thenReturn(false);

                // when
                adminCouponService.deleteBoard(boardId);

                // then
                assertThat(board.isActive()).isFalse();
                assertThat(board.getDeletedAt()).isNotNull();
            }
        }

        @Nested
        @DisplayName("Context: 소속 쿠폰이 있는 쿠폰보드인 경우")
        class Context_with_board_having_coupons {

            @Test
            @DisplayName("It: COUPON_BOARD_HAS_COUPONS 예외 발생")
            void It_COUPON_BOARD_HAS_COUPONS_예외_발생() {
                // given
                Long boardId = 20L;
                CouponBoard board = couponBoard(boardId, "쿠폰이 있는 쿠폰보드");

                when(couponBoardRepository.findByIdAndDeletedAtIsNull(boardId)).thenReturn(Optional.of(board));
                when(couponRepository.existsByCouponBoardIdAndDeletedAtIsNull(boardId)).thenReturn(true);

                // when & then
                assertThatThrownBy(() -> adminCouponService.deleteBoard(boardId))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.COUPON_BOARD_HAS_COUPONS.getMessage());
            }
        }
    }

    private AdminCouponCreateRequest eventCouponCreateRequest(Long couponBoardId) {
        return new AdminCouponCreateRequest(
                "이벤트 쿠폰",
                DiscountType.FIXED,
                1000L,
                0L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(7),
                100,
                IssueType.EVENT,
                couponBoardId,
                true
        );
    }

    private AdminCouponCreateRequest percentCouponCreateRequest(Long discountValue) {
        return new AdminCouponCreateRequest(
                "정률 쿠폰",
                DiscountType.PERCENT,
                discountValue,
                0L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(7),
                100,
                IssueType.SIGNIN,
                null,
                true
        );
    }

    private AdminCouponUpdateRequest couponUpdateRequest(String name,
                                                         LocalDateTime validUntil,
                                                         Integer totalQuantity) {
        return new AdminCouponUpdateRequest(name, validUntil, totalQuantity);
    }

    private AdminCouponStatusUpdateRequest couponStatusUpdateRequest(boolean isActive) {
        return new AdminCouponStatusUpdateRequest(isActive);
    }

    private AdminCouponBoardCreateRequest couponBoardCreateRequest(LocalDateTime startAt, LocalDateTime endAt) {
        return new AdminCouponBoardCreateRequest(
                "신규 쿠폰보드",
                "신규 쿠폰보드 내용",
                "https://image.test/new-board.png",
                true,
                startAt,
                endAt
        );
    }

    private AdminCouponBoardUpdateRequest couponBoardUpdateRequest(LocalDateTime startAt, LocalDateTime endAt) {
        return new AdminCouponBoardUpdateRequest(
                "수정된 쿠폰보드",
                "수정된 쿠폰보드 내용",
                "https://image.test/updated-board.png",
                false,
                startAt,
                endAt
        );
    }

    private Coupon eventCoupon(Long id, String name, Long discountValue, Long couponBoardId) {
        Coupon coupon = Coupon.builder()
                .name(name)
                .discountType(DiscountType.FIXED)
                .discountValue(discountValue)
                .minOrderPrice(0L)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(7))
                .totalQuantity(100)
                .issueType(IssueType.EVENT)
                .couponBoardId(couponBoardId)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        ReflectionTestUtils.setField(coupon, "issuedQuantity", 10);
        return coupon;
    }

    private Coupon signinCoupon(Long id, String name, Integer totalQuantity, Integer issuedQuantity) {
        Coupon coupon = Coupon.builder()
                .name(name)
                .discountType(DiscountType.FIXED)
                .discountValue(1000L)
                .minOrderPrice(0L)
                .validFrom(LocalDateTime.now().minusDays(1))
                .validUntil(LocalDateTime.now().plusDays(7))
                .totalQuantity(totalQuantity)
                .issueType(IssueType.SIGNIN)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(coupon, "id", id);
        ReflectionTestUtils.setField(coupon, "issuedQuantity", issuedQuantity);
        return coupon;
    }

    private CouponBoard couponBoard(Long id, String title) {
        return couponBoard(
                id,
                title,
                "쿠폰보드 내용",
                "https://image.test/board.png",
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(7)
        );
    }

    private CouponBoard couponBoard(Long id,
                                    String title,
                                    String content,
                                    String thumbnailUrl,
                                    boolean isActive,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt) {
        CouponBoard board = CouponBoard.builder()
                .title(title)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .isActive(isActive)
                .startAt(startAt)
                .endAt(endAt)
                .build();
        ReflectionTestUtils.setField(board, "id", id);
        return board;
    }
}
