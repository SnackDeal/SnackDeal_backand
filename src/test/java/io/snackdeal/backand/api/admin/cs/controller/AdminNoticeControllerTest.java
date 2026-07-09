package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeCreateRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeUpdateRequest;
import io.snackdeal.backand.api.user.cs.dto.NoticeResponse;
import io.snackdeal.backand.api.user.cs.dto.NoticeSummaryResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.service.AdminNoticeService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-config.properties",
        properties = {
                "custom.cloud.s3.access-key=test-access-key",
                "custom.cloud.s3.secret-key=test-secret-key",
                "custom.cloud.s3.bucket=test-bucket"
        }
)
@WithMockUser(roles = "ADMIN")
class AdminNoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminNoticeService adminNoticeService;

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(1L, role.name().toLowerCase() + "@test.com", "password", role);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("GET /admin/cs/notice - 공지사항 목록 조회 성공")
    void list_Success() throws Exception {
        NoticeSummaryResponse response = new NoticeSummaryResponse(1L, "공지", true, LocalDateTime.now());
        given(adminNoticeService.findList()).willReturn(List.of(response));

        mockMvc.perform(get("/admin/cs/notice").with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("공지"));
    }

    @Test
    @DisplayName("GET /admin/cs/notice/{id} - 공지사항 상세 조회 성공")
    void findById_Success() throws Exception {
        NoticeResponse response = new NoticeResponse(1L, "공지", "내용", true, LocalDateTime.now(), null);
        given(adminNoticeService.findById(1L)).willReturn(response);

        mockMvc.perform(get("/admin/cs/notice/{id}", 1L).with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("공지"));
    }

    @Test
    @DisplayName("GET /admin/cs/notice/{id} - 존재하지 않으면 NOTICE_NOT_FOUND")
    void findById_NotFound() throws Exception {
        given(adminNoticeService.findById(999L))
                .willThrow(new BusinessException(ResponseCode.NOTICE_NOT_FOUND));

        mockMvc.perform(get("/admin/cs/notice/{id}", 999L).with(as(MemberRole.ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOTICE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("POST /admin/cs/notice - 공지사항 생성 성공")
    void create_Success() throws Exception {
        AdminNoticeCreateRequest request = new AdminNoticeCreateRequest("제목", "내용", true);
        NoticeResponse response = new NoticeResponse(1L, "제목", "내용", true, LocalDateTime.now(), null);
        given(adminNoticeService.create(any(AdminNoticeCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/admin/cs/notice")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("제목"));
    }

    @Test
    @DisplayName("POST /admin/cs/notice - 제목이 비어있으면 400 Bad Request")
    void create_ValidationFail() throws Exception {
        AdminNoticeCreateRequest request = new AdminNoticeCreateRequest("", "내용", false);

        mockMvc.perform(post("/admin/cs/notice")
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminNoticeService);
    }

    @Test
    @DisplayName("PUT /admin/cs/notice/{id} - 공지사항 수정 성공")
    void update_Success() throws Exception {
        AdminNoticeUpdateRequest request = new AdminNoticeUpdateRequest("새제목", "새내용", false);
        NoticeResponse response = new NoticeResponse(1L, "새제목", "새내용", false, LocalDateTime.now(), LocalDateTime.now());
        given(adminNoticeService.update(eq(1L), any(AdminNoticeUpdateRequest.class))).willReturn(response);

        mockMvc.perform(put("/admin/cs/notice/{id}", 1L)
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("새제목"));
    }

    @Test
    @DisplayName("PUT /admin/cs/notice/{id} - 존재하지 않으면 NOTICE_NOT_FOUND")
    void update_NotFound() throws Exception {
        AdminNoticeUpdateRequest request = new AdminNoticeUpdateRequest("제목", "내용", false);
        given(adminNoticeService.update(eq(999L), any(AdminNoticeUpdateRequest.class)))
                .willThrow(new BusinessException(ResponseCode.NOTICE_NOT_FOUND));

        mockMvc.perform(put("/admin/cs/notice/{id}", 999L)
                        .with(as(MemberRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOTICE_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("DELETE /admin/cs/notice/{id} - 공지사항 소프트 삭제 성공")
    void delete_Success() throws Exception {
        willDoNothing().given(adminNoticeService).delete(1L);

        mockMvc.perform(delete("/admin/cs/notice/{id}", 1L).with(as(MemberRole.ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(adminNoticeService).delete(1L);
    }

    @Test
    @DisplayName("DELETE /admin/cs/notice/{id} - 존재하지 않으면 NOTICE_NOT_FOUND")
    void delete_NotFound() throws Exception {
        willThrow(new BusinessException(ResponseCode.NOTICE_NOT_FOUND)).given(adminNoticeService).delete(999L);

        mockMvc.perform(delete("/admin/cs/notice/{id}", 999L).with(as(MemberRole.ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOTICE_NOT_FOUND.getCode()));
    }
}