package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.api.admin.category.dto.CategoryOrderRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findList() {
        return categoryRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public void updateOrder(CategoryOrderRequest request) {
        List<CategoryOrderRequest.CategoryOrderItem> items = request.categoryOrders();

        Set<Long> categoryIds = new HashSet<>();
        Set<Integer> sortOrders = new HashSet<>();

        for (CategoryOrderRequest.CategoryOrderItem item : items) {
            if (item.categoryId() == null || item.sortOrder() == null || item.sortOrder() < 0) {
                throw new BusinessException(ResponseCode.VALIDATION_FAILED);
            }
            if (!categoryIds.add(item.categoryId())) {
                throw new BusinessException(ResponseCode.DUPLICATE_CATEGORY_ORDER_ID);
            }
            if (!sortOrders.add(item.sortOrder())) {
                throw new BusinessException(ResponseCode.DUPLICATE_CATEGORY_SORT_ORDER);
            }
        }

        if (categoryRepository.count() != items.size()) {
            throw new BusinessException(ResponseCode.CATEGORY_ORDER_SIZE_MISMATCH);
        }

        for (CategoryOrderRequest.CategoryOrderItem item : items) {
            Category category = categoryRepository.findById(item.categoryId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));
            category.setSortOrder(item.sortOrder());
        }
    }

    public Object save(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object update(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void delete(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
