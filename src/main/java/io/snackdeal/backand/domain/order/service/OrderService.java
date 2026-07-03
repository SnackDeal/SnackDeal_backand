package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public Object prepare(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object complete(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findList(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findById(String email, Long orderId) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object refund(String email, Long orderId) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
