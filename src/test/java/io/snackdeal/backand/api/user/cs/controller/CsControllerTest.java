package io.snackdeal.backand.api.user.cs.controller;

import io.snackdeal.backand.api.user.cs.dto.*;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.service.CsService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-config.properties")
class CsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CsService csService;

    private static final String EMAIL = "test@test.com";
    private static final Long MEMBER_ID = 1L;

    private RequestPostProcessor asUser() {
        UserDetails principal = new MemberDetails(MEMBER_ID, EMAIL, "password", MemberRole.USER);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("myQnaList - 내 QNA 목록 조회 성공")
    void myQnaList_Success() throws Exception {
        QnaSummaryResponse item = new QnaSummaryResponse(1L, QnaType.ORDER, "Title", false, LocalDateTime.now());
        given(csService.findMyQnaList(MEMBER_ID)).willReturn(List.of(item));

        mockMvc.perform(get("/cs/qna/list").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Title"));
    }

    @Test
    @DisplayName("myQnaList - 인증 없이 접근 시 401")
    void myQnaList_Unauthorized() throws Exception {
        mockMvc.perform(get("/cs/qna/list"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("createQna - QNA 생성 성공")
    void createQna_Success() throws Exception {
        QnaCreateRequest request = new QnaCreateRequest(QnaType.ORDER, "Test title", "Test content", null);
        QnaResponse response = new QnaResponse(1L, QnaType.ORDER, "Test title", "Test content", null,
                false, LocalDateTime.now(), null, null);
        given(csService.createQna(eq(MEMBER_ID), any(QnaCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/cs/qna").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("faqList - 비로그인 사용자가 전체 FAQ 조회 성공")
    void faqList_Success() throws Exception {
        // given
        FaqResponse response = new FaqResponse(1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?", "로그인 후 마이페이지에서 확인할 수 있습니다.");
        given(csService.findFaqList(null)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/cs/qna/faq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("주문 내역은 어디서 확인하나요?"));
    }

    @Test
    @DisplayName("faqList - 비로그인 사용자가 타입별 FAQ 조회 성공")
    void faqList_ByType_Success() throws Exception {
        // given
        FaqResponse response = new FaqResponse(1L, QnaType.ORDER, "주문 내역은 어디서 확인하나요?", "로그인 후 마이페이지에서 확인할 수 있습니다.");
        given(csService.findFaqList(QnaType.ORDER)).willReturn(List.of(response));

        // when / then
        mockMvc.perform(get("/cs/qna/faq").param("type", "ORDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].type").value("ORDER"));
    }

    @Test
    @DisplayName("createQna - type이 null이면 400")
    void createQna_ValidationFail_WhenTypeNull() throws Exception {
        QnaCreateRequest request = new QnaCreateRequest(null, "Title", "Content", null);

        mockMvc.perform(post("/cs/qna").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("createQna - title이 blank면 400")
    void createQna_ValidationFail_WhenTitleBlank() throws Exception {
        QnaCreateRequest request = new QnaCreateRequest(QnaType.ORDER, "", "Content", null);

        mockMvc.perform(post("/cs/qna").with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("createQna - 인증 없이 접근 시 401")
    void createQna_Unauthorized() throws Exception {
        QnaCreateRequest request = new QnaCreateRequest(QnaType.ORDER, "Title", "Content", null);

        mockMvc.perform(post("/cs/qna")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("qnaDetail - 내 QNA 상세 조회 성공")
    void qnaDetail_Success() throws Exception {
        QnaResponse response = new QnaResponse(1L, QnaType.ORDER, "Title", "Content", null,
                false, LocalDateTime.now(), null, null);
        given(csService.findQnaById(MEMBER_ID, 1L)).willReturn(response);

        mockMvc.perform(get("/cs/qna/{id}", 1L).with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("qnaDetail - 다른 사용자 QNA 조회 시 QNA_NOT_FOUND")
    void qnaDetail_OtherMember_Blocked() throws Exception {
        given(csService.findQnaById(MEMBER_ID, 1L))
                .willThrow(new BusinessException(ResponseCode.QNA_NOT_FOUND));

        mockMvc.perform(get("/cs/qna/{id}", 1L).with(asUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CS002"));
    }

    @Test
    @DisplayName("qnaDetail - 인증 없이 접근 시 401")
    void qnaDetail_Unauthorized() throws Exception {
        mockMvc.perform(get("/cs/qna/{id}", 1L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("updateQna - QNA 수정 성공")
    void updateQna_Success() throws Exception {
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated", "Updated content", null);
        QnaResponse response = new QnaResponse(1L, QnaType.SHIPPING, "Updated", "Updated content", null,
                false, LocalDateTime.now(), null, null);
        given(csService.updateQna(eq(MEMBER_ID), eq(1L), any(QnaUpdateRequest.class))).willReturn(response);

        mockMvc.perform(patch("/cs/qna/{id}", 1L).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated"));
    }

    @Test
    @DisplayName("updateQna - 답변 완료된 QNA 수정 시 QNA_ALREADY_ANSWERED")
    void updateQna_Answered_Blocked() throws Exception {
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated", "Updated content", null);
        given(csService.updateQna(eq(MEMBER_ID), eq(1L), any(QnaUpdateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED));

        mockMvc.perform(patch("/cs/qna/{id}", 1L).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CS004"));
    }

    @Test
    @DisplayName("updateQna - 다른 사용자 QNA 수정 시 QNA_NOT_FOUND")
    void updateQna_OtherMember_Blocked() throws Exception {
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated", "Updated content", null);
        given(csService.updateQna(eq(MEMBER_ID), eq(1L), any(QnaUpdateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.QNA_NOT_FOUND));

        mockMvc.perform(patch("/cs/qna/{id}", 1L).with(asUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CS002"));
    }

    @Test
    @DisplayName("updateQna - 인증 없이 접근 시 401")
    void updateQna_Unauthorized() throws Exception {
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.ORDER, "Title", "Content", null);

        mockMvc.perform(patch("/cs/qna/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(csService);
    }

    @Test
    @DisplayName("deleteQna - QNA 삭제 성공")
    void deleteQna_Success() throws Exception {
        willDoNothing().given(csService).deleteQna(MEMBER_ID, 1L);

        mockMvc.perform(delete("/cs/qna/{id}", 1L).with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("deleteQna - 답변 완료된 QNA 삭제 시 QNA_ALREADY_ANSWERED")
    void deleteQna_Answered_Blocked() throws Exception {
        willThrow(new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED))
                .given(csService).deleteQna(MEMBER_ID, 1L);

        mockMvc.perform(delete("/cs/qna/{id}", 1L).with(asUser()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CS004"));
    }

    @Test
    @DisplayName("deleteQna - 다른 사용자 QNA 삭제 시 QNA_NOT_FOUND")
    void deleteQna_OtherMember_Blocked() throws Exception {
        willThrow(new BusinessException(ResponseCode.QNA_NOT_FOUND))
                .given(csService).deleteQna(MEMBER_ID, 1L);

        mockMvc.perform(delete("/cs/qna/{id}", 1L).with(asUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CS002"));
    }

    @Test
    @DisplayName("deleteQna - 인증 없이 접근 시 401")
    void deleteQna_Unauthorized() throws Exception {
        mockMvc.perform(delete("/cs/qna/{id}", 1L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(csService);
    }
}
