package io.snackdeal.backand.domain.order.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findById(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object changeStatus(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object refund(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
