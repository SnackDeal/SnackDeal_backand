package io.snackdeal.backand.domain.delivery.service;

import io.snackdeal.backand.api.user.delivery.dto.DeliveryRequest;
import io.snackdeal.backand.domain.delivery.entity.Delivery;
import io.snackdeal.backand.domain.delivery.repository.DeliveryRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeliveryService 클래스의")
class DeliveryServiceTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Nested
    @DisplayName("Describe: save() 메서드는")
    class Describe_with_save {

        @Nested
        @DisplayName("Context: 사용자의 배송지가 하나도 없는 경우")
        class Context_without_delivery {

            @Test
            @DisplayName("It: isDefault 요청값이 false여도 기본 배송지로 저장")
            void It_첫_배송지는_기본_배송지로_저장된다() {
                // given
                DeliveryRequest request = createRequest(false);
                given(deliveryRepository.existsByMemberIdAndDeletedAtIsNull(MEMBER_ID)).willReturn(false);
                given(deliveryRepository.findActiveDefaultsByMemberId(MEMBER_ID)).willReturn(List.of());
                given(deliveryRepository.save(any(Delivery.class)))
                        .willAnswer(invocation -> invocation.getArgument(0));

                // when
                var response = deliveryService.save(MEMBER_ID, request);

                // then
                then(deliveryRepository).should().existsByMemberIdAndDeletedAtIsNull(MEMBER_ID);
                then(deliveryRepository).should().save(any(Delivery.class));

                assertThat(response.isDefault()).isTrue();
            }
        }

        @Nested
        @DisplayName("Context: 기존 배송지가 있고 새 배송지를 일반 배송지로 등록하는 경우")
        class Context_with_existing_delivery_and_normal_request {

            @Test
            @DisplayName("It: 기존 기본 배송지는 유지하고 새 배송지는 기본 배송지가 아닌 상태로 저장")
            void It_새_배송지는_일반_배송지로_저장된다() {
                // given
                DeliveryRequest request = createRequest(false);
                given(deliveryRepository.existsByMemberIdAndDeletedAtIsNull(MEMBER_ID)).willReturn(true);
                given(deliveryRepository.save(any(Delivery.class)))
                        .willAnswer(invocation -> invocation.getArgument(0));

                // when
                var response = deliveryService.save(MEMBER_ID, request);

                // then
                then(deliveryRepository).should().save(any(Delivery.class));
                then(deliveryRepository).should(never()).findActiveDefaultsByMemberId(MEMBER_ID);

                assertThat(response.isDefault()).isFalse();
            }
        }

        @Nested
        @DisplayName("Context: 기존 기본 배송지가 있고 새 배송지를 기본으로 등록하는 경우")
        class Context_with_existing_default_and_default_request {

            @Test
            @DisplayName("It: 기존 기본 배송지를 해제하고 새 배송지를 기본으로 저장")
            void It_기존_기본_배송지를_해제하고_새_배송지를_기본으로_저장() {
                // given
                Delivery existingDefault = createDelivery(1L, MEMBER_ID, true);
                DeliveryRequest request = createRequest(true);

                given(deliveryRepository.existsByMemberIdAndDeletedAtIsNull(MEMBER_ID)).willReturn(true);
                given(deliveryRepository.findActiveDefaultsByMemberId(MEMBER_ID)).willReturn(List.of(existingDefault));
                given(deliveryRepository.save(any(Delivery.class)))
                        .willAnswer(invocation -> invocation.getArgument(0));

                // when
                var response = deliveryService.save(MEMBER_ID, request);

                // then
                then(deliveryRepository).should().save(any(Delivery.class));

                assertThat(existingDefault.isDefault()).isFalse();
                assertThat(response.isDefault()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("Describe: update() 메서드는")
    class Describe_with_update {

        @Nested
        @DisplayName("Context: 기본 배송지를 수정하면서 isDefault=false가 들어온 경우")
        class Context_update_default_delivery_with_false_default_request {

            @Test
            @DisplayName("It: 기존 기본 배송지 상태를 해제하지 않음")
            void It_주소만_수정하고_기본_배송지_상태는_유지() {
                // given
                Delivery target = createDelivery(20L, MEMBER_ID, true);
                DeliveryRequest request = createRequest(
                        "회사",
                        "김스낵",
                        "010-9876-5432",
                        "06234",
                        "서울 강남구 역삼로 1",
                        "2층",
                        false
                );
                given(deliveryRepository.findByIdAndDeletedAtIsNull(20L)).willReturn(Optional.of(target));

                // when
                var response = deliveryService.update(MEMBER_ID, 20L, request);

                // then
                then(deliveryRepository).should().findByIdAndDeletedAtIsNull(20L);
                then(deliveryRepository).should(never()).findActiveDefaultsByMemberId(MEMBER_ID);

                assertThat(target.getName()).isEqualTo("회사");
                assertThat(target.getReceiverName()).isEqualTo("김스낵");
                assertThat(target.getReceiverPhone()).isEqualTo("010-9876-5432");
                assertThat(target.getZipcode()).isEqualTo("06234");
                assertThat(target.getAddress()).isEqualTo("서울 강남구 역삼로 1");
                assertThat(target.getDetailAddress()).isEqualTo("2층");
                assertThat(target.isDefault()).isTrue();
                assertThat(response.isDefault()).isTrue();
            }
        }

        @Nested
        @DisplayName("Context: 다른 회원의 배송지를 수정하려는 경우")
        class Context_update_other_member_delivery {

            @Test
            @DisplayName("It: FORBIDDEN_ACCESS 예외를 발생시킨다")
            void It_FORBIDDEN_ACCESS_예외를_발생시킨다() {
                // given
                Delivery otherMemberDelivery = createDelivery(21L, OTHER_MEMBER_ID, false);
                DeliveryRequest request = createRequest(false);
                given(deliveryRepository.findByIdAndDeletedAtIsNull(21L)).willReturn(Optional.of(otherMemberDelivery));

                // when & then
                assertThatThrownBy(() -> deliveryService.update(MEMBER_ID, 21L, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.FORBIDDEN_ACCESS.getMessage());
            }
        }

        @Nested
        @DisplayName("Context: 조회 대상 배송지를 찾을 수 없는 경우")
        class Context_update_missing_delivery {

            @Test
            @DisplayName("It: DELIVERY_NOT_FOUND 예외를 발생시킨다")
            void It_DELIVERY_NOT_FOUND_예외를_발생시킨다() {
                // given
                DeliveryRequest request = createRequest(false);
                given(deliveryRepository.findByIdAndDeletedAtIsNull(404L)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> deliveryService.update(MEMBER_ID, 404L, request))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.DELIVERY_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Describe: markDefault() 메서드는")
    class Describe_with_markDefault {

        @Nested
        @DisplayName("Context: 일반 배송지를 기본 배송지로 지정하는 경우")
        class Context_mark_normal_delivery_as_default {

            @Test
            @DisplayName("It: 기존 기본 배송지를 해제하고 대상 배송지를 기본으로 지정")
            void It_대상_배송지만_기본_배송지로_지정() {
                // given
                Delivery previousDefault = createDelivery(30L, MEMBER_ID, true);
                Delivery target = createDelivery(31L, MEMBER_ID, false);
                given(deliveryRepository.findByIdAndDeletedAtIsNull(31L)).willReturn(Optional.of(target));
                given(deliveryRepository.findActiveDefaultsByMemberId(MEMBER_ID)).willReturn(List.of(previousDefault));

                // when
                deliveryService.markDefault(MEMBER_ID, 31L);

                // then
                assertThat(previousDefault.isDefault()).isFalse();
                assertThat(target.isDefault()).isTrue();
            }
        }

        @Nested
        @DisplayName("Context: 이미 여러 개의 기본 배송지가 존재하는 상태에서 하나를 기본 배송지로 지정하는 경우")
        class Context_normalize_duplicate_default_deliveries {

            @Test
            @DisplayName("It: 대상 배송지만 기본 배송지로 남기고 나머지는 해제")
            void It_중복_기본_배송지를_정리() {
                // given
                Delivery target = createDelivery(40L, MEMBER_ID, true);
                Delivery anotherDefault = createDelivery(41L, MEMBER_ID, true);
                Delivery thirdDefault = createDelivery(42L, MEMBER_ID, true);

                given(deliveryRepository.findByIdAndDeletedAtIsNull(40L)).willReturn(Optional.of(target));
                given(deliveryRepository.findActiveDefaultsByMemberId(MEMBER_ID))
                        .willReturn(List.of(target, anotherDefault, thirdDefault));

                // when
                deliveryService.markDefault(MEMBER_ID, 40L);

                // then
                assertThat(target.isDefault()).isTrue();
                assertThat(anotherDefault.isDefault()).isFalse();
                assertThat(thirdDefault.isDefault()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("Describe: delete() 메서드는")
    class Describe_with_delete {

        @Nested
        @DisplayName("Context: 기본 배송지가 아닌 배송지를 삭제하는 경우")
        class Context_delete_normal_delivery {

            @Test
            @DisplayName("It: deletedAt을 기록하여 소프트 삭제")
            void It_일반_배송지를_소프트_삭제() {
                // given
                Delivery target = createDelivery(50L, MEMBER_ID, false);
                given(deliveryRepository.findByIdAndDeletedAtIsNull(50L)).willReturn(Optional.of(target));

                // when
                deliveryService.delete(MEMBER_ID, 50L);

                // then
                assertThat(target.getDeletedAt()).isNotNull();
                assertThat(target.isDefault()).isFalse();
            }
        }

        @Nested
        @DisplayName("Context: 기본 배송지를 삭제하려는 경우")
        class Context_delete_default_delivery {

            @Test
            @DisplayName("It: DELIVERY_DEFAULT_CANNOT_BE_DELETED 예외를 발생시키고 삭제하지 않음")
            void It_DELIVERY_DEFAULT_CANNOT_BE_DELETED_예외를_발생시킨다() {
                // given
                Delivery target = createDelivery(51L, MEMBER_ID, true);
                given(deliveryRepository.findByIdAndDeletedAtIsNull(51L)).willReturn(Optional.of(target));

                // when & then
                assertThatThrownBy(() -> deliveryService.delete(MEMBER_ID, 51L))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ResponseCode.DELIVERY_DEFAULT_CANNOT_BE_DELETED.getMessage());

                assertThat(target.getDeletedAt()).isNull();
                assertThat(target.isDefault()).isTrue();
            }
        }
    }

    private DeliveryRequest createRequest(boolean isDefault) {
        return createRequest(
                "우리집",
                "홍길동",
                "010-1234-5678",
                "06133",
                "서울 강남구 테헤란로 123",
                "101동 1203호",
                isDefault
        );
    }

    private DeliveryRequest createRequest(String name, String receiverName, String receiverPhone,
                                          String zipcode, String address, String detailAddress,
                                          boolean isDefault) {
        return new DeliveryRequest(
                name,
                receiverName,
                receiverPhone,
                zipcode,
                address,
                detailAddress,
                isDefault
        );
    }

    private Delivery createDelivery(Long id, Long memberId, boolean isDefault) {
        Delivery delivery = Delivery.builder()
                .name("우리집")
                .receiverName("홍길동")
                .receiverPhone("010-1234-5678")
                .zipcode("06133")
                .address("서울 강남구 테헤란로 123")
                .detailAddress("101동 1203호")
                .isDefault(isDefault)
                .memberId(memberId)
                .build();
        setId(delivery, id);
        return delivery;
    }

    private void setId(Delivery delivery, Long id) {
        ReflectionTestUtils.setField(delivery, "id", id);
    }
}
