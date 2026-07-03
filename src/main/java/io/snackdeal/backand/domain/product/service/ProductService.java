package io.snackdeal.backand.domain.product.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findById(Long productId) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
