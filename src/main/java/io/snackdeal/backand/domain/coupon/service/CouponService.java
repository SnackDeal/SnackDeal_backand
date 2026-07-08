package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.api.user.coupon.dto.CouponDownloadResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardListResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponBoardSummaryResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponDetailResponse;
import io.snackdeal.backand.api.user.coupon.dto.EventCouponResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponListResponse;
import io.snackdeal.backand.api.user.coupon.dto.MyCouponResponse;
import io.snackdeal.backand.domain.coupon.dto.CouponDiscountResult;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCoupon;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.repository.CouponBoardRepository;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private static final String STATE_CLOSED = "closed";
    private static final String STATE_SOLD_OUT = "soldout";
    private static final String STATE_UPCOMING = "upcoming";
    private static final String STATE_OPEN = "open";

    private final CouponRepository couponRepository;
    private final CouponBoardRepository couponBoardRepository;
    private final UserCouponRepository userCouponRepository;

    public EventCouponBoardListResponse findEventCouponBoards() {
        LocalDateTime now = LocalDateTime.now();
        List<EventCouponBoardSummaryResponse> boards = couponBoardRepository.findOpenBoards(now)
                .stream()
                .map(this::toBoardSummaryResponse)
                .toList();

        return new EventCouponBoardListResponse(boards);
    }

    public EventCouponDetailResponse findEventCouponDetail(Long memberId, Long boardId) {
        LocalDateTime now = LocalDateTime.now();
        CouponBoard board = couponBoardRepository.findOpenBoardById(boardId, now)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_BOARD_NOT_FOUND));
        List<Coupon> coupons = couponRepository.findByCouponBoardIdAndIssueTypeAndIsActiveTrueAndDeletedAtIsNull(
                board.getId(),
                IssueType.EVENT
        );

        Set<Long> downloadedCouponIds = getDownloadedCouponIds(memberId, coupons);

        List<EventCouponResponse> couponResponses = coupons.stream()
                .map(coupon -> toEventCouponResponse(coupon, downloadedCouponIds.contains(coupon.getId()), now))
                .toList();

        return new EventCouponDetailResponse(
                toBoardDetailResponse(board),
                couponResponses
        );
    }

    @Transactional
    public CouponDownloadResponse download(Long memberId, Long couponId) {
        Coupon coupon = couponRepository.findByIdForUpdate(couponId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));

        validateDownloadable(coupon);

        if (userCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId())) {
            throw new BusinessException(ResponseCode.COUPON_ALREADY_ISSUED);
        }

        coupon.increaseIssuedQuantity();
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.builder()
                .memberId(memberId)
                .couponId(coupon.getId())
                .build());

        return new CouponDownloadResponse(
                userCoupon.getId(),
                coupon.getId(),
                coupon.getName(),
                userCoupon.getStatus(),
                userCoupon.getIssuedAt()
        );
    }

    public MyCouponListResponse findMyCoupons(Long memberId, UserCouponStatus status) {
        List<UserCoupon> userCoupons = userCouponRepository.findByMemberId(memberId);
        if (userCoupons.isEmpty()) {
            return new MyCouponListResponse(List.of());
        }

        Map<Long, Coupon> couponMap = couponRepository.findAllById(
                        userCoupons.stream()
                                .map(UserCoupon::getCouponId)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        List<MyCouponResponse> coupons = userCoupons.stream()
                .map(userCoupon -> toMyCouponResponse(userCoupon, couponMap.get(userCoupon.getCouponId()), now))
                .filter(response -> response != null)
                .filter(response -> status == null || response.status() == status)
                .toList();

        return new MyCouponListResponse(coupons);
    }

    public CouponDiscountResult calculateDiscountForOrder(Long memberId, Long userCouponId, Long orderAmount) {
        validateOrderAmount(orderAmount);
        if (userCouponId == null) {
            return CouponDiscountResult.none(orderAmount);
        }

        validateMemberIdForCoupon(memberId);
        UserCoupon userCoupon = userCouponRepository.findByIdAndMemberId(userCouponId, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET));

        return validateAndCalculateForOrder(userCoupon, orderAmount, LocalDateTime.now());
    }

    @Transactional
    public CouponDiscountResult useCouponForOrder(Long memberId, Long userCouponId, Long orderAmount) {
        validateOrderAmount(orderAmount);
        if (userCouponId == null) {
            return CouponDiscountResult.none(orderAmount);
        }

        validateMemberIdForCoupon(memberId);
        UserCoupon userCoupon = userCouponRepository.findByIdAndMemberIdForUpdate(userCouponId, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET));

        CouponDiscountResult result = validateAndCalculateForOrder(userCoupon, orderAmount, LocalDateTime.now());
        userCoupon.use();
        return result;
    }

    public String resolveCouponName(Long userCouponId) {
        if (userCouponId == null) {
            return null;
        }

        return userCouponRepository.findById(userCouponId)
                .flatMap(userCoupon -> couponRepository.findByIdAndDeletedAtIsNull(userCoupon.getCouponId()))
                .map(Coupon::getName)
                .orElse(null);
    }

    @Transactional
    public void issueSigninCoupons(Long memberId) {
        if (memberId == null) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findIssuableCouponsForUpdate(IssueType.SIGNIN, now);
        for (Coupon coupon : coupons) {
            if (userCouponRepository.existsByMemberIdAndCouponId(memberId, coupon.getId())) {
                continue;
            }

            try {
                coupon.increaseIssuedQuantity();
            } catch (BusinessException e) {
                if (e.getResponseCode() == ResponseCode.COUPON_SOLD_OUT) {
                    continue;
                }
                throw e;
            }

            userCouponRepository.save(UserCoupon.builder()
                    .memberId(memberId)
                    .couponId(coupon.getId())
                    .build());
        }
    }

    private Set<Long> getDownloadedCouponIds(Long memberId, List<Coupon> coupons) {
        if (memberId == null || coupons.isEmpty()) {
            return Set.of();
        }

        List<Long> couponIds = coupons.stream()
                .map(Coupon::getId)
                .toList();

        return userCouponRepository.findByMemberIdAndCouponIdIn(memberId, couponIds)
                .stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toSet());
    }

    private EventCouponResponse toEventCouponResponse(Coupon coupon, boolean alreadyDownloaded, LocalDateTime now) {
        return new EventCouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderPrice(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                calculateRemainingQuantity(coupon),
                calculateState(coupon, now),
                alreadyDownloaded
        );
    }

    private EventCouponBoardSummaryResponse toBoardSummaryResponse(CouponBoard board) {
        return new EventCouponBoardSummaryResponse(
                board.getId(),
                board.getTitle(),
                board.getThumbnailUrl(),
                board.getStartAt(),
                board.getEndAt()
        );
    }

    private EventCouponBoardDetailResponse toBoardDetailResponse(CouponBoard board) {
        return new EventCouponBoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getThumbnailUrl(),
                board.getStartAt(),
                board.getEndAt()
        );
    }

    private MyCouponResponse toMyCouponResponse(UserCoupon userCoupon, Coupon coupon, LocalDateTime now) {
        if (coupon == null) {
            return null;
        }

        return new MyCouponResponse(
                userCoupon.getId(),
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderPrice(),
                coupon.getValidUntil(),
                coupon.getIssueType(),
                deriveUserCouponStatus(userCoupon, coupon, now),
                userCoupon.getIssuedAt(),
                userCoupon.getUsedAt()
        );
    }

    private void validateDownloadable(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();

        if (coupon.getIssueType() != IssueType.EVENT) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }
        if (!coupon.isActive()) {
            throw new BusinessException(ResponseCode.COUPON_NOT_ACTIVE);
        }
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new BusinessException(ResponseCode.COUPON_NOT_OPEN);
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }
        if (coupon.hasLimitedQuantity() && coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            throw new BusinessException(ResponseCode.COUPON_SOLD_OUT);
        }
    }

    private Integer calculateRemainingQuantity(Coupon coupon) {
        if (!coupon.hasLimitedQuantity()) {
            return null;
        }
        return Math.max(coupon.getTotalQuantity() - coupon.getIssuedQuantity(), 0);
    }

    private String calculateState(Coupon coupon, LocalDateTime now) {
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return STATE_CLOSED;
        }
        if (coupon.hasLimitedQuantity() && coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            return STATE_SOLD_OUT;
        }
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return STATE_UPCOMING;
        }
        return STATE_OPEN;
    }

    private UserCouponStatus deriveUserCouponStatus(UserCoupon userCoupon, Coupon coupon, LocalDateTime now) {
        if (userCoupon.getStatus() == UserCouponStatus.USED) {
            return UserCouponStatus.USED;
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return UserCouponStatus.EXPIRED;
        }
        return UserCouponStatus.ACTIVE;
    }

    private CouponDiscountResult validateAndCalculateForOrder(UserCoupon userCoupon,
                                                              Long orderAmount,
                                                              LocalDateTime now) {
        if (userCoupon.getStatus() != UserCouponStatus.ACTIVE) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }

        Coupon coupon = findCoupon(userCoupon.getCouponId());
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }

        long minOrderPrice = coupon.getMinOrderPrice() == null ? 0L : coupon.getMinOrderPrice();
        if (orderAmount < minOrderPrice) {
            throw new BusinessException(ResponseCode.COUPON_CONDITION_NOT_MET);
        }

        long discountAmount = calculateDiscountAmount(coupon, orderAmount);
        return new CouponDiscountResult(
                userCoupon.getId(),
                coupon.getId(),
                coupon.getName(),
                discountAmount,
                orderAmount - discountAmount
        );
    }

    private Coupon findCoupon(Long couponId) {
        return couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));
    }

    private long calculateDiscountAmount(Coupon coupon, Long orderAmount) {
        DiscountType discountType = coupon.getDiscountType();
        Long discountValue = coupon.getDiscountValue();
        if (discountType == null || discountValue == null) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY);
        }

        long discountAmount;
        if (discountType == DiscountType.FIXED) {
            if (discountValue <= 0) {
                throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY);
            }
            discountAmount = discountValue;
        } else if (discountType == DiscountType.PERCENT) {
            if (discountValue < 1 || discountValue > 100) {
                throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY);
            }
            discountAmount = orderAmount * discountValue / 100;
        } else {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY);
        }

        return Math.min(discountAmount, orderAmount);
    }

    private void validateOrderAmount(Long orderAmount) {
        if (orderAmount == null || orderAmount < 0) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED);
        }
    }

    private void validateMemberIdForCoupon(Long memberId) {
        if (memberId == null) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED);
        }
    }
}
