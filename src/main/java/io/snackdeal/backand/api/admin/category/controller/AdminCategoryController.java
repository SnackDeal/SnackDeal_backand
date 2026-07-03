package io.snackdeal.backand.api.admin.category.controller;

import io.snackdeal.backand.domain.category.service.AdminCategoryService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public CommonResponse<Object> list() {
        return CommonResponse.success(adminCategoryService.findList());
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
