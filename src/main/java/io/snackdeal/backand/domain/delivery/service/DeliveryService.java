package io.snackdeal.backand.domain.delivery.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    public Object findList(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object save(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object update(String email, Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object markDefault(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void delete(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
