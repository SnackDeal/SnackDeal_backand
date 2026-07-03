package io.snackdeal.backand.global.config.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // Success
    SUCCESS(HttpStatus.OK, "S000", "성공"),

    // Member / Auth
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 사용 중인 이메일입니다."),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "M002", "이미 존재하는 회원 정보입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M003", "존재하지 않는 회원입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "M004", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "M005", "인증코드가 일치하지 않습니다."),
    EMAIL_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "M006", "인증코드가 만료되었습니다."),
    EMAIL_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "M007", "유효하지 않거나 만료된 인증 토큰입니다."),

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A001", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A002", "존재하지 않는 사용자입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 RefreshToken입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "만료되었거나 로그아웃된 RefreshToken입니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "A005", "제공된 RefreshToken이 현재 유효한 RefreshToken과 일치하지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A006", "인증되지 않은 접근입니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "A007", "권한이 없습니다."),

    // Common
    INPUT_REQUIRED(HttpStatus.BAD_REQUEST, "C001", "입력값이 필요합니다."),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "C002", "아직 구현되지 않은 기능입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "C003", "입력값이 유효하지 않습니다."),

    // Product / Category
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PR001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "PR002", "재고가 부족합니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "PR003", "카테고리를 찾을 수 없습니다."),

    // Cart
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "장바구니 항목을 찾을 수 없습니다."),

    // Order / Delivery
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "OR001", "주문을 찾을 수 없습니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "OR002", "취소할 수 없는 주문 상태입니다."),
    EMPTY_CART(HttpStatus.BAD_REQUEST, "OR003", "장바구니가 비어있습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "OR004", "배송지를 찾을 수 없습니다."),

    // Coupon
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "쿠폰을 찾을 수 없습니다."),
    COUPON_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "CO002", "이벤트게시판을 찾을 수 없습니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "CO003", "이미 발급받은 쿠폰입니다."),
    COUPON_SOLD_OUT(HttpStatus.BAD_REQUEST, "CO004", "쿠폰 수량이 모두 소진되었습니다."),

    // CS (notice/faq/qna)
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "CS001", "공지사항을 찾을 수 없습니다."),
    QNA_NOT_FOUND(HttpStatus.NOT_FOUND, "CS002", "문의글을 찾을 수 없습니다."),
    UNAUTHORIZED_QNA_UPDATE(HttpStatus.FORBIDDEN, "CS003", "본인 문의만 수정/삭제할 수 있습니다."),
    QNA_ALREADY_ANSWERED(HttpStatus.BAD_REQUEST, "CS004", "답변이 완료된 문의는 수정/삭제할 수 없습니다."),

    // Server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 내부 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
