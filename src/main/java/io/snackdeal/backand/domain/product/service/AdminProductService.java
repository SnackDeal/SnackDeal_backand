package io.snackdeal.backand.domain.product.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AdminProductService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object save(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findById(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object update(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object changeStatus(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
