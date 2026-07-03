package io.snackdeal.backand.api.admin.main.dto;

public record DashboardResponse(
        long todayOrderCount,
        long todaySalesAmount,
        long newMemberCount,
        long lowStockProductCount,
        long pendingQnaCount
) {
}
