package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class AdminCouponService {

    public Object findList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object save(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object update(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object changeStatus(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findBoardList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object saveBoard(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object updateBoard(Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void deleteBoard(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
