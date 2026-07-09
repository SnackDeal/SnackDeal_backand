package io.snackdeal.backand.api.user.coupon.controller;

import io.snackdeal.backand.api.user.coupon.dto.CouponDownloadResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardListResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardSummaryResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponListResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.service.CouponService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-config.properties"
)
@DisplayName("CouponController 클래스의")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @Nested
    @DisplayName("Describe: eventCouponList() 메서드는")
    class Describe_eventCouponList {

        @Nested
        @DisplayName("Context: 이벤트 쿠폰보드 목록 조회를 요청한 경우")
        class Context_with_event_coupon_board_list {

            @Test
            @DisplayName("It: 이벤트 쿠폰보드 목록 반환")
            void It_이벤트_쿠폰보드_목록_반환() throws Exception {
                // given
                given(couponService.findEventCouponBoards())
                        .willReturn(createEventCouponBoardListResponse());

                // when / then
                mockMvc.perform(get("/event/coupon/list"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.boards[0].id").value(1))
                        .andExpect(jsonPath("$.data.boards[0].title").value("여름 쿠폰 이벤트"))
                        .andExpect(jsonPath("$.data.boards[0].thumbnailUrl").value("https://image.test/coupon-board.png"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: eventCouponDetail() 메서드는")
    class Describe_eventCouponDetail {

        @Nested
        @DisplayName("Context: 로그인 사용자가 이벤트 쿠폰보드 상세 조회를 요청한 경우")
        class Context_with_login_member {

            @Test
            @DisplayName("It: 쿠폰보드 상세와 쿠폰 목록 반환")
            void It_쿠폰보드_상세와_쿠폰_목록_반환() throws Exception {
                // given
                Long memberId = 1L;
                Long boardId = 10L;

                given(couponService.findEventCouponDetail(eq(memberId), eq(boardId)))
                        .willReturn(createEventCouponDetailResponse());

                // when / then
                mockMvc.perform(get("/event/coupon-board/{boardId}", boardId)
                                .with(as(MemberRole.USER)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.couponBoard.id").value(10))
                        .andExpect(jsonPath("$.data.couponBoard.title").value("여름 쿠폰 이벤트"))
                        .andExpect(jsonPath("$.data.coupons[0].id").value(100))
                        .andExpect(jsonPath("$.data.coupons[0].name").value("이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.coupons[0].alreadyDownloaded").value(true))
                        .andExpect(jsonPath("$.data.coupons[0].state").value("open"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: download() 메서드는")
    class Describe_download {

        @Nested
        @DisplayName("Context: 로그인 사용자가 이벤트 쿠폰 다운로드를 요청한 경우")
        class Context_with_login_member {

            @Test
            @DisplayName("It: 쿠폰을 다운로드하고 생성 응답을 반환")
            void It_쿠폰을_다운로드하고_생성_응답을_반환() throws Exception {
                // given
                Long memberId = 1L;
                Long couponId = 100L;

                given(couponService.download(eq(memberId), eq(couponId)))
                        .willReturn(createCouponDownloadResponse());

                // when / then
                mockMvc.perform(post("/event/coupon/{couponId}/download", couponId)
                                .with(as(MemberRole.USER)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S001"))
                        .andExpect(jsonPath("$.data.userCouponId").value(1000))
                        .andExpect(jsonPath("$.data.couponId").value(100))
                        .andExpect(jsonPath("$.data.name").value("이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.status").value("ACTIVE"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: myCoupons() 메서드는")
    class Describe_myCoupons {

        @Nested
        @DisplayName("Context: 로그인 사용자가 내 쿠폰 목록을 조회하는 경우")
        class Context_with_login_member {

            @Test
            @DisplayName("It: 내 쿠폰 목록 반환")
            void It_내_쿠폰_목록_반환() throws Exception {
                // given
                Long memberId = 1L;

                given(couponService.findMyCoupons(eq(memberId), eq(UserCouponStatus.ACTIVE)))
                        .willReturn(createMyCouponListResponse());

                // when / then
                mockMvc.perform(get("/mypage/coupon")
                                .with(as(MemberRole.USER))
                                .param("status", "ACTIVE"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.coupons[0].userCouponId").value(1000))
                        .andExpect(jsonPath("$.data.coupons[0].couponId").value(100))
                        .andExpect(jsonPath("$.data.coupons[0].name").value("이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.coupons[0].status").value("ACTIVE"));
            }
        }
    }

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(
                1L,
                role.name().toLowerCase() + "@test.com",
                "password",
                role
        );

        return authentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        "token",
                        principal.getAuthorities()
                )
        );
    }

    private EventCouponBoardListResponse createEventCouponBoardListResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new EventCouponBoardListResponse(List.of(
                new EventCouponBoardSummaryResponse(
                        1L,
                        "여름 쿠폰 이벤트",
                        "https://image.test/coupon-board.png",
                        now.minusDays(1),
                        now.plusDays(7)
                )
        ));
    }

    private EventCouponDetailResponse createEventCouponDetailResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);

        EventCouponBoardDetailResponse board = new EventCouponBoardDetailResponse(
                10L,
                "여름 쿠폰 이벤트",
                "여름 이벤트 쿠폰을 받을 수 있습니다.",
                "https://image.test/coupon-board.png",
                now.minusDays(1),
                now.plusDays(7)
        );

        EventCouponResponse coupon = new EventCouponResponse(
                100L,
                "이벤트 쿠폰",
                DiscountType.FIXED,
                1000L,
                0L,
                now.minusDays(1),
                now.plusDays(7),
                9,
                "open",
                true
        );

        return new EventCouponDetailResponse(board, List.of(coupon));
    }

    private CouponDownloadResponse createCouponDownloadResponse() {
        return new CouponDownloadResponse(
                1000L,
                100L,
                "이벤트 쿠폰",
                UserCouponStatus.ACTIVE,
                LocalDateTime.of(2026, 7, 9, 12, 0)
        );
    }

    private MyCouponListResponse createMyCouponListResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new MyCouponListResponse(List.of(
                new MyCouponResponse(
                        1000L,
                        100L,
                        "이벤트 쿠폰",
                        DiscountType.FIXED,
                        1000L,
                        0L,
                        now.plusDays(7),
                        IssueType.EVENT,
                        UserCouponStatus.ACTIVE,
                        now,
                        null
                )
        ));
    }
}
