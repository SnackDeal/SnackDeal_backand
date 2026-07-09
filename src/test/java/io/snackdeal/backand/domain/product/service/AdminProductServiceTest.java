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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminProductService 클래스의")
class AdminProductServiceTest {

    @InjectMocks
    private AdminProductService adminProductService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("Describe: findList() 메서드는")
    class Describe_findList {

        @Nested
        @DisplayName("Context: 조회 조건에 맞는 상품이 있는 경우")
        class Context_with_products {

            @Test
            @DisplayName("It: 관리자 상품 목록을 카테고리명, 썸네일 URL과 함께 반환")
            void It_관리자_상품_목록을_반환() {
                // given
                Product product = createProduct(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        50000L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.ACTIVE
                );

                Category category = createCategory(3L, "과자");

                ProductImage image = createProductImage(
                        10L,
                        1L,
                        "https://image.test.spicy-bigint.jpg"
                );

                given(productRepository.searchAdminProducts(
                        eq("감자"),
                        eq(3L),
                        eq(ProductStatus.ACTIVE),
                        eq(false),
                        eq(ProductStatus.DELETED),
                        any(Pageable.class)
                )).willReturn(new PageImpl<>(
                        List.of(product),
                        PageRequest.of(0, 10),
                        1
                ));

                given(categoryRepository.findAllById(List.of(3L)))
                        .willReturn(List.of(category));

                given(productImageRepository.findByProductIdIn(List.of(1L)))
                        .willReturn(List.of(image));

                // when
                PageResponse<AdminProductListResponse> response =
                        adminProductService.findList(
                                " 감자 ",
                                3L,
                                ProductStatus.ACTIVE,
                                false,
                                "latest",
                                1,
                                10
                        );

                // then
                assertThat(response.items()).hasSize(1);
                assertThat(response.page()).isEqualTo(1);
                assertThat(response.size()).isEqualTo(10);
                assertThat(response.total()).isEqualTo(1);

                AdminProductListResponse item = response.items().get(0);

                assertThat(item.id()).isEqualTo(1L);
                assertThat(item.name()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(item.categoryId()).isEqualTo(3L);
                assertThat(item.category()).isEqualTo("과자");
                assertThat(item.price()).isEqualTo(50000L);
                assertThat(item.stock()).isEqualTo(10);
                assertThat(item.status()).isEqualTo(ProductStatus.ACTIVE);
                assertThat(item.thumbnailUrl()).isEqualTo("https://image.test.spicy-bigint.jpg");

                then(productRepository).should().searchAdminProducts(
                        eq("감자"),
                        eq(3L),
                        eq(ProductStatus.ACTIVE),
                        eq(false),
                        eq(ProductStatus.DELETED),
                        any(Pageable.class)
                );
            }
        }

        @Nested
        @DisplayName("Context: DELETED 상태로 조회 요청한 경우")
        class Context_deleted_status {

            @Test
            @DisplayName("It: INVALID_PRODUCT_STATUS 예외 발생")
            void It_INVALID_PRODUCT_STATUS_예외_발생() {
                // given & when & then
                assertThatThrownBy(() -> adminProductService.findList(
                        null,
                        null,
                        ProductStatus.DELETED,
                        false,
                        "latest",
                        1,
                        10
                ))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.INVALID_PRODUCT_STATUS.getMessage());

                then(productRepository).shouldHaveNoInteractions();
                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("Describe: save() 메서드는")
    class Describe_save {
        @Nested
        @DisplayName("Context: 올바른 상품 등록 요청인 경우")
        class Context_valid_request {

            @Test
            @DisplayName("It: 상품과 대표 이미지를 저장하고 상세 응답을 반환")
            void It_상품과_대표_이미지를_저장() {
                // given
                AdminProductRequest request = createRequest(ProductStatus.ACTIVE);
                Category category = createCategory(3L, "과자");

                given(categoryRepository.findById(3L))
                        .willReturn(Optional.of(category));

                given(productRepository.save(any(Product.class)))
                        .willAnswer(invocation -> {
                            Product product = invocation.getArgument(0);
                            ReflectionTestUtils.setField(product, "id", 1L);
                            return product;
                        });

                given(productImageRepository.save(any(ProductImage.class)))
                        .willAnswer(invocation -> {
                            ProductImage image = invocation.getArgument(0);
                            ReflectionTestUtils.setField(image, "id", 10L);
                            return image;
                        });

                // when
                AdminProductDetailResponse response = adminProductService.save(request);

                // then
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.name()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(response.categoryId()).isEqualTo(3L);
                assertThat(response.category()).isEqualTo("과자");
                assertThat(response.price()).isEqualTo(50000L);
                assertThat(response.description()).isEqualTo("대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩");
                assertThat(response.stock()).isEqualTo(10);
                assertThat(response.status()).isEqualTo(ProductStatus.ACTIVE);
                assertThat(response.imageUrl()).isEqualTo("https://image.test.spicy-bigint.jpg");

                then(categoryRepository).should().findById(3L);
                then(productRepository).should().save(any(Product.class));
                then(productImageRepository).should().save(any(ProductImage.class));
            }
        }

