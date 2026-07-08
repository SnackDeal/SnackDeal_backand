package io.snackdeal.backand.domain.coupon.service;

import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponBoardUpdateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponCreateResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponListResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponStatusUpdateRequest;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponSummaryResponse;
import io.snackdeal.backand.api.admin.coupon.dto.AdminCouponUpdateRequest;
import io.snackdeal.backand.domain.coupon.entity.Coupon;
import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;
import io.snackdeal.backand.domain.coupon.repository.CouponBoardRepository;
import io.snackdeal.backand.domain.coupon.repository.CouponRepository;
import io.snackdeal.backand.domain.coupon.repository.UserCouponRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponBoardRepository couponBoardRepository;

    public AdminCouponListResponse findList(String keyword, IssueType issueType, CouponStatus status, int page, int size) {
        validatePageRequest(page, size);

        LocalDateTime now = LocalDateTime.now();
        Page<Coupon> couponPage = couponRepository.searchAdminCoupons(
                normalizeKeyword(keyword),
                issueType,
                status == null ? null : status.name(),
                now,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Map<Long, String> boardTitleMap = getBoardTitleMap(couponPage.getContent());

        List<AdminCouponSummaryResponse> coupons = couponPage.getContent().stream()
                .map(coupon -> toSummaryResponse(coupon, boardTitleMap.get(coupon.getCouponBoardId()), now))
                .toList();

        return new AdminCouponListResponse(coupons, page, size, couponPage.getTotalElements());
    }

    @Transactional
    public AdminCouponCreateResponse save(AdminCouponCreateRequest request) {
        validateCouponPolicy(request);

        if (request.couponBoardId() != null) {
            findBoard(request.couponBoardId());
        }

        Coupon coupon = Coupon.builder()
                .name(request.name())
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .minOrderPrice(request.minOrderPrice())
                .validFrom(request.validFrom())
                .validUntil(request.validUntil())
                .totalQuantity(request.totalQuantity())
                .issueType(request.issueType())
                .couponBoardId(request.couponBoardId())
                .isActive(request.isActive())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        LocalDateTime now = LocalDateTime.now();
        return new AdminCouponCreateResponse(
                savedCoupon.getId(),
                savedCoupon.getName(),
                savedCoupon.isActive(),
                savedCoupon.deriveStatus(now),
                savedCoupon.getCouponBoardId(),
                savedCoupon.getCreatedAt()
        );
    }

    @Transactional
    public AdminCouponSummaryResponse update(Long id, AdminCouponUpdateRequest request) {
        Coupon coupon = findCoupon(id);
        validateCouponUpdateRequest(request);

        if (request.name() != null) {
            coupon.changeName(request.name().trim());
        }
        if (request.validUntil() != null) {
            validateValidUntilExtension(coupon, request.validUntil());
            coupon.extendValidUntil(request.validUntil());
        }
        if (request.totalQuantity() != null) {
            validateTotalQuantityIncrease(coupon, request.totalQuantity());
            coupon.increaseTotalQuantity(request.totalQuantity());
        }

        LocalDateTime now = LocalDateTime.now();
        String boardTitle = getBoardTitleMap(List.of(coupon)).get(coupon.getCouponBoardId());
        return toSummaryResponse(coupon, boardTitle, now);
    }

    @Transactional
    public AdminCouponStatusResponse changeStatus(Long id, AdminCouponStatusUpdateRequest request) {
        Coupon coupon = findCoupon(id);
        coupon.changeActive(request.isActive());

        return new AdminCouponStatusResponse(
                coupon.getId(),
                coupon.isActive(),
                coupon.deriveStatus(LocalDateTime.now()),
                coupon.getUpdatedAt()
        );
    }

    public AdminCouponBoardListResponse findBoardList() {
        List<AdminCouponBoardResponse> boards = couponBoardRepository
                .findByDeletedAtIsNull(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toBoardResponse)
                .toList();

        return new AdminCouponBoardListResponse(boards);
    }

    @Transactional
    public AdminCouponBoardResponse saveBoard(AdminCouponBoardCreateRequest request) {
        validatePeriod(request.startAt(), request.endAt());

        CouponBoard board = CouponBoard.builder()
                .title(request.title())
                .content(request.content())
                .thumbnailUrl(request.thumbnailUrl())
                .isActive(request.isActive())
                .startAt(request.startAt())
                .endAt(request.endAt())
                .build();

        return toBoardResponse(couponBoardRepository.save(board));
    }

    @Transactional
    public AdminCouponBoardResponse updateBoard(Long id, AdminCouponBoardUpdateRequest request) {
        CouponBoard board = findBoard(id);
        validatePeriod(request.startAt(), request.endAt());

        board.update(
                request.title(),
                request.content(),
                request.thumbnailUrl(),
                request.isActive(),
                request.startAt(),
                request.endAt()
        );

        return toBoardResponse(board);
    }

    @Transactional
    public void deleteBoard(Long id) {
        CouponBoard board = findBoard(id);
        if (couponRepository.existsByCouponBoardIdAndDeletedAtIsNull(board.getId())) {
            throw new BusinessException(ResponseCode.COUPON_BOARD_HAS_COUPONS);
        }
        board.delete();
    }

    private void validateCouponPolicy(AdminCouponCreateRequest request) {
        if (request.discountType() == DiscountType.PERCENT
                && (request.discountValue() < 1 || request.discountValue() > 100)) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY, "정률 할인값은 1~100 사이여야 합니다.");
        }
        if (request.discountType() == DiscountType.FIXED && request.discountValue() <= 0) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY, "정액 할인값은 양수여야 합니다.");
        }
        if (request.totalQuantity() != null && request.totalQuantity() < 0) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY, "발급 수량은 음수일 수 없습니다.");
        }
        validatePeriod(request.validFrom(), request.validUntil());
        if (request.issueType() == IssueType.EVENT && request.couponBoardId() == null) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY, "EVENT 쿠폰은 couponBoardId가 필수입니다.");
        }
    }

    private void validatePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        if (endAt != null && endAt.isBefore(startAt)) {
            throw new BusinessException(ResponseCode.INVALID_COUPON_POLICY, "종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private void validatePageRequest(int page, int size) {
        if (page < 1 || size < 1) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "page와 size는 1 이상이어야 합니다.");
        }
    }

    private void validateCouponUpdateRequest(AdminCouponUpdateRequest request) {
        if (request.name() == null && request.validUntil() == null && request.totalQuantity() == null) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "변경할 쿠폰 정보가 없습니다.");
        }
        if (request.name() != null && request.name().isBlank()) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "쿠폰명은 공백일 수 없습니다.");
        }
    }

    private void validateValidUntilExtension(Coupon coupon, LocalDateTime requestedValidUntil) {
        if (coupon.getValidUntil() == null || requestedValidUntil.isBefore(coupon.getValidUntil())) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "유효기간 종료일은 단축할 수 없습니다.");
        }
    }

    private void validateTotalQuantityIncrease(Coupon coupon, Integer requestedTotalQuantity) {
        if (requestedTotalQuantity < 0) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "발급 수량은 음수일 수 없습니다.");
        }

        int currentTotalQuantity = coupon.getTotalQuantity() == null ? 0 : coupon.getTotalQuantity();
        int issuedQuantity = coupon.getIssuedQuantity() == null ? 0 : coupon.getIssuedQuantity();

        if (requestedTotalQuantity > 0 && requestedTotalQuantity < issuedQuantity) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "발급 수량은 이미 발급된 수량보다 작을 수 없습니다.");
        }
        if (currentTotalQuantity == 0 && requestedTotalQuantity > 0) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "무제한 쿠폰은 제한 수량으로 변경할 수 없습니다.");
        }
        if (currentTotalQuantity > 0 && requestedTotalQuantity > 0 && requestedTotalQuantity < currentTotalQuantity) {
            throw new BusinessException(ResponseCode.VALIDATION_FAILED, "발급 수량은 감소시킬 수 없습니다.");
        }
    }

    private Coupon findCoupon(Long id) {
        return couponRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_NOT_FOUND));
    }

    private CouponBoard findBoard(Long id) {
        return couponBoardRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.COUPON_BOARD_NOT_FOUND));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private Map<Long, String> getBoardTitleMap(List<Coupon> coupons) {
        List<Long> boardIds = coupons.stream()
                .map(Coupon::getCouponBoardId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (boardIds.isEmpty()) {
            return Map.of();
        }

        return couponBoardRepository.findAllById(boardIds)
                .stream()
                .collect(Collectors.toMap(CouponBoard::getId, CouponBoard::getTitle));
    }

    private AdminCouponSummaryResponse toSummaryResponse(Coupon coupon, String boardTitle, LocalDateTime now) {
        return new AdminCouponSummaryResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderPrice(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.getIssueType(),
                coupon.getCouponBoardId(),
                boardTitle,
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                userCouponRepository.countByCouponIdAndStatus(coupon.getId(), UserCouponStatus.USED),
                coupon.isActive(),
                coupon.deriveStatus(now)
        );
    }

    private AdminCouponBoardResponse toBoardResponse(CouponBoard board) {
        return new AdminCouponBoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getThumbnailUrl(),
                board.isActive(),
                board.getStartAt(),
                board.getEndAt(),
                board.getCreatedAt()
        );
    }
}
