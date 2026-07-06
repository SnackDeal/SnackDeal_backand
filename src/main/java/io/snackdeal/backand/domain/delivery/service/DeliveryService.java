package io.snackdeal.backand.domain.delivery.service;

import io.snackdeal.backand.api.user.delivery.dto.DeliveryCreateResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryListResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryRequest;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryResponse;
import io.snackdeal.backand.domain.delivery.entity.Delivery;
import io.snackdeal.backand.domain.delivery.repository.DeliveryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional(readOnly = true)
    public DeliveryListResponse findList(Long memberId) {
        List<DeliveryResponse> deliveries = deliveryRepository.findActiveByMemberId(memberId)
                .stream()
                .map(DeliveryResponse::from)
                .toList();

        return new DeliveryListResponse(deliveries);
    }

    @Transactional
    public DeliveryCreateResponse save(Long memberId, DeliveryRequest request) {
        boolean firstDelivery = !deliveryRepository.existsByMemberIdAndDeletedAtIsNull(memberId);
        boolean defaultDelivery = firstDelivery || request.isDefault();

        if (defaultDelivery) {
            unmarkDefaultDeliveries(memberId);
        }

        Delivery delivery = Delivery.builder()
                .name(request.name())
                .receiverName(request.receiverName())
                .receiverPhone(request.receiverPhone())
                .zipcode(request.zipcode())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .isDefault(defaultDelivery)
                .memberId(memberId)
                .build();

        Delivery saved = deliveryRepository.save(delivery);
        return new DeliveryCreateResponse(saved.getId(), saved.isDefault());
    }

    @Transactional
    public DeliveryResponse update(Long memberId, Long id, DeliveryRequest request) {
        Delivery delivery = findActiveDelivery(id);
        validateOwner(delivery, memberId);

        delivery.updateAddress(
                request.name(),
                request.receiverName(),
                request.receiverPhone(),
                request.zipcode(),
                request.address(),
                request.detailAddress()
        );

        if (request.isDefault()) {
            setOnlyDefault(memberId, delivery);
        }

        return DeliveryResponse.from(delivery);
    }

    @Transactional
    public void markDefault(Long memberId, Long id) {
        Delivery delivery = findActiveDelivery(id);
        validateOwner(delivery, memberId);

        setOnlyDefault(memberId, delivery);
    }

    @Transactional
    public void delete(Long memberId, Long id) {
        Delivery delivery = findActiveDelivery(id);
        validateOwner(delivery, memberId);

        if (delivery.isDefault()) {
            throw new BusinessException(ResponseCode.DELIVERY_DEFAULT_CANNOT_BE_DELETED);
        }

        delivery.markDeleted();
    }

    private Delivery findActiveDelivery(Long id) {
        return deliveryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.DELIVERY_NOT_FOUND));
    }

    private void validateOwner(Delivery delivery, Long memberId) {
        if (!delivery.getMemberId().equals(memberId)) {
            throw new BusinessException(ResponseCode.FORBIDDEN_ACCESS);
        }
    }

    private void unmarkDefaultDeliveries(Long memberId) {
        deliveryRepository.findActiveDefaultsByMemberId(memberId)
                .forEach(Delivery::unmarkDefault);
    }

    private void setOnlyDefault(Long memberId, Delivery target) {
        deliveryRepository.findActiveDefaultsByMemberId(memberId)
                .stream()
                .filter(delivery -> !delivery.getId().equals(target.getId()))
                .forEach(Delivery::unmarkDefault);
        target.markAsDefault();
    }
}
