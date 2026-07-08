package io.snackdeal.backand.domain.product.repository;

import io.snackdeal.backand.domain.product.entity.Product;
import io.snackdeal.backand.domain.product.entity.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository 클래스의")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("Describe: searchUserProducts() 메서드는")
    class Describe_searchUserProducts {
        @Nested
        @DisplayName("Context: keyword와 categoryId 조건이 있는 경우")
        class Context_with_keyword_and_category {

            @Test
            @DisplayName("It: ACTIVE 상품 중 이름과 카테고리 조건에 맞는 상품만 반환")
            void It_조건에_맞는_ACTIVE_상품만_반환() {
                // given
                Product target = createProduct("대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE, 0L);
                Product differentKeyword = createProduct("대-옥주의 초코 퐁당", 20000L, 15, 3L, ProductStatus.ACTIVE, 0L);
                Product differentCategory = createProduct("대-동현의 롯데리아맛 감자칩", 10000L, 5, 4L, ProductStatus.ACTIVE, 0L);
                Product inactive = createProduct("대-준환의 조청유과 감자칩", 5000L, 20, 3L, ProductStatus.INACTIVE, 0L);

                productRepository.saveAll(List.of(target, differentKeyword, differentCategory, inactive));

                // when
                Page<Product> result = productRepository.searchUserProducts(
                        "감자",
                        3L,
                        ProductStatus.ACTIVE,
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
                );

                // then
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getName()).isEqualTo("대-정수의 핵불닭맛 감자칩");

            }
        }

        @Nested
        @DisplayName("Context: 인기순으로 조회하는 경우")
        class Context_popular_sort {

            @Test
            @DisplayName("It: ACTIVE 상품을 recentSalesCount DESC, createdAt DESC 순으로 반환")
            void It_ACTIVE_상품을_인기순으로_반환() {
                // given
                Product low = createProduct("대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE, 10L);
                Product highOld = createProduct("대-옥주의 초코 퐁당", 20000L, 15, 3L, ProductStatus.ACTIVE, 100L);
                Product highNew = createProduct("대-동현의 롯데리아맛 감자칩", 10000L, 5, 3L, ProductStatus.ACTIVE, 100L);
                Product inactive = createProduct("대-준환의 조청유과 감자칩", 5000L, 20, 3L, ProductStatus.INACTIVE, 1000L);

                setCreatedAt(low, LocalDateTime.of(2026, 1, 1, 0, 0));
                setCreatedAt(highOld, LocalDateTime.of(2026, 1, 1, 0, 0));
                setCreatedAt(highNew, LocalDateTime.of(2026, 1, 2, 0, 0));
                setCreatedAt(inactive, LocalDateTime.of(2026, 1, 3, 0, 0));

                productRepository.saveAll(List.of(low, highOld, highNew, inactive));

                // when
                Page<Product> result = productRepository.searchUserProducts(
                        null,
                        null,
                        ProductStatus.ACTIVE,
                        PageRequest.of(
                                0,
                                10,
                                Sort.by(
                                        Sort.Order.desc("recentSalesCount"),
                                        Sort.Order.desc("createdAt")
                                )
                        )
                );

                // then
                assertThat(result.getContent()).hasSize(3);
                assertThat(result.getContent().get(0).getName()).isEqualTo("대-동현의 롯데리아맛 감자칩");
                assertThat(result.getContent().get(1).getName()).isEqualTo("대-옥주의 초코 퐁당");
                assertThat(result.getContent().get(2).getName()).isEqualTo("대-정수의 핵불닭맛 감자칩");
            }
        }
    }

    @Nested
    @DisplayName("Describe: searchAdminProducts() 메서드는")
    class Describe_searchAdminProducts {
        @Nested
        @DisplayName("Context: lowStock 조건으로 조회하는 경우")
        class Context_low_stock {

            @Test
            @DisplayName("It: DELETED 상품을 제외하고 재고가 10개 이하인 상품만 반환")
            void It_DELETED_제외_재고_부족_상품만_반환() {
                // given
                Product activeLowStock = createProduct("대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE, 10L);
                Product activeEnoughStock = createProduct("대-옥주의 초코 퐁당", 20000L, 15, 3L, ProductStatus.ACTIVE, 100L);
                Product inactiveLowStock = createProduct("대-동현의 롯데리아맛 감자칩", 10000L, 5, 3L, ProductStatus.INACTIVE, 100L);
                Product deletedLowStock = createProduct("대-준환의 조청유과 감자칩", 5000L, 2, 3L, ProductStatus.DELETED, 1000L);

                productRepository.saveAll(List.of(
                        activeLowStock,
                        activeEnoughStock,
                        inactiveLowStock,
                        deletedLowStock
                ));

                // when
                Page<Product> result = productRepository.searchAdminProducts(
                        null,
                        null,
                        null,
                        true,
                        ProductStatus.DELETED,
                        PageRequest.of(0, 10)
                );

                // then
                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getContent().get(0).getName()).isEqualTo("대-정수의 핵불닭맛 감자칩");
                assertThat(result.getContent().get(1).getName()).isEqualTo("대-동현의 롯데리아맛 감자칩");
            }
        }

        @Nested
        @DisplayName("Context: keyword, categoryId, status 조건이 있는 경우")
        class Context_with_filters {

            @Test
            @DisplayName("It: DELETED 상품을 제외하고 조건에 맞는 상품만 반환")
            void It_조건에_맞는_상품만_반환() {
                // given
                Product target= createProduct("대-정수의 핵불닭맛 감자칩", 50000L, 0, 3L, ProductStatus.ACTIVE, 10L);
                Product inactive  = createProduct("대-동현의 롯데리아맛 감자칩", 10000L, 5, 3L, ProductStatus.INACTIVE, 100L);
                Product differentKeyword = createProduct("대-옥주의 초코 퐁당", 20000L, 15, 3L, ProductStatus.ACTIVE, 100L);
                Product differentCategory = createProduct("대-준환의 조청유과 감자칩", 5000L, 2, 4L, ProductStatus.ACTIVE, 1000L);
                Product deleted = createProduct("대-선의 초코 감자칩", 5000L, 2, 3L, ProductStatus.DELETED, 1000L);

                productRepository.saveAll(List.of(
                        target,
                        inactive,
                        differentKeyword,
                        differentCategory,
                        deleted
                ));

                // when
                Page<Product> result = productRepository.searchAdminProducts(
                        "감자",
                        3L,
                        ProductStatus.ACTIVE,
                        false,
                        ProductStatus.DELETED,
                        PageRequest.of(0, 10)
                );

                // then
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getName()).isEqualTo("대-정수의 핵불닭맛 감자칩");
            }
        }
    }

    private Product createProduct(
            String name,
            Long price,
            Integer stock,
            Long categoryId,
            ProductStatus status,
            Long recentSalesCount
    ) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description("상품 설명")
                .stock(stock)
                .categoryId(categoryId)
                .status(status)
                .build();

        ReflectionTestUtils.setField(product, "recentSalesCount", recentSalesCount);

        return product;
    }

    private void setCreatedAt(Product product, LocalDateTime createdAt) {
        ReflectionTestUtils.setField(product, "createdAt", createdAt);
    }
}