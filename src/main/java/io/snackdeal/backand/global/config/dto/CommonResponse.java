package io.snackdeal.backand.global.config.dto;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 모든 API 응답을 감싸는 공통 래퍼 data 안에 각 API 의 실제 payload 가 들어간다.
 */
@Schema(description = "공통 응답 래퍼")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 코드(성공: S000)", example = "S000")
    private String code;

    @Schema(description = "응답 메시지", example = "성공")
    private String message;

    @Schema(description = "실제 응답 데이터(API 별로 다름)")
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return of(ResponseCode.SUCCESS, data);
    }

    public static <T> CommonResponse<T> created(T data) {
        return of(ResponseCode.CREATED, data);
    }

    public static <T> CommonResponse<T> successWithMessage(T data, ResponseCode responseCode) {
        return CommonResponse.<T>builder()
                .success(true)
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> of(ResponseCode responseCode, T data) {
        return CommonResponse.<T>builder()
                .success(responseCode.getHttpStatus().is2xxSuccessful())
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> fail(ResponseCode responseCode) {
        return fail(responseCode, responseCode.getMessage());
    }

    public static <T> CommonResponse<T> fail(ResponseCode responseCode, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .code(responseCode.getCode())
                .message(message)
                .data(null)
                .build();
    }
}