        @Nested
        @DisplayName("Context: DELETED 상태로 등록 요청한 경우")
        class Context_deleted_status {

            @Test
            @DisplayName("It: INVALID_PRODUCT_STATUS 예외 발생")
            void It_INVALID_PRODUCT_STATUS_예외_발생() {
                // given
                AdminProductRequest request = createRequest(ProductStatus.DELETED);

                // when & then
                assertThatThrownBy(() -> adminProductService.save(request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.INVALID_PRODUCT_STATUS.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("Describe: findById() 메서드는")
    class Describe_findById {
        @Nested
        @DisplayName("Context: 삭제되지 않은 상품이 존재하는 경우")
        class Context_existing_product {

            @Test
            @DisplayName("It: 관리자 상품 상세 정보 반환")
            void It_관리자_상품_상세_반환() {
                // given
                Product product = createProduct(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        50000L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.INACTIVE
                );
                Category category = createCategory(3L, "과자");
                ProductImage image = createProductImage(10L, 1L, "https://image.test.spicy-bigint.jpg");

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));
                given(categoryRepository.findById(3L))
                        .willReturn(Optional.of(category));
                given(productImageRepository.findByProductId(1L))
                        .willReturn(Optional.of(image));

                // when
                AdminProductDetailResponse response = adminProductService.findById(1L);

                // then
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.name()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(response.categoryId()).isEqualTo(3L);
                assertThat(response.category()).isEqualTo("과자");
                assertThat(response.price()).isEqualTo(50000L);
                assertThat(response.description()).isEqualTo("대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩");
                assertThat(response.stock()).isEqualTo(10);
                assertThat(response.status()).isEqualTo(ProductStatus.INACTIVE);
                assertThat(response.imageUrl()).isEqualTo("https://image.test.spicy-bigint.jpg");
            }
        }

        @Nested
        @DisplayName("Context: DELETED 상품인 경우")
        class Context_deleted_product {

            @Test
            @DisplayName("It: PRODUCT_NOT_FOUND 예외 발생")
            void It_PRODUCT_NOT_FOUND_예외_발생() {
                // given
                Product product = createProduct(
                        1L,
                        "삭제된 대-정수의 핵불닭맛 감자칩",
                        1000L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.DELETED
                );

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));

                // when & then
                assertThatThrownBy(() -> adminProductService.findById(1L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.PRODUCT_NOT_FOUND.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("Describe: update() 메서드는")
    class Describe_update {
        @Nested
        @DisplayName("Context: 올바른 상품 수정 요청인 경우")
        class Context_valid_request {

            @Test
            @DisplayName("It: 상품 정보와 대표 이미지 URL 수정")
            void It_상품과_대표_이미지_수정() {
                // given
                Product product = createProduct(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        1000L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.ACTIVE
                );
                Category category = createCategory(4L, "신규 과자");
                ProductImage image = createProductImage(10L, 1L, "https://image.test.spicy-bigint.jpg");

                AdminProductRequest request = new AdminProductRequest(
                        "수정된 대-정수의 핵불닭맛 감자칩",
                        2000L,
                        4L,
                        "BIGINT는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        20,
                        "https://image.test.update.spicy-bigint.jpg",
                        ProductStatus.INACTIVE
                );

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));
                given(categoryRepository.findById(4L))
                        .willReturn(Optional.of(category));
                given(productImageRepository.findByProductId(1L))
                        .willReturn(Optional.of(image));

                // when
                AdminProductDetailResponse response = adminProductService.update(1L, request);

                // then
                assertThat(product.getName()).isEqualTo("수정된 대-정수의 핵불닭맛 감자칩");
                assertThat(product.getPrice()).isEqualTo(2000L);
                assertThat(product.getDescription()).isEqualTo("BIGINT는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩");
                assertThat(product.getStock()).isEqualTo(20);
                assertThat(product.getCategoryId()).isEqualTo(4L);
                assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
                assertThat(image.getAttachmentUrl()).isEqualTo("https://image.test.update.spicy-bigint.jpg");

                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.name()).isEqualTo("수정된 대-정수의 핵불닭맛 감자칩");
                assertThat(response.price()).isEqualTo(2000L);
                assertThat(response.description()).isEqualTo("BIGINT는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩");
                assertThat(response.stock()).isEqualTo(20);
                assertThat(response.categoryId()).isEqualTo(4L);
                assertThat(response.category()).isEqualTo("신규 과자");
                assertThat(response.status()).isEqualTo(ProductStatus.INACTIVE);
                assertThat(response.imageUrl()).isEqualTo("https://image.test.update.spicy-bigint.jpg");
            }
        }

        @Nested
        @DisplayName("Context: 이미 삭제된 상품을 수정하는 경우")
        class Context_deleted_product {

            @Test
            @DisplayName("It: PRODUCT_NOT_FOUND 예외 발생")
            void It_PRODUCT_NOT_FOUND_예외_발생() {
                // given
                Product product = createProduct(
                        1L,
                        "삭제된 대-정수의 핵불닭맛 감자칩",
                        1000L,
                        "대-정수만 먹을 수 있어서 삭제된 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.DELETED
                );

                AdminProductRequest request = createRequest(ProductStatus.ACTIVE);

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));

                // when & then
                assertThatThrownBy(() -> adminProductService.update(1L, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.PRODUCT_NOT_FOUND.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("Describe: changeStatus() 메서드는")
    class Describe_changeStatus {
        @Nested
        @DisplayName("Context: 변경 가능한 상품인 경우")
        class Context_changeable_product {

            @Test
            @DisplayName("It: 상품 상태 변경")
            void It_상품_상태_변경() {
                // given
                Product product = createProduct(
                        1L,
                        "대-정수의 핵불닭맛 감자칩",
                        1000L,
                        "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.ACTIVE
                );

                AdminProductStatusUpdateRequest request =
                        new AdminProductStatusUpdateRequest(ProductStatus.INACTIVE);

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));

                // when
                AdminProductStatusResponse response =
                        adminProductService.changeStatus(1L, request);

                // then
                assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.status()).isEqualTo(ProductStatus.INACTIVE);
            }
        }

        @Nested
        @DisplayName("Context: 이미 삭제된 상품인 경우")
        class Context_deleted_product {

            @Test
            @DisplayName("It: INVALID_PRODUCT_STATUS 예외 발생")
            void It_INVALID_PRODUCT_STATUS_예외_발생() {
                // given
                Product product = createProduct(
                        1L,
                        "삭제된 대-정수의 핵불닭맛 감자칩",
                        1000L,
                        "대-정수만 먹을 수 있어서 삭제된 핵불닭맛의 엄청 매운 감자칩",
                        10,
                        3L,
                        ProductStatus.DELETED
                );

                AdminProductStatusUpdateRequest request =
                        new AdminProductStatusUpdateRequest(ProductStatus.ACTIVE);

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));

                // when & then
                assertThatThrownBy(() -> adminProductService.changeStatus(1L, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.INVALID_PRODUCT_STATUS.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }
    }

    private AdminProductRequest createRequest(ProductStatus status) {
        return new AdminProductRequest(
                "대-정수의 핵불닭맛 감자칩",
                50000L,
                3L,
                "대-정수는 쉽게 먹는 핵불닭맛의 엄청 매운 감자칩",
                10,
                "https://image.test.spicy-bigint.jpg",
                status
        );
    }

    private Product createProduct(
            Long id,
            String name,
            Long price,
            String description,
            Integer stock,
            Long categoryId,
            ProductStatus status
    ) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .stock(stock)
                .categoryId(categoryId)
                .status(status)
                .build();

        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Category createCategory(Long id, String name) {
        Category category = Category.builder()
                .name(name)
                .sortOrder(1)
                .build();

        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

    private ProductImage createProductImage(Long id, Long productId, String attachmentUrl) {
        ProductImage image = ProductImage.builder()
                .productId(productId)
                .attachmentUrl(attachmentUrl)
                .sortOrder(1)
                .build();

        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }

}