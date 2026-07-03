package io.snackdeal.backand.api.admin.order.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.domain.order.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public CommonResponse<Object> list() {
        return CommonResponse.success(adminOrderService.findList());
    }

    @GetMapping("/{id}")
    public CommonResponse<Object> findById(@PathVariable Long id) {
        return CommonResponse.success(adminOrderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public CommonResponse<Object> changeStatus(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminOrderService.changeStatus(id, request));
    }

    @PostMapping("/{id}/refund")
    public CommonResponse<Object> refund(@PathVariable Long id, @RequestBody(required = false) Object request) {
        return CommonResponse.success(adminOrderService.refund(id, request));
    }
}
