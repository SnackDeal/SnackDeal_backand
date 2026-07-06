package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.dashboard.service.DashboardService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    @Mock
    private DashboardService dashboardService;

    @Test
    @DisplayName("main - 대시보드 집계 결과를 그대로 감싸 반환")
    void main() {
        DashboardResponse expected = new DashboardResponse(12, 340000, 5, 3, 6);
        when(dashboardService.getSummary()).thenReturn(expected);

        CommonResponse<DashboardResponse> response = adminDashboardController.main();

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }
}
