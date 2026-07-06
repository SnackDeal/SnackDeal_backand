package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.api.admin.category.dto.CategoryRequest;
import io.snackdeal.backand.api.admin.category.dto.CategoryResponse;
import io.snackdeal.backand.domain.category.entity.Category;
import io.snackdeal.backand.domain.category.repository.CategoryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findList() {
        return categoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAscIdAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse save(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException(ResponseCode.DUPLICATE_CATEGORY);
        }

        Category category = Category.builder()
                .name(request.name())
                .sortOrder(request.sortOrder())
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException(ResponseCode.DUPLICATE_CATEGORY);
        }

        category.update(request.name(), request.sortOrder());

        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));

        category.delete();
    }
}
