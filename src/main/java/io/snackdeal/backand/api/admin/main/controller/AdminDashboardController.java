package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.dashboard.service.DashboardService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.AdminMainApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 대시보드 API. 홈 화면 상단 요약 지표(오늘 주문/매출, 신규회원, 저재고, 미답변 QnA)를 제공한다.
 * 접근 권한은 SecurityConfig 에서 "/admin/**" → ROLE_ADMIN 으로 제한된다.
 * Swagger 설명은 global 의 @AdminMainApiDocs 에서 가져온다.
 */
@AdminMainApiDocs.Doc
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    // GET /admin/main : 대시보드 요약 지표 조회
    @AdminMainApiDocs.Dashboard
    @GetMapping("/main")
    public CommonResponse<DashboardResponse> main() {
        return CommonResponse.success(dashboardService.getSummary());
    }
}
