package io.snackdeal.backand.domain.delivery.repository;

import io.snackdeal.backand.domain.delivery.entity.Delivery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("DeliveryRepository 클래스의")
class DeliveryRepositoryTest {

    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Nested
    @DisplayName("Describe: findActiveByMemberId() 메서드는")
    class Describe_with_findActiveByMemberId {

        @Nested
        @DisplayName("Context: 여러 회원의 활성/삭제 배송지가 함께 저장된 경우")
        class Context_with_mixed_deliveries {

            @Test
            @DisplayName("It: 같은 회원의 삭제되지 않은 배송지만 기본 배송지 우선, id 내림차순으로 조회")
            void It_활성_배송지를_정렬하여_조회() {
                // given
                Delivery normalOld = deliveryRepository.save(createDelivery(MEMBER_ID, false));
                Delivery defaultDelivery = deliveryRepository.save(createDelivery(MEMBER_ID, true));

                Delivery deletedDelivery = deliveryRepository.save(createDelivery(MEMBER_ID, false));
                deletedDelivery.markDeleted();

                deliveryRepository.save(createDelivery(OTHER_MEMBER_ID, false));
                Delivery normalNew = deliveryRepository.save(createDelivery(MEMBER_ID, false));

                // when
                List<Delivery> result = deliveryRepository.findActiveByMemberId(MEMBER_ID);

                // then
                assertThat(result)
                        .extracting(Delivery::getId)
                        .containsExactly(defaultDelivery.getId(), normalNew.getId(), normalOld.getId());
                assertThat(result).doesNotContain(deletedDelivery);
            }
        }
    }

    @Nested
    @DisplayName("Describe: findActiveDefaultsByMemberId() 메서드는")
    class Describe_with_findActiveDefaultsByMemberId {

        @Nested
        @DisplayName("Context: 기본/일반/삭제 배송지가 함께 저장된 경우")
        class Context_with_mixed_defaults {

            @Test
            @DisplayName("It: 같은 회원의 삭제되지 않은 기본 배송지만 조회")
            void It_활성_기본_배송지만_조회() {
                // given
                Delivery activeDefault = deliveryRepository.save(createDelivery(MEMBER_ID, true));
                deliveryRepository.save(createDelivery(MEMBER_ID, false));

                Delivery deletedDefault = deliveryRepository.save(createDelivery(MEMBER_ID, true));
                deletedDefault.markDeleted();

                deliveryRepository.save(createDelivery(OTHER_MEMBER_ID, true));

                // when
                List<Delivery> result = deliveryRepository.findActiveDefaultsByMemberId(MEMBER_ID);

                // then
                assertThat(result).containsExactly(activeDefault);
            }
        }
    }

    @Nested
    @DisplayName("Describe: findByIdAndDeletedAtIsNull() 메서드는")
    class Describe_with_findByIdAndDeletedAtIsNull {

        @Nested
        @DisplayName("Context: 활성 배송지와 삭제된 배송지가 저장된 경우")
        class Context_with_active_and_deleted {

            @Test
            @DisplayName("It: 활성 배송지만 조회하고 삭제된 배송지는 조회하지 않음")
            void It_활성_배송지만_단건_조회() {
                // given
                Delivery activeDelivery = deliveryRepository.save(createDelivery(MEMBER_ID, false));
                Delivery deletedDelivery = deliveryRepository.save(createDelivery(MEMBER_ID, false));
                deletedDelivery.markDeleted();

                // when
                Optional<Delivery> activeResult =
                        deliveryRepository.findByIdAndDeletedAtIsNull(activeDelivery.getId());
                Optional<Delivery> deletedResult =
                        deliveryRepository.findByIdAndDeletedAtIsNull(deletedDelivery.getId());

                // then
                assertThat(activeResult).contains(activeDelivery);
                assertThat(deletedResult).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Describe: existsByMemberIdAndDeletedAtIsNull() 메서드는")
    class Describe_with_existsByMemberIdAndDeletedAtIsNull {

        @Nested
        @DisplayName("Context: 한 회원은 활성 배송지가 있고 다른 회원은 삭제된 배송지만 있는 경우")
        class Context_with_active_member_and_deleted_only_member {

            @Test
            @DisplayName("It: 활성 배송지가 있는 회원만 true를 반환")
            void It_활성_배송지_존재_여부를_확인() {
                // given
                deliveryRepository.save(createDelivery(MEMBER_ID, false));

                Delivery otherDeletedDelivery = deliveryRepository.save(createDelivery(OTHER_MEMBER_ID, false));
                otherDeletedDelivery.markDeleted();

                // when
                boolean memberExists = deliveryRepository.existsByMemberIdAndDeletedAtIsNull(MEMBER_ID);
                boolean otherMemberExists = deliveryRepository.existsByMemberIdAndDeletedAtIsNull(OTHER_MEMBER_ID);

                // then
                assertThat(memberExists).isTrue();
                assertThat(otherMemberExists).isFalse();
            }
        }
    }

    private Delivery createDelivery(Long memberId, boolean isDefault) {
        return Delivery.builder()
                .memberId(memberId)
                .name("우리집")
                .receiverName("홍길동")
                .receiverPhone("010-1234-5678")
                .zipcode("06133")
                .address("서울 강남구 테헤란로 123")
                .detailAddress("101동 1203호")
                .isDefault(isDefault)
                .build();
    }

}
