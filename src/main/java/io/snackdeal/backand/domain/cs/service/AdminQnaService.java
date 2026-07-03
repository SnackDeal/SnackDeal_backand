package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AdminQnaService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findById(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object answer(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
