package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyResponse;
import io.snackdeal.backand.api.admin.order.dto.ShippingPolicyUpdateRequest;
import io.snackdeal.backand.domain.order.entity.ShippingPolicy;
import io.snackdeal.backand.domain.order.repository.ShippingPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배송비 정책 관리 서비스(관리자 전용).
 * 정책은 단일 행(id=1)으로 관리하며, 행이 없으면 기본값(무료기준 20,000 / 배송비 0)으로 생성한다.
 */
@Service
@RequiredArgsConstructor
public class ShippingPolicyService {

    private static final long POLICY_ID = 1L;
    private static final long DEFAULT_BASE_FEE = 0L;
    private static final long DEFAULT_FREE_THRESHOLD = 20000L;

    private final ShippingPolicyRepository shippingPolicyRepository;

    @Transactional
    public ShippingPolicyResponse get() {
        return toResponse(getOrCreate());
    }

    @Transactional
    public ShippingPolicyResponse update(ShippingPolicyUpdateRequest request) {
        ShippingPolicy policy = getOrCreate();
        policy.update(request.baseFee(), request.freeThreshold());
        return toResponse(policy);
    }

    // 정책 행이 없으면 기본값으로 생성해 반환한다(H2/신규 환경 대비).
    private ShippingPolicy getOrCreate() {
        return shippingPolicyRepository.findById(POLICY_ID)
                .orElseGet(() -> shippingPolicyRepository.save(ShippingPolicy.builder()
                        .id(POLICY_ID)
                        .baseFee(DEFAULT_BASE_FEE)
                        .freeThreshold(DEFAULT_FREE_THRESHOLD)
                        .build()));
    }

    private ShippingPolicyResponse toResponse(ShippingPolicy policy) {
        return new ShippingPolicyResponse(policy.getBaseFee(), policy.getFreeThreshold(), policy.getUpdatedAt());
    }
}
