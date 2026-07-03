package io.snackdeal.backand.domain.cart.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    public Object findCart(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object addItem(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object updateQuantity(String email, Long itemId, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void delete(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
