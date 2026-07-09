package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.domain.coupon.repository.CouponBoardRepository;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponBoardRepository couponBoardRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findEventCouponList - TODO")
    void findEventCouponList_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("download - TODO")
    void download_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findMyCoupons - TODO")
    void findMyCoupons_Success() {
        fail("not implemented");
    }

}
