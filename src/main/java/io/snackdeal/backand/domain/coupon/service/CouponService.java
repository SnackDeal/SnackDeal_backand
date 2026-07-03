package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public Object findEventCouponList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object download(String email, Long couponId) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findMyCoupons(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
