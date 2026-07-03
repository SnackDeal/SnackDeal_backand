package io.snackdeal.backand.domain.category.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoryService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
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
