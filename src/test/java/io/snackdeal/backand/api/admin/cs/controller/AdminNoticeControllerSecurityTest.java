package io.snackdeal.backand.api.admin.cs.controller;

import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.cs.service.AdminNoticeService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import org.junit.jupiter.api.DisplayName;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
        locations = "classpath:test-config.properties",
        properties = {
                "custom.cloud.s3.access-key=test-access-key",
                "custom.cloud.s3.secret-key=test-secret-key",
                "custom.cloud.s3.bucket=test-bucket"
        }
)
class AdminNoticeControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminNoticeService adminNoticeService;

    private RequestPostProcessor as(MemberRole role) {
        UserDetails principal = new MemberDetails(1L, role.name().toLowerCase() + "@test.com", "password", role);
        return authentication(new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities()));
    }

    @Test
    @DisplayName("USER 권한으로 관리자 공지사항 API 호출 시 403 Forbidden")
    void user_Forbidden() throws Exception {
        mockMvc.perform(get("/admin/cs/notice").with(as(MemberRole.USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("비인증 상태로 관리자 공지사항 API 호출 시 401 Unauthorized")
    void unauthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/admin/cs/notice"))
                .andExpect(status().isUnauthorized());
    }
}
