package io.snackdeal.backand.api.admin.product.controller;

import io.snackdeal.backand.api.admin.product.dto.*;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.domain.product.service.AdminProductService;
import io.snackdeal.backand.global.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;

    @GetMapping
    public CommonResponse<PageResponse<AdminProductListResponse>> findList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "false") Boolean lowStock,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return CommonResponse.success(adminProductService.findList(keyword, categoryId, status, lowStock, sort, page, size));
    }

    @PostMapping
    public CommonResponse<AdminProductDetailResponse> save(
            @Valid @RequestBody AdminProductRequest request
    ) {
        return CommonResponse.created(adminProductService.save(request));
    }

    @GetMapping("/{id}")
    public CommonResponse<AdminProductDetailResponse> findById(@PathVariable Long id) {
        return CommonResponse.success(adminProductService.findById(id));
    }

    @PutMapping("/{id}")
    public CommonResponse<AdminProductDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequest request
    ) {
        return CommonResponse.success(adminProductService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public CommonResponse<AdminProductStatusResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductStatusUpdateRequest request
    ) {
        return CommonResponse.success(adminProductService.changeStatus(id, request));
    }
}
