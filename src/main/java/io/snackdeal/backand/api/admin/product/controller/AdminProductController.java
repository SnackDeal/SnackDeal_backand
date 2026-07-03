package io.snackdeal.backand.api.admin.product.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.domain.product.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public CommonResponse<Object> list() {
        return CommonResponse.success(adminProductService.findList());
    }

    @PostMapping
    public CommonResponse<Object> save(@RequestBody Object request) {
        return CommonResponse.success(adminProductService.save(request));
    }

    @GetMapping("/{id}")
    public CommonResponse<Object> findById(@PathVariable Long id) {
        return CommonResponse.success(adminProductService.findById(id));
    }

    @PutMapping("/{id}")
    public CommonResponse<Object> update(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminProductService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public CommonResponse<Object> changeStatus(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminProductService.changeStatus(id, request));
    }
}
