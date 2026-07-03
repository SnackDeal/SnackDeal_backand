package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드 지표는 각 도메인 테이블 집계(파생)이며, 해당 도메인 서비스가 아직 골격만 있어 우선 0으로 고정 응답한다.
 */
@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping("/main")
    public CommonResponse<DashboardResponse> main() {
        return CommonResponse.success(new DashboardResponse(0, 0, 0, 0, 0));
    }
}
