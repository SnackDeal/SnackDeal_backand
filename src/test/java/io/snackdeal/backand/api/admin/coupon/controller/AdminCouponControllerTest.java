package io.snackdeal.backand.api.admin.coupon.controller;

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
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.service.AdminCouponService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-config.properties"
)
@DisplayName("AdminCouponController 클래스의")
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminCouponService adminCouponService;

    @Nested
    @DisplayName("Describe: list() 메서드는")
    class Describe_list {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰 목록 조회를 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰 목록과 페이지 정보를 반환")
            void It_쿠폰_목록과_페이지_정보를_반환() throws Exception {
                // given
                given(adminCouponService.findList(
                        eq("쿠폰"),
                        eq(IssueType.EVENT),
                        eq(CouponStatus.ACTIVE),
                        eq(1),
                        eq(10)
                )).willReturn(createCouponListResponse());

                // when / then
                mockMvc.perform(get("/admin/coupon")
                                .with(as(MemberRole.ADMIN))
                                .param("keyword", "쿠폰")
                                .param("issueType", "EVENT")
                                .param("status", "ACTIVE")
                                .param("page", "1")
                                .param("size", "10"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.coupons[0].id").value(100))
                        .andExpect(jsonPath("$.data.coupons[0].name").value("이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.coupons[0].issueType").value("EVENT"))
                        .andExpect(jsonPath("$.data.coupons[0].status").value("ACTIVE"))
                        .andExpect(jsonPath("$.data.page").value(1))
                        .andExpect(jsonPath("$.data.size").value(10))
                        .andExpect(jsonPath("$.data.total").value(1));
            }
        }
    }

    @Nested
    @DisplayName("Describe: save() 메서드는")
    class Describe_save {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰 생성을 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰을 생성하고 생성 응답을 반환")
            void It_쿠폰을_생성하고_생성_응답을_반환() throws Exception {
                // given
                AdminCouponCreateRequest request = createCouponCreateRequest();

                given(adminCouponService.save(any(AdminCouponCreateRequest.class)))
                        .willReturn(createCouponCreateResponse());

                // when / then
                mockMvc.perform(post("/admin/coupon")
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S001"))
                        .andExpect(jsonPath("$.data.id").value(100))
                        .andExpect(jsonPath("$.data.name").value("이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.isActive").value(true))
                        .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                        .andExpect(jsonPath("$.data.couponBoardId").value(10));
            }
        }
    }

    @Nested
    @DisplayName("Describe: update() 메서드는")
    class Describe_update {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰 수정을 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰을 수정하고 요약 응답을 반환")
            void It_쿠폰을_수정하고_요약_응답을_반환() throws Exception {
                // given
                AdminCouponUpdateRequest request = new AdminCouponUpdateRequest(
                        "수정 이벤트 쿠폰",
                        null,
                        null
                );

                given(adminCouponService.update(eq(100L), any(AdminCouponUpdateRequest.class)))
                        .willReturn(createUpdatedCouponSummaryResponse());

                // when / then
                mockMvc.perform(put("/admin/coupon/{id}", 100L)
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.id").value(100))
                        .andExpect(jsonPath("$.data.name").value("수정 이벤트 쿠폰"))
                        .andExpect(jsonPath("$.data.status").value("ACTIVE"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: changeStatus() 메서드는")
    class Describe_changeStatus {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰 상태 변경을 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰 활성 상태를 변경")
            void It_쿠폰_활성_상태를_변경() throws Exception {
                // given
                AdminCouponStatusUpdateRequest request = new AdminCouponStatusUpdateRequest(false);

                given(adminCouponService.changeStatus(eq(100L), any(AdminCouponStatusUpdateRequest.class)))
                        .willReturn(new AdminCouponStatusResponse(
                                100L,
                                false,
                                CouponStatus.STOPPED,
                                LocalDateTime.of(2026, 7, 9, 12, 0)
                        ));

                // when / then
                mockMvc.perform(patch("/admin/coupon/{id}/status", 100L)
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.id").value(100))
                        .andExpect(jsonPath("$.data.isActive").value(false))
                        .andExpect(jsonPath("$.data.status").value("STOPPED"));
            }
        }
    }

    @Nested
    @DisplayName("Describe: boardList() 메서드는")
    class Describe_boardList {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰보드 목록 조회를 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰보드 목록을 반환")
            void It_쿠폰보드_목록을_반환() throws Exception {
                // given
                given(adminCouponService.findBoardList())
                        .willReturn(new AdminCouponBoardListResponse(List.of(createCouponBoardResponse())));

                // when / then
                mockMvc.perform(get("/admin/coupon-board")
                                .with(as(MemberRole.ADMIN)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.boards[0].id").value(10))
                        .andExpect(jsonPath("$.data.boards[0].title").value("여름 쿠폰보드"))
                        .andExpect(jsonPath("$.data.boards[0].isActive").value(true));
            }
        }
    }

    @Nested
    @DisplayName("Describe: saveBoard() 메서드는")
    class Describe_saveBoard {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰보드 생성을 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰보드를 생성하고 응답을 반환")
            void It_쿠폰보드를_생성하고_응답을_반환() throws Exception {
                // given
                AdminCouponBoardCreateRequest request = createCouponBoardCreateRequest();

                given(adminCouponService.saveBoard(any(AdminCouponBoardCreateRequest.class)))
                        .willReturn(createCouponBoardResponse());

                // when / then
                mockMvc.perform(post("/admin/coupon-board")
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S001"))
                        .andExpect(jsonPath("$.data.id").value(10))
                        .andExpect(jsonPath("$.data.title").value("여름 쿠폰보드"))
                        .andExpect(jsonPath("$.data.isActive").value(true));
            }
        }
    }

    @Nested
    @DisplayName("Describe: updateBoard() 메서드는")
    class Describe_updateBoard {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰보드 수정을 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰보드를 수정하고 응답 반환")
            void It_쿠폰보드를_수정하고_응답_반환() throws Exception {
                // given
                AdminCouponBoardUpdateRequest request = createCouponBoardUpdateRequest();

                given(adminCouponService.updateBoard(eq(10L), any(AdminCouponBoardUpdateRequest.class)))
                        .willReturn(createUpdatedCouponBoardResponse());

                // when / then
                mockMvc.perform(put("/admin/coupon-board/{id}", 10L)
                                .with(as(MemberRole.ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data.id").value(10))
                        .andExpect(jsonPath("$.data.title").value("수정 쿠폰보드"))
                        .andExpect(jsonPath("$.data.thumbnailUrl").value("https://image.test/update-coupon-board.png"))
                        .andExpect(jsonPath("$.data.isActive").value(false));
            }
        }
    }

    @Nested
    @DisplayName("Describe: deleteBoard() 메서드는")
    class Describe_deleteBoard {

        @Nested
        @DisplayName("Context: 관리자 권한으로 쿠폰보드 삭제를 요청한 경우")
        class Context_with_admin_role {

            @Test
            @DisplayName("It: 쿠폰보드를 삭제하고 성공 응답을 반환")
            void It_쿠폰보드를_삭제하고_성공_응답을_반환() throws Exception {
                // when / then
                mockMvc.perform(delete("/admin/coupon-board/{id}", 10L)
                                .with(as(MemberRole.ADMIN)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true))
                        .andExpect(jsonPath("$.code").value("S000"))
                        .andExpect(jsonPath("$.data").isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Describe: admin API는")
    class Describe_admin_api_authorization {

        @Nested
        @DisplayName("Context: USER 권한으로 요청한 경우")
        class Context_with_user_role {

            @Test
            @DisplayName("It: 403 Forbidden을 반환")
            void It_403_Forbidden_반환() throws Exception {
                // when / then
                mockMvc.perform(get("/admin/coupon")
                                .with(as(MemberRole.USER)))
                        .andExpect(status().isForbidden());
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

    private AdminCouponListResponse createCouponListResponse() {
        return new AdminCouponListResponse(
                List.of(createCouponSummaryResponse("이벤트 쿠폰")),
                1,
                10,
                1
        );
    }

    private AdminCouponSummaryResponse createCouponSummaryResponse(String name) {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponSummaryResponse(
                100L,
                name,
                DiscountType.FIXED,
                1000L,
                0L,
                now.minusDays(1),
                now.plusDays(7),
                IssueType.EVENT,
                10L,
                "여름 쿠폰보드",
                10,
                1,
                0,
                true,
                CouponStatus.ACTIVE
        );
    }

    private AdminCouponSummaryResponse createUpdatedCouponSummaryResponse() {
        return createCouponSummaryResponse("수정 이벤트 쿠폰");
    }

    private AdminCouponCreateRequest createCouponCreateRequest() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponCreateRequest(
                "이벤트 쿠폰",
                DiscountType.FIXED,
                1000L,
                0L,
                now.minusDays(1),
                now.plusDays(7),
                10,
                IssueType.EVENT,
                10L,
                true
        );
    }

    private AdminCouponCreateResponse createCouponCreateResponse() {
        return new AdminCouponCreateResponse(
                100L,
                "이벤트 쿠폰",
                true,
                CouponStatus.ACTIVE,
                10L,
                LocalDateTime.of(2026, 7, 9, 12, 0)
        );
    }

    private AdminCouponBoardCreateRequest createCouponBoardCreateRequest() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponBoardCreateRequest(
                "여름 쿠폰보드",
                "여름 이벤트 쿠폰보드입니다.",
                "https://image.test/coupon-board.png",
                true,
                now.minusDays(1),
                now.plusDays(7)
        );
    }

    private AdminCouponBoardUpdateRequest createCouponBoardUpdateRequest() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponBoardUpdateRequest(
                "수정 쿠폰보드",
                "수정된 쿠폰보드입니다.",
                "https://image.test/update-coupon-board.png",
                false,
                now.minusDays(1),
                now.plusDays(7)
        );
    }

    private AdminCouponBoardResponse createCouponBoardResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponBoardResponse(
                10L,
                "여름 쿠폰보드",
                "여름 이벤트 쿠폰보드입니다.",
                "https://image.test/coupon-board.png",
                true,
                now.minusDays(1),
                now.plusDays(7),
                now
        );
    }

    private AdminCouponBoardResponse createUpdatedCouponBoardResponse() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 9, 12, 0);
        return new AdminCouponBoardResponse(
                10L,
                "수정 쿠폰보드",
                "수정된 쿠폰보드입니다.",
                "https://image.test/update-coupon-board.png",
                false,
                now.minusDays(1),
                now.plusDays(7),
                now
        );
    }
}
