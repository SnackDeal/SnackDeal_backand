package io.snackdeal.backand.global.config.dto;

import io.snackdeal.backand.global.config.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return of(ResponseCode.SUCCESS, data);
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
