package io.snackdeal.backand.api.user.product.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public CommonResponse<Object> list() {
        return CommonResponse.success(productService.findList());
    }

    @GetMapping("/{productId}")
    public CommonResponse<Object> findById(@PathVariable Long productId) {
        return CommonResponse.success(productService.findById(productId));
    }
}
