package io.snackdeal.backand.api.user.delivery.controller;

import io.snackdeal.backand.domain.delivery.service.DeliveryService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public CommonResponse<Object> list(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(deliveryService.findList(details.getEmail()));
    }

    @PostMapping
    public CommonResponse<Object> save(@AuthenticationPrincipal MemberDetails details, @RequestBody Object request) {
        return CommonResponse.success(deliveryService.save(details.getEmail(), request));
    }

    @PutMapping("/{id}")
    public CommonResponse<Object> update(@AuthenticationPrincipal MemberDetails details,
                                          @PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(deliveryService.update(details.getEmail(), id, request));
    }

    @PatchMapping("/{id}/default")
    public CommonResponse<Object> markDefault(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        return CommonResponse.success(deliveryService.markDefault(details.getEmail(), id));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        deliveryService.delete(details.getEmail(), id);
        return CommonResponse.success(null);
    }
}
