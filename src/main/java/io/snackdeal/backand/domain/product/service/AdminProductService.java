package io.snackdeal.backand.domain.product.service;

import io.snackdeal.backand.api.admin.product.dto.*;
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
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    private static final int DEFAULT_IMAGE_SORT_ORDER = 1;

    public PageResponse<AdminProductListResponse> findList(
            String keyword,
            Long categoryId,
            ProductStatus status,
            Boolean lowStock,
            String sort,
            int page,
            int size
    ) {
        validateSaveStatus(status);

        validatePageRequest(page, size);

        String normalizedKeyword = normalizeKeyword(keyword);
        boolean lowStockOnly = Boolean.TRUE.equals(lowStock);

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                toAdminSort(sort)
        );

        Page<Product> productPage = productRepository.searchAdminProducts(
                normalizedKeyword,
                categoryId,
                status,
                lowStockOnly,
                ProductStatus.DELETED,
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

        List<AdminProductListResponse> items = products.stream()
                .map(product -> new AdminProductListResponse(
                        product.getId(),
                        product.getName(),
                        product.getCategoryId(),
                        categoryNameMap.get(product.getCategoryId()),
                        product.getPrice(),
                        product.getStock(),
                        product.getStatus(),
                        imageUrlMap.get(product.getId())
                ))
                .toList();

        return new PageResponse<>(
                items,
                page,
                size,
                productPage.getTotalElements()
        );
    }

    @Transactional
    public AdminProductDetailResponse save(AdminProductRequest request) {
        validateSaveStatus(request.status());

        Category category = findCategory(request.categoryId());

        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .stock(request.stock())
                .categoryId(category.getId())
                .status(request.status())
                .build();

        Product savedProduct = productRepository.save(product);

        ProductImage productImage = ProductImage.builder()
                .productId(savedProduct.getId())
                .attachmentUrl(request.imageUrl())
                .sortOrder(DEFAULT_IMAGE_SORT_ORDER)
                .build();

        ProductImage savedProductImage = productImageRepository.save(productImage);

        return toDetailResponse(savedProduct, category, savedProductImage);
    }

    public AdminProductDetailResponse findById(Long id) {
        Product product = findProduct(id);
        validateNotDeleted(product);
        return toDetailResponse(product);
    }

    @Transactional
    public AdminProductDetailResponse update(Long id, AdminProductRequest request) {
        validateSaveStatus(request.status());

        Product product = findProduct(id);
        validateNotDeleted(product);

        Category category = findCategory(request.categoryId());

        product.updateProduct(
                request.name(),
                request.price(),
                request.description(),
                request.stock(),
                category.getId(),
                request.status()
        );

        ProductImage productImage = findProductImage(product.getId());
        productImage.updateAttachmentUrl(request.imageUrl());

        return toDetailResponse(product, category, productImage);
    }

    @Transactional
    public AdminProductStatusResponse changeStatus(Long id, AdminProductStatusUpdateRequest request) {
        Product product = findProduct(id);

        validateChangeableProduct(product);

        product.changeStatus(request.status());

        return new AdminProductStatusResponse(
                product.getId(),
                product.getStatus(),
                product.getUpdatedAt()
        );
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

    private AdminProductDetailResponse toDetailResponse(Product product) {
        Category category = findCategory(product.getCategoryId());
        ProductImage productImage = findProductImage(product.getId());

        return toDetailResponse(product, category, productImage);
    }

    private AdminProductDetailResponse toDetailResponse(
            Product product,
            Category category,
            ProductImage productImage
    ) {
        return new AdminProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getCategoryId(),
                category.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock(),
                product.getStatus(),
                productImage.getAttachmentUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void validateSaveStatus(ProductStatus status) {
        if (status == ProductStatus.DELETED) {
            throw new BusinessException(ResponseCode.INVALID_PRODUCT_STATUS);
        }
    }

    private void validateChangeableProduct(Product product) {
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ResponseCode.INVALID_PRODUCT_STATUS);
        }
    }

    private void validateNotDeleted(Product product) {
        if (product.getStatus() == ProductStatus.DELETED) {
            throw new BusinessException(ResponseCode.PRODUCT_NOT_FOUND);
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private void validatePageRequest(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(ResponseCode.INVALID_PAGE_REQUEST);
        }
    }

    private Sort toAdminSort(String sort) {
        return switch (sort) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "stock_asc" -> Sort.by(Sort.Direction.ASC, "stock");
            case "stock_desc" -> Sort.by(Sort.Direction.DESC, "stock");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
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
}
