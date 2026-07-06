package io.snackdeal.backand.domain.dashboard.service;

import io.snackdeal.backand.api.admin.main.dto.DashboardResponse;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 대시보드 집계 단위테스트.
 * EntityManager(JPQL)와 MemberRepository 를 목으로 대체하고, 각 지표 쿼리 결과가
 * DashboardResponse 의 올바른 필드로 매핑되는지 검증
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private EntityManager em;
    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        // @InjectMocks 가 생성자 주입(memberRepository)을 사용하면 @PersistenceContext 필드(em)는
        // 자동 주입되지 않으므로, EntityManager 목을 필드에 직접 넣어준다.
        ReflectionTestUtils.setField(dashboardService, "em", em);
    }

    // JPQL 문자열의 특징적인 조각으로 각 쿼리를 구분해 결과값을 지정하는 헬퍼
    @SuppressWarnings("unchecked")
    private void stubQuery(String jpqlFragment, long result) {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(em.createQuery(contains(jpqlFragment), eq(Long.class))).thenReturn(query);
        // 파라미터가 없는 쿼리(미답변 QnA 집계 등)도 있으므로 setParameter 스텁은 lenient 처리
        lenient().when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(result);
    }

    @Test
    @DisplayName("getSummary - 각 지표를 집계해 DashboardResponse 로 매핑")
    void getSummary() {
        stubQuery("count(o)", 12);              // 오늘 주문 수
        stubQuery("sum(o.finalAmount)", 340000);// 오늘 매출
        stubQuery("count(p)", 3);               // 저재고 상품 수
        stubQuery("count(q)", 6);               // 미답변 QnA 수
        when(memberRepository.countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);                // 신규 회원 수

        DashboardResponse response = dashboardService.getSummary();

        assertEquals(12, response.todayOrderCount());
        assertEquals(340000, response.todaySalesAmount());
        assertEquals(5, response.newMemberCount());
        assertEquals(3, response.lowStockProductCount());
        assertEquals(6, response.pendingQnaCount());
    }
}
