package io.snackdeal.backand.domain.product.scheduler;

import io.snackdeal.backand.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductRepository productRepository;

    // product테이블에 총 판매수 기입하기 위한 스케줄러(새벽 3시)
    @Scheduled(cron = "0 0 3 * * *")
    //@Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void updateProductSoldQuantity() {
        productRepository.updateSoldQuantity();
    }
}