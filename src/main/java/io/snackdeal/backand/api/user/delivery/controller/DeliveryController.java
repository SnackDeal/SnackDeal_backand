package io.snackdeal.backand.api.user.delivery.controller;

import io.snackdeal.backand.domain.delivery.service.DeliveryService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryCreateResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryListResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryRequest;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "배송지", description = "사용자 배송지 주소록 조회 · 등록 · 수정 · 기본 배송지 설정 · 삭제 API")
@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "내 배송지 목록 조회",
            description = "현재 로그인한 사용자의 삭제되지 않은 배송지 목록을 조회 기본 배송지가 우선 정렬됨 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (CommonResponse<DeliveryListResponse>)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @GetMapping
    public CommonResponse<DeliveryListResponse> list(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(deliveryService.findList(details.getId()));
    }

    @Operation(summary = "배송지 등록",
            description = "현재 로그인한 사용자의 배송지를 등록 첫 배송지는 요청값과 관계없이 기본 배송지로 설정됨 "
                    + "isDefault=true이면 기존 기본 배송지를 해제하고 새 배송지를 기본 배송지로 설정 "
                    + "Kakao/Daum Postcode는 프론트엔드에서만 사용하며, 백엔드는 제출된 zipcode/address/detailAddress를 저장 "
                    + "(Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공 (CommonResponse<DeliveryCreateResponse>)"),
            @ApiResponse(responseCode = "400", description = "유효성 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<DeliveryCreateResponse> save(@AuthenticationPrincipal MemberDetails details,
                                                       @Valid @RequestBody DeliveryRequest request) {
        return CommonResponse.created(deliveryService.save(details.getId(), request));
    }

    @Operation(summary = "배송지 수정",
            description = "현재 로그인한 사용자의 배송지만 수정할 수 있다 isDefault=false는 기존 기본 배송지를 해제하지 않음 "
                    + "isDefault=true이면 해당 배송지를 기본 배송지로 설정 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공 (CommonResponse<DeliveryResponse>)"),
            @ApiResponse(responseCode = "400", description = "유효성 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "403", description = "다른 회원의 배송지 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 배송지")
    })
    @PutMapping("/{id}")
    public CommonResponse<DeliveryResponse> update(@AuthenticationPrincipal MemberDetails details,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody DeliveryRequest request) {
        return CommonResponse.success(deliveryService.update(details.getId(), id, request));
    }

    @Operation(summary = "기본 배송지 설정",
            description = "현재 로그인한 사용자의 배송지를 기본 배송지로 설정 기존 기본 배송지는 해제됨 (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기본 배송지 설정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "403", description = "다른 회원의 배송지 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 배송지")
    })
    @PatchMapping("/{id}/default")
    public CommonResponse<Void> markDefault(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        deliveryService.markDefault(details.getId(), id);
        return CommonResponse.success(null);
    }

    @Operation(summary = "배송지 삭제",
            description = "기본 배송지가 아닌 배송지만 삭제할 수 있다 삭제는 deletedAt을 기록하는 soft delete이다 "
                    + "기본 배송지는 삭제할 수 없으며, 먼저 다른 배송지를 기본 배송지로 변경해야  (Bearer 인증 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
            @ApiResponse(responseCode = "403", description = "다른 회원의 배송지 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 삭제된 배송지"),
            @ApiResponse(responseCode = "409", description = "현재 기본 배송지 삭제 시도")
    })
    @DeleteMapping("/{id}")
    public CommonResponse<Void> delete(@AuthenticationPrincipal MemberDetails details, @PathVariable Long id) {
        deliveryService.delete(details.getId(), id);
        return CommonResponse.success(null);
    }
}
