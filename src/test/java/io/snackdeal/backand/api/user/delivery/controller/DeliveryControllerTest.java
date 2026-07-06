package io.snackdeal.backand.api.user.delivery.controller;

import io.snackdeal.backand.api.user.delivery.dto.DeliveryCreateResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryListResponse;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryRequest;
import io.snackdeal.backand.api.user.delivery.dto.DeliveryResponse;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.delivery.service.DeliveryService;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryController 클래스의")
class DeliveryControllerTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long DELIVERY_ID = 5L;

    @InjectMocks
    private DeliveryController deliveryController;

    @Mock
    private DeliveryService deliveryService;

    @Nested
    @DisplayName("Describe: list 메서드는")
    class Describe_with_list {

        @Nested
        @DisplayName("Context: 인증된 사용자가 요청한 경우")
        class Context_with_authenticated_member {

            @Test
            @DisplayName("It: 인증된 사용자 id로 배송지 목록을 조회하고 CommonResponse로 감싸 반환")
            void It_내_배송지_목록_조회_성공() {
                // given
                DeliveryListResponse expected = new DeliveryListResponse(List.of(deliveryResponse()));
                given(deliveryService.findList(MEMBER_ID)).willReturn(expected);

                // when
                CommonResponse<DeliveryListResponse> response = deliveryController.list(memberDetails());

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
                assertThat(response.getMessage()).isEqualTo(ResponseCode.SUCCESS.getMessage());
                assertThat(response.getData()).isSameAs(expected);
                then(deliveryService).should().findList(MEMBER_ID);
            }
        }
    }

    @Nested
    @DisplayName("Describe: save 메서드는")
    class Describe_with_save {

        @Nested
        @DisplayName("Context: 유효한 배송지 등록 요청인 경우")
        class Context_with_valid_request {

            @Test
            @DisplayName("It: 인증된 사용자 id와 요청으로 배송지를 등록하고 CommonResponse로 감싸 반환")
            void It_배송지_등록_성공() {
                // given
                DeliveryRequest request = validRequest();
                DeliveryCreateResponse expected = new DeliveryCreateResponse(DELIVERY_ID, true);
                given(deliveryService.save(MEMBER_ID, request)).willReturn(expected);

                // when
                CommonResponse<DeliveryCreateResponse> response = deliveryController.save(memberDetails(), request);

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getCode()).isEqualTo(ResponseCode.CREATED.getCode());
                assertThat(response.getMessage()).isEqualTo(ResponseCode.CREATED.getMessage());
                assertThat(response.getData()).isSameAs(expected);
                then(deliveryService).should().save(MEMBER_ID, request);
            }
        }
    }

    @Nested
    @DisplayName("Describe: update 메서드는")
    class Describe_with_update {

        @Nested
        @DisplayName("Context: 유효한 배송지 수정 요청인 경우")
        class Context_with_valid_request {

            @Test
            @DisplayName("It: 인증된 사용자 id, 배송지 id, 요청으로 배송지를 수정하고 CommonResponse로 감싸 반환")
            void It_배송지_수정_성공() {
                // given
                DeliveryRequest request = validRequest();
                DeliveryResponse expected = deliveryResponse();
                given(deliveryService.update(MEMBER_ID, DELIVERY_ID, request)).willReturn(expected);

                // when
                CommonResponse<DeliveryResponse> response =
                        deliveryController.update(memberDetails(), DELIVERY_ID, request);

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
                assertThat(response.getMessage()).isEqualTo(ResponseCode.SUCCESS.getMessage());
                assertThat(response.getData()).isSameAs(expected);
                then(deliveryService).should().update(MEMBER_ID, DELIVERY_ID, request);
            }
        }

        @Nested
        @DisplayName("Context: 서비스에서 FORBIDDEN_ACCESS 예외가 발생하는 경우")
        class Context_with_forbidden_exception {

            @Test
            @DisplayName("It: BusinessException을 그대로 전파")
            void It_FORBIDDEN_ACCESS_예외를_전파() {
                // given
                DeliveryRequest request = validRequest();
                given(deliveryService.update(MEMBER_ID, DELIVERY_ID, request))
                        .willThrow(new BusinessException(ResponseCode.FORBIDDEN_ACCESS));

                // when & then
                assertThatThrownBy(() -> deliveryController.update(memberDetails(), DELIVERY_ID, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.FORBIDDEN_ACCESS.getMessage());
                then(deliveryService).should().update(MEMBER_ID, DELIVERY_ID, request);
            }
        }

        @Nested
        @DisplayName("Context: 서비스에서 DELIVERY_NOT_FOUND 예외가 발생하는 경우")
        class Context_with_not_found_exception {

            @Test
            @DisplayName("It: BusinessException을 그대로 전파")
            void It_DELIVERY_NOT_FOUND_예외를_전파() {
                // given
                DeliveryRequest request = validRequest();
                given(deliveryService.update(MEMBER_ID, DELIVERY_ID, request))
                        .willThrow(new BusinessException(ResponseCode.DELIVERY_NOT_FOUND));

                // when & then
                assertThatThrownBy(() -> deliveryController.update(memberDetails(), DELIVERY_ID, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.DELIVERY_NOT_FOUND.getMessage());
                then(deliveryService).should().update(MEMBER_ID, DELIVERY_ID, request);
            }
        }
    }

    @Nested
    @DisplayName("Describe: markDefault 메서드는")
    class Describe_with_mark_default {

        @Nested
        @DisplayName("Context: 인증된 사용자가 요청한 경우")
        class Context_with_authenticated_member {

            @Test
            @DisplayName("It: 인증된 사용자 id와 배송지 id로 기본 배송지를 설정하고 성공 응답을 반환")
            void It_기본_배송지_설정_성공() {
                // when
                CommonResponse<Void> response = deliveryController.markDefault(memberDetails(), DELIVERY_ID);

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
                assertThat(response.getMessage()).isEqualTo(ResponseCode.SUCCESS.getMessage());
                assertThat(response.getData()).isNull();
                then(deliveryService).should().markDefault(MEMBER_ID, DELIVERY_ID);
            }
        }
    }

    @Nested
    @DisplayName("Describe: delete 메서드는")
    class Describe_with_delete {

        @Nested
        @DisplayName("Context: 인증된 사용자가 요청한 경우")
        class Context_with_authenticated_member {

            @Test
            @DisplayName("It: 인증된 사용자 id와 배송지 id로 배송지를 삭제하고 성공 응답을 반환")
            void It_배송지_삭제_성공() {
                // when
                CommonResponse<Void> response = deliveryController.delete(memberDetails(), DELIVERY_ID);

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
                assertThat(response.getMessage()).isEqualTo(ResponseCode.SUCCESS.getMessage());
                assertThat(response.getData()).isNull();
                then(deliveryService).should().delete(MEMBER_ID, DELIVERY_ID);
            }
        }

        @Nested
        @DisplayName("Context: 서비스에서 DELIVERY_DEFAULT_CANNOT_BE_DELETED 예외가 발생하는 경우")
        class Context_with_default_delete_exception {

            @Test
            @DisplayName("It: BusinessException을 그대로 전파")
            void It_DELIVERY_DEFAULT_CANNOT_BE_DELETED_예외를_전파() {
                // given
                willThrow(new BusinessException(ResponseCode.DELIVERY_DEFAULT_CANNOT_BE_DELETED))
                        .given(deliveryService)
                        .delete(MEMBER_ID, DELIVERY_ID);

                // when & then
                assertThatThrownBy(() -> deliveryController.delete(memberDetails(), DELIVERY_ID))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.DELIVERY_DEFAULT_CANNOT_BE_DELETED.getMessage());
                then(deliveryService).should().delete(MEMBER_ID, DELIVERY_ID);
            }
        }
    }

    private DeliveryRequest validRequest() {
        return new DeliveryRequest(
                "우리집",
                "홍길동",
                "010-1234-5678",
                "06133",
                "서울 강남구 테헤란로 123",
                "101동 1203호",
                true
        );
    }

    private DeliveryResponse deliveryResponse() {
        return new DeliveryResponse(
                DELIVERY_ID,
                "우리집",
                "홍길동",
                "010-1234-5678",
                "06133",
                "서울 강남구 테헤란로 123",
                "101동 1203호",
                true
        );
    }

    private MemberDetails memberDetails() {
        return new MemberDetails(MEMBER_ID, "user@test.com", "ENCODED", MemberRole.USER);
    }

}
