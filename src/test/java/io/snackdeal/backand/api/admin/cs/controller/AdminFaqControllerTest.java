package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminFaqRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminFaqResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.AdminFaqService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class AdminFaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminFaqService adminFaqService;

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(1L, role.name().toLowerCase() + "@test.com", "password", role);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("list - ADMIN 권한으로 FAQ 목록 조회 성공")
    void list_Success() throws Exception {
        // given
        AdminFaqResponse response = new AdminFaqResponse(
                1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?",
                "로그인 후 마이페이지에서 확인할 수 있습니다.",
                LocalDateTime.now(), null);
        given(adminFaqService.findList(null)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/admin/cs/faq").with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("주문 내역은 어디서 확인하나요?"));
    }

    @Test
    @DisplayName("list - ADMIN 권한으로 타입별 FAQ 목록 조회 성공")
    void list_ByType_Success() throws Exception {
        // given
        AdminFaqResponse response = new AdminFaqResponse(
                1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?",
                "로그인 후 마이페이지에서 확인할 수 있습니다.",
                LocalDateTime.now(), null);
        given(adminFaqService.findList(QnaType.ORDER)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/admin/cs/faq")
                        .param("type", "ORDER")
                        .with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].type").value("ORDER"));
    }

    @Test
    @DisplayName("findById - ADMIN 권한으로 FAQ 단건 조회 성공")
    void findById_Success() throws Exception {
        // given
        Long id = 1L;
        AdminFaqResponse response = new AdminFaqResponse(
                id, QnaType.SHIPPING, "배송은 보통 얼마나 걸리나요?",
                "주문 확인 후 순차적으로 진행됩니다.",
                LocalDateTime.now(), null);
        given(adminFaqService.findById(id)).willReturn(response);

        // when / then
        mockMvc.perform(get("/admin/cs/faq/{id}", id).with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("배송은 보통 얼마나 걸리나요?"));
    }

    @Test
    @DisplayName("save - ADMIN 권한으로 FAQ 생성 성공")
    void save_Success() throws Exception {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.PRODUCT, "새 상품 FAQ", "상품 관련 FAQ 내용입니다.");
        AdminFaqResponse response = new AdminFaqResponse(
                1L, QnaType.PRODUCT, "새 상품 FAQ", "상품 관련 FAQ 내용입니다.",
                LocalDateTime.now(), null);
        given(adminFaqService.save(any(AdminFaqRequest.class))).willReturn(response);

        // when / then
        mockMvc.perform(post("/admin/cs/faq")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("새 상품 FAQ"));
    }

    @Test
    @DisplayName("save - type이 null이면 400 Bad Request")
    void save_ValidationFail_TypeNull() throws Exception {
        // given
        AdminFaqRequest request = new AdminFaqRequest(null, "제목", "내용");

        // when / then
        mockMvc.perform(post("/admin/cs/faq")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminFaqService);
    }

    @Test
    @DisplayName("save - title이 빈 문자열이면 400 Bad Request")
    void save_ValidationFail_TitleBlank() throws Exception {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.OTHER, "", "내용");

        // when / then
        mockMvc.perform(post("/admin/cs/faq")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminFaqService);
    }

    @Test
    @DisplayName("save - content가 빈 문자열이면 400 Bad Request")
    void save_ValidationFail_ContentBlank() throws Exception {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.OTHER, "제목", "");

        // when / then
        mockMvc.perform(post("/admin/cs/faq")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminFaqService);
    }

    @Test
    @DisplayName("update - ADMIN 권한으로 FAQ 수정 성공")
    void update_Success() throws Exception {
        // given
        Long id = 1L;
        AdminFaqRequest request = new AdminFaqRequest(QnaType.SHIPPING, "수정된 FAQ", "수정된 내용");
        AdminFaqResponse response = new AdminFaqResponse(
                id, QnaType.SHIPPING, "수정된 FAQ", "수정된 내용",
                LocalDateTime.now(), LocalDateTime.now());
        given(adminFaqService.update(eq(id), any(AdminFaqRequest.class))).willReturn(response);

        // when / then
        mockMvc.perform(put("/admin/cs/faq/{id}", id)
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("수정된 FAQ"));
    }

    @Test
    @DisplayName("delete - ADMIN 권한으로 FAQ 삭제 성공")
    void delete_Success() throws Exception {
        // given
        Long id = 1L;

        // when / then
        mockMvc.perform(delete("/admin/cs/faq/{id}", id).with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(adminFaqService).delete(id);
    }

    @Test
    @DisplayName("USER 권한으로 /admin/cs/faq 접근 시 403 Forbidden")
    void request_WithoutAdminRole_Forbidden() throws Exception {
        // when / then
        mockMvc.perform(get("/admin/cs/faq").with(as(MemberRole.USER)))
                .andExpect(status().isForbidden());
    }
}