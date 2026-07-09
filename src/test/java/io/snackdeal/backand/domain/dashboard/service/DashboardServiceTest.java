package io.snackdeal.backand.domain.dashboard.service;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.dashboard.repository.DashboardQueryRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * 대시보드 집계 단위테스트.
 * DashboardQueryRepository 와 MemberRepository 를 목으로 대체하고, 각 지표 결과가
 * DashboardResponse 의 올바른 필드로 매핑되는지 검증
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private DashboardQueryRepository dashboardQueryRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("getSummary - 각 지표를 집계해 DashboardResponse 로 매핑")
    void getSummary() {
        when(dashboardQueryRepository.countOrdersBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(12L);
        when(dashboardQueryRepository.sumSalesAmountBetween(any(LocalDateTime.class), any(LocalDateTime.class), anyList()))
                .thenReturn(340000L);
        when(dashboardQueryRepository.countLowStockProducts(anyInt()))
                .thenReturn(3L);
        when(dashboardQueryRepository.countPendingQna())
                .thenReturn(6L);
        when(memberRepository.countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);

        DashboardResponse response = dashboardService.getSummary();

        assertEquals(12, response.todayOrderCount());
        assertEquals(340000, response.todaySalesAmount());
        assertEquals(5, response.newMemberCount());
        assertEquals(3, response.lowStockProductCount());
        assertEquals(6, response.pendingQnaCount());
    }
}
