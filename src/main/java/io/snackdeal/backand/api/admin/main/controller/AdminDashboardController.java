package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.admin.main.dto.CouponChartResponse;
import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.api.admin.main.dto.MemberChartResponse;
import io.snackdeal.backand.api.admin.main.dto.OrderChartResponse;
import io.snackdeal.backand.api.admin.main.dto.ProductSalesChartResponse;
import io.snackdeal.backand.domain.dashboard.service.DashboardService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.AdminMainApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


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

    // GET /admin/main/chart/members : 기간별 신규 회원가입 추이
    @AdminMainApiDocs.MemberChart
    @GetMapping("/main/chart/members")
    public CommonResponse<MemberChartResponse> memberChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResponse.success(dashboardService.getMemberChart(startDate, endDate));
    }

    // GET /admin/main/chart/orders : 기간별 주문 수 추이
    @AdminMainApiDocs.OrderChart
    @GetMapping("/main/chart/orders")
    public CommonResponse<OrderChartResponse> orderChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResponse.success(dashboardService.getOrderChart(startDate, endDate));
    }

    // GET /admin/main/chart/sales : 기간별 상품판매(매출액/판매수량) 추이
    @AdminMainApiDocs.ProductSalesChart
    @GetMapping("/main/chart/sales")
    public CommonResponse<ProductSalesChartResponse> productSalesChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResponse.success(dashboardService.getProductSalesChart(startDate, endDate));
    }

    // GET /admin/main/chart/coupons : 기간별 쿠폰 발급/사용 추이
    @AdminMainApiDocs.CouponChart
    @GetMapping("/main/chart/coupons")
    public CommonResponse<CouponChartResponse> couponChart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return CommonResponse.success(dashboardService.getCouponChart(startDate, endDate));
    }
}
