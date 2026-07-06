package io.snackdeal.backand.api.user.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "배송지 등록/수정 요청")
public record DeliveryRequest(
        @Schema(description = "배송지명", example = "우리집", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 50)
        String name,

        @Schema(description = "수령인 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 50)
        String receiverName,

        @Schema(description = "수령인 연락처, 하이픈 포함 형식", example = "010-1234-5678",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^0\\d{1,2}-\\d{3,4}-\\d{4}$",
                message = "연락처는 하이픈을 포함한 형식으로 입력해야 합니다. 예: 010-1234-5678")
        String receiverPhone,

        @Schema(description = "우편번호(Kakao/Daum Postcode zonecode)", example = "06133", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
        String zipcode,

        @Schema(description = "기본 주소(Kakao/Daum Postcode에서 선택한 주소, 도로명 주소 권장)", example = "서울 강남구 테헤란로 123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 255)
        String address,

        @Schema(description = "상세 주소", example = "101동 1203호")
        @Size(max = 255)
        String detailAddress,

        @Schema(description = "기본 배송지 설정 여부", example = "true")
        boolean isDefault
) {
}
