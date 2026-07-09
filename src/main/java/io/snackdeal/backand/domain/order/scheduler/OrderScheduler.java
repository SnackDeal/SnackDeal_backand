package io.snackdeal.backand.domain.order.scheduler;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.Orders;
import io.snackdeal.backand.domain.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrdersRepository ordersRepository;

    @Value("${custom.order.pending-expiration-minutes:30}")
    private long pendingExpirationMinutes;

    // 결제창 진입(prepare) 후 유예시간이 지나도록 결제를 완료하지 않은 주문을 자동 취소 (10분마다, 관리자가 수동 변경한 건은 제외)
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void cancelExpiredPendingOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(pendingExpirationMinutes);
        List<Orders> expired = ordersRepository.findByStatusAndManualOverrideFalseAndOrderedAtBefore(
                OrderStatus.PENDING_PAYMENT, cutoff);
        expired.forEach(order -> order.changeStatus(OrderStatus.CANCELLED));
    }
}
