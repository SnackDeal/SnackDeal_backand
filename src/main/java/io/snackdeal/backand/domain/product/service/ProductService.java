package io.snackdeal.backand.domain.product.service;

import io.snackdeal.backand.api.user.product.dto.ProductResponse;
import io.snackdeal.backand.api.user.product.dto.ProductSummaryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.entity.ProductImage;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import io.snackdeal.backand.domain.product.repository.ProductImageRepository;
import io.snackdeal.backand.domain.product.repository.ProductRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.global.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    public PageResponse<ProductSummaryResponse> findList(
            String keyword,
            Long categoryId,
            String sort,
            int page,
            int size
    ) {
        validatePageRequest(page, size);

        String normalizedKeyword = normalizeKeyword(keyword);

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                toUserSort(sort)
        );

        Page<Product> productPage = productRepository.searchUserProducts(
                normalizedKeyword,
                categoryId,
                ProductStatus.ACTIVE,
                pageable
        );

        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    page,
                    size,
                    productPage.getTotalElements()
            );
        }

        Map<Long, String> categoryNameMap = getCategoryNameMap(products);
        Map<Long, String> imageUrlMap = getImageUrlMap(products);

        List<ProductSummaryResponse> items = products.stream()
                .map(product -> new ProductSummaryResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        imageUrlMap.get(product.getId()),
                        product.getCategoryId(),
                        categoryNameMap.get(product.getCategoryId()),
                        product.isSoldout()
                ))
                .toList();

        return new PageResponse<>(
                items,
                page,
                size,
                productPage.getTotalElements()
        );
    }

    public ProductResponse findById(Long productId) {
        Product product = findProduct(productId);
        validateActive(product);
        return toDetailResponse(product);
    }

    private void validatePageRequest(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(ResponseCode.INVALID_PAGE_REQUEST);
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private Sort toUserSort(String sort) {
        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "popular" -> throw new BusinessException(ResponseCode.NOT_IMPLEMENTED); // order에서 받아오기?
            default -> throw new BusinessException(ResponseCode.INVALID_PRODUCT_SORT);
        };
    }

    private Map<Long, String> getCategoryNameMap(List<Product> products) {
        List<Long> categoryIds = products.stream()
                .map(Product::getCategoryId)
                .distinct()
                .toList();

        return categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        Category::getName
                ));
    }

    private Map<Long, String> getImageUrlMap(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        return productImageRepository.findByProductIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductImage::getProductId,
                        ProductImage::getAttachmentUrl,
                        (first, second) -> first
                ));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_NOT_FOUND));
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));
    }

    private ProductImage findProductImage(Long productId) {
        return productImageRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(ResponseCode.PRODUCT_IMAGE_NOT_FOUND));
    }

    private void validateActive(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException(ResponseCode.PRODUCT_NOT_FOUND);
        }
    }

    private ProductResponse toDetailResponse(Product product) {
        Category category = findCategory(product.getCategoryId());
        ProductImage productImage = findProductImage(product.getId());

        return toDetailResponse(product, category, productImage);
    }

    private ProductResponse toDetailResponse(
            Product product,
            Category category,
            ProductImage productImage
    ) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                productImage.getAttachmentUrl(),
                product.getStock(),
                product.getStatus(),
                product.isSoldout(),
                product.getCategoryId(),
                category.getName()
        );
    }
}
