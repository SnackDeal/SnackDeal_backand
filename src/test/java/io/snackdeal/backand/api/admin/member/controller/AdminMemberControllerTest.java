package io.snackdeal.backand.api.admin.member.controller;

import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.MemberStatusResponse;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.service.MemberService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMemberControllerTest {

    @InjectMocks
    private AdminMemberController adminMemberController;

    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("findAll - keyword/status 필터를 서비스에 전달하고 페이지를 반환한다")
    void findAll() {
        Page<MemberDescription> page = new PageImpl<>(List.of());
        when(memberService.search(eq("hong"), eq(MemberStatus.ACTIVE), any())).thenReturn(page);

        CommonResponse<Page<MemberDescription>> response =
                adminMemberController.findAll("hong", MemberStatus.ACTIVE, 0, 10);

        assertSame(page, response.getData());
        verify(memberService).search(eq("hong"), eq(MemberStatus.ACTIVE), any());
    }

    @Test
    @DisplayName("findById - 회원 상세를 조회한다")
    void findById() {
        MemberDescription expected = new MemberDescription(45L, "hong@test.com", "홍길동",
                "01011112222", null, null, MemberStatus.ACTIVE, MemberRole.USER, null, null);
        when(memberService.findById(45L)).thenReturn(expected);

        CommonResponse<MemberDescription> response = adminMemberController.findById(45L);

        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("changeStatus - 인증된 관리자 id를 함께 넘겨 상태 변경을 위임한다")
    void changeStatus() {
        MemberDetails admin = new MemberDetails(99L, "admin@test.com", "ENCODED", MemberRole.ADMIN);
        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(MemberStatus.INACTIVE, "6개월 미접속");
        MemberStatusResponse expected =
                new MemberStatusResponse(45L, "hong@test.com", MemberStatus.INACTIVE, LocalDateTime.now());
        when(memberService.changeStatus(45L, request, 99L)).thenReturn(expected);

        CommonResponse<MemberStatusResponse> response =
                adminMemberController.changeStatus(admin, 45L, request);

        assertSame(expected, response.getData());
        assertEquals(MemberStatus.INACTIVE, response.getData().status());
        verify(memberService).changeStatus(45L, request, 99L);
    }
}
