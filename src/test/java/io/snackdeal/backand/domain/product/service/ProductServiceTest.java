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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 클래스의")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

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
            @DisplayName("It: 상품 목록을 카테고리명, 썸네일 URL, 품절 여부와 함께 반환")
            void It_상품_목록을_반환() {
                // given
                Product product = createProduct(1L, "대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE);
                Product product2 = createProduct(2L, "대-동현의 롯데리아맛 감자칩", 10000L, 5, 3L, ProductStatus.ACTIVE);
                Product product3 = createProduct(3L, "대-옥주의 초코 퐁당", 20000L, 15, 3L, ProductStatus.ACTIVE);
                Product product4 = createProduct(4L, "대-준환의 조청유과", 5000L, 20, 4L, ProductStatus.INACTIVE);

                Category category = createCategory(3L, "과자");
                Category category2 = createCategory(4L, "한과");

                ProductImage image = createProductImage(10L, 1L, "https://image.test.spicy-bigint.jpg");
                ProductImage image2 = createProductImage(11L, 2L, "https://image.test.lotteria-potato.jpg");
                ProductImage image3 = createProductImage(12L, 3L, "https://image.test.okju-choco.jpg");
                ProductImage image4 = createProductImage(13L, 4L, "https://image.test.junhwan-korea.jpg");

                given(productRepository.searchUserProducts(
                        eq("감자"),
                        eq(3L),
                        eq(ProductStatus.ACTIVE),
                        any(Pageable.class)
                )).willReturn(new PageImpl<>(List.of(product, product2), PageRequest.of(0, 10), 2));

                given(categoryRepository.findAllById(List.of(3L)))
                        .willReturn(List.of(category));

                given(productImageRepository.findByProductIdIn(List.of(1L, 2L)))
                        .willReturn(List.of(image, image2));

                // when
                PageResponse<ProductSummaryResponse> response =
                        productService.findList(" 감자 ", 3L, "latest", 1, 10);

                // then
                assertThat(response.items()).hasSize(2);
                assertThat(response.page()).isEqualTo(1);
                assertThat(response.size()).isEqualTo(10);
                assertThat(response.total()).isEqualTo(2);

                ProductSummaryResponse item1 = response.items().get(0);
                assertThat(item1.id()).isEqualTo(1L);
                assertThat(item1.name()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(item1.thumbnailUrl()).isEqualTo("https://image.test.spicy-bigint.jpg");
                assertThat(item1.categoryId()).isEqualTo(3L);
                assertThat(item1.category()).isEqualTo("과자");
                assertThat(item1.isSoldout()).isTrue();

                ProductSummaryResponse item2 = response.items().get(1);
                assertThat(item2.id()).isEqualTo(2L);
                assertThat(item2.name()).isEqualTo("대-동현의 롯데리아맛 감자칩");
                assertThat(item2.thumbnailUrl()).isEqualTo("https://image.test.lotteria-potato.jpg");
                assertThat(item2.categoryId()).isEqualTo(3L);
                assertThat(item2.category()).isEqualTo("과자");
                assertThat(item2.isSoldout()).isFalse();

            }
        }

        @Nested
        @DisplayName("Context: 조회 결과가 없는 경우")
        class Context_without_products {

            @Test
            @DisplayName("It: 빈 목록을 반환하고 카테고리와 이미지 조회는 하지 않는다")
            void It_빈_목록_반환() {
                // given
                given(productRepository.searchUserProducts(
                        isNull(),
                        isNull(),
                        eq(ProductStatus.ACTIVE),
                        any(Pageable.class)
                )).willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

                // when
                PageResponse<ProductSummaryResponse> response =
                        productService.findList(null, null, "latest", 1, 10);

                // then
                assertThat(response.items()).isEmpty();
                assertThat(response.page()).isEqualTo(1);
                assertThat(response.size()).isEqualTo(10);
                assertThat(response.total()).isEqualTo(0);

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }

        @Nested
        @DisplayName("Context: 페이지 요청 값이 올바르지 않은 경우")
        class Context_invalid_page_request {

            @Test
            @DisplayName("It: INVALID_PAGE_REQUEST 예외 발생")
            void It_INVALID_PAGE_REQUEST_예외_발생() {
                // given & when & then
                assertThatThrownBy(() -> productService.findList(null, null, "latest", 0, 10))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.INVALID_PAGE_REQUEST.getMessage());

                then(productRepository).shouldHaveNoInteractions();
            }
        }

        @Nested
        @DisplayName("Context: 정렬 조건이 올바르지 않은 경우")
        class Context_invalid_sort {

            @Test
            @DisplayName("It: INVALID_PRODUCT_SORT 예외 발생")
            void It_INVALID_PRODUCT_SORT_예외_발생() {
                // given & when & then
                assertThatThrownBy(() -> productService.findList(null, null, "wrong", 1, 10))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.INVALID_PRODUCT_SORT.getMessage());

                then(productRepository).shouldHaveNoInteractions();
            }
        }
    }

    @Nested
    @DisplayName("Describe: findById() 메서드는")
    class Describe_findById {

        @Nested
        @DisplayName("Context: ACTIVE 상품이 존재하는 경우")
        class Context_active_product {

            @Test
            @DisplayName("It: 상품 상세 정보 반환")
            void It_상품_상세를_반환() {
                // given
                Product product = createProduct(1L, "대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE);
                Category category = createCategory(3L, "과자");
                ProductImage image = createProductImage(10L, 1L, "https://image.test.spicy-bigint.jpg");

                given(productRepository.findById(1L))
                        .willReturn(Optional.of(product));
                given(categoryRepository.findById(3L))
                        .willReturn(Optional.of(category));
                given(productImageRepository.findByProductId(1L))
                        .willReturn(Optional.of(image));

                // when
                ProductResponse response = productService.findById(1L);

                // then
                assertThat(response.id()).isEqualTo(1L);
                assertThat(response.name()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(response.price()).isEqualTo(50000L);
                assertThat(response.description()).isEqualTo("상품 설명");
                assertThat(response.imageUrl()).isEqualTo("https://image.test.spicy-bigint.jpg");
                assertThat(response.stock()).isEqualTo(0);
                assertThat(response.status()).isEqualTo(ProductStatus.ACTIVE);
                assertThat(response.isSoldout()).isTrue();
                assertThat(response.categoryId()).isEqualTo(3L);
                assertThat(response.category()).isEqualTo("과자");
            }
        }

        @Nested
        @DisplayName("Context: 상품이 존재하지 않는 경우")
        class Context_missing_product {

            @Test
            @DisplayName("It: PRODUCT_NOT_FOUND 예외 발생")
            void It_PRODUCT_NOT_FOUND_예외_발생() {
                // given
                given(productRepository.findById(999L))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> productService.findById(999L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.PRODUCT_NOT_FOUND.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();
            }
        }

        @Nested
        @DisplayName("Context: 상품이 판매중 상태가 아닌 경우")
        class Context_not_active_product {

            @Test
            @DisplayName("It: PRODUCT_NOT_FOUND 예외 발생")
            void It_PRODUCT_NOT_FOUND_예외_발생() {
                // given
                Product product = createProduct(5L, "판매중지된 대-정수의 핵불닭맛 감자칩",50000L, 0, 3L, ProductStatus.INACTIVE);

                given(productRepository.findById(5L))
                        .willReturn(Optional.of(product));

                // when & then
                assertThatThrownBy(() -> productService.findById(5L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.PRODUCT_NOT_FOUND.getMessage());

                then(categoryRepository).shouldHaveNoInteractions();
                then(productImageRepository).shouldHaveNoInteractions();

            }
        }
    }

    private Product createProduct(Long id, String name, Long price, Integer stock,
                                  Long categoryId, ProductStatus status) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description("상품 설명")
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