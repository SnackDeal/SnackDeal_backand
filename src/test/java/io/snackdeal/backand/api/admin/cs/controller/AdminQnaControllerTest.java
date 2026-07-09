package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminQnaAnswerCreateRequest;
import io.snackdeal.backand.api.user.cs.dto.QnaResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.AdminQnaService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class AdminQnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminQnaService adminQnaService;

    private static final String ADMIN_EMAIL = "admin@test.com";

    private RequestPostProcessor asAdmin() {
        UserDetails principal = new MemberDetails(1L, ADMIN_EMAIL, "password", MemberRole.ADMIN);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    private RequestPostProcessor asUser() {
        UserDetails principal = new MemberDetails(2L, "user@test.com", "password", MemberRole.USER);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("answer - 관리자 답변 등록 성공")
    void answer_Success() throws Exception {
        QnaResponse response = new QnaResponse(1L, QnaType.ORDER, "Title", "Content", null,
                true, LocalDateTime.now(), "Answer content", LocalDateTime.now());
        given(adminQnaService.answer(eq(1L), any(AdminQnaAnswerCreateRequest.class))).willReturn(response);
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        mockMvc.perform(post("/admin/cs/qna/{id}/answer", 1L).with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answerContent").value("Answer content"));
    }

    @Test
    @DisplayName("answer - 중복 답변 시 QNA_ALREADY_ANSWERED")
    void answer_Duplicate_Blocked() throws Exception {
        given(adminQnaService.answer(eq(1L), any(AdminQnaAnswerCreateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED));
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        mockMvc.perform(post("/admin/cs/qna/{id}/answer", 1L).with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CS004"));
    }

    @Test
    @DisplayName("answer - 존재하지 않는 QNA 시 QNA_NOT_FOUND")
    void answer_NotFound_Blocked() throws Exception {
        given(adminQnaService.answer(eq(1L), any(AdminQnaAnswerCreateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.QNA_NOT_FOUND));
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        mockMvc.perform(post("/admin/cs/qna/{id}/answer", 1L).with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CS002"));
    }

    @Test
    @DisplayName("answer - USER 권한으로 접근 시 403")
    void answer_UserAccess_Forbidden() throws Exception {
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        mockMvc.perform(post("/admin/cs/qna/{id}/answer", 1L).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminQnaService);
    }

    @Test
    @DisplayName("answer - 인증 없이 접근 시 401")
    void answer_Unauthorized() throws Exception {
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        mockMvc.perform(post("/admin/cs/qna/{id}/answer", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(adminQnaService);
    }
}
