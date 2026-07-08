package io.snackdeal.backand.api.user.product.controller;

import io.snackdeal.backand.api.user.product.dto.ProductResponse;
import io.snackdeal.backand.api.user.product.dto.ProductSummaryResponse;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.domain.product.service.ProductService;
import io.snackdeal.backand.global.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public CommonResponse<PageResponse<ProductSummaryResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return CommonResponse.success(productService.findList(keyword, categoryId, sort, page, size));
    }

    @GetMapping("/{productId}")
    public CommonResponse<ProductResponse> findById(@PathVariable Long productId) {
        return CommonResponse.success(productService.findById(productId));
    }
}
