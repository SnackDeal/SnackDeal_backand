package io.snackdeal.backand.api.admin.category.controller;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.service.AdminCategoryService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public CommonResponse<List<CategoryResponse>> list() {
        return CommonResponse.success(adminCategoryService.findList());
    }

    @PatchMapping("/order")
    public CommonResponse<Void> updateOrder(@Valid @RequestBody CategoryOrderRequest request) {
        adminCategoryService.updateOrder(request);
        return CommonResponse.success(null);
    }

    @PostMapping
    public CommonResponse<Object> save(@RequestBody Object request) {
        return CommonResponse.success(adminCategoryService.save(request));
    }

    @PutMapping("/{id}")
    public CommonResponse<Object> update(@PathVariable Long id, @RequestBody Object request) {
        return CommonResponse.success(adminCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return CommonResponse.success(null);
    }
}
