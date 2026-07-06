package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyResponse;
import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyUpdateRequest;
import io.snackdeal.backand.domain.order.entity.ShippingPolicy;
import io.snackdeal.backand.domain.order.repository.ShippingPolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingPolicyServiceTest {

    @InjectMocks
    private ShippingPolicyService shippingPolicyService;

    @Mock
    private ShippingPolicyRepository shippingPolicyRepository;

    @Test
    @DisplayName("get - 정책 행이 없으면 기본값(무료기준 20,000 / 배송비 0)으로 생성해 반환한다")
    void get_createsDefault() {
        when(shippingPolicyRepository.findById(1L)).thenReturn(Optional.empty());
        when(shippingPolicyRepository.save(any(ShippingPolicy.class))).thenAnswer(inv -> inv.getArgument(0));

        ShippingPolicyResponse response = shippingPolicyService.get();

        assertEquals(0L, response.baseFee());
        assertEquals(20000L, response.freeThreshold());
    }

    @Test
    @DisplayName("update - null 항목은 기존 값을 유지하고 지정 항목만 변경한다")
    void update_partial() {
        ShippingPolicy policy = ShippingPolicy.builder().id(1L).baseFee(0L).freeThreshold(20000L).build();
        when(shippingPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        ShippingPolicyResponse response = shippingPolicyService.update(
                new ShippingPolicyUpdateRequest(2500L, null));

        assertEquals(2500L, response.baseFee());       // 변경됨
        assertEquals(20000L, response.freeThreshold()); // 유지됨
    }
}
