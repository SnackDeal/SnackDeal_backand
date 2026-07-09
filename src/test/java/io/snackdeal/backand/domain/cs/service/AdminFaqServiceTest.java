package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminFaqRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminFaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminFaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private AdminFaqService adminFaqService;

    @Test
    @DisplayName("findList - 전체 FAQ 조회 성공")
    void findList_All_Success() {
        // given
        Faq faq1 = Faq.builder()
                .type(QnaType.ORDER)
                .title("주문 내역은 어디서 확인하나요?")
                .content("로그인 후 마이페이지에서 확인할 수 있습니다.")
                .build();
        Faq faq2 = Faq.builder()
                .type(QnaType.SHIPPING)
                .title("배송은 보통 얼마나 걸리나요?")
                .content("주문 확인 후 순차적으로 진행됩니다.")
                .build();
        given(faqRepository.findAllByDeletedAtIsNullOrderByTypeAscIdAsc())
                .willReturn(List.of(faq1, faq2));

        // when
        List<AdminFaqResponse> result = adminFaqService.findList(null);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findList - 타입별 FAQ 조회 성공")
    void findList_ByType_Success() {
        // given
        Faq faq = Faq.builder()
                .type(QnaType.PRODUCT)
                .title("상품 재고는 어디서 확인하나요?")
                .content("상품 상세 페이지에서 확인할 수 있습니다.")
                .build();
        given(faqRepository.findAllByTypeAndDeletedAtIsNullOrderByIdAsc(QnaType.PRODUCT))
                .willReturn(List.of(faq));

        // when
        List<AdminFaqResponse> result = adminFaqService.findList(QnaType.PRODUCT);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo(QnaType.PRODUCT);
    }

    @Test
    @DisplayName("findById - FAQ 단건 조회 성공")
    void findById_Success() {
        // given
        Faq faq = Faq.builder()
                .type(QnaType.OTHER)
                .title("회원가입은 어떻게 하나요?")
                .content("회원가입 페이지에서 가입할 수 있습니다.")
                .build();
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(faq));

        // when
        AdminFaqResponse result = adminFaqService.findById(1L);

        // then
        assertThat(result.title()).isEqualTo("회원가입은 어떻게 하나요?");
    }

    @Test
    @DisplayName("findById - FAQ가 없으면 FAQ_NOT_FOUND 예외 발생")
    void findById_NotFound() {
        // given
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> adminFaqService.findById(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResponseCode.FAQ_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("save - FAQ 생성 성공")
    void save_Success() {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.ORDER, "새 FAQ 제목", "새 FAQ 내용");
        Faq faq = Faq.builder()
                .type(QnaType.ORDER)
                .title("새 FAQ 제목")
                .content("새 FAQ 내용")
                .build();
        given(faqRepository.existsByTypeAndTitleAndDeletedAtIsNull(QnaType.ORDER, "새 FAQ 제목"))
                .willReturn(false);
        given(faqRepository.save(any(Faq.class))).willReturn(faq);

        // when
        AdminFaqResponse result = adminFaqService.save(request);

        // then
        assertThat(result.type()).isEqualTo(QnaType.ORDER);
        assertThat(result.title()).isEqualTo("새 FAQ 제목");
        verify(faqRepository).save(any(Faq.class));
    }

    @Test
    @DisplayName("save - 중복 FAQ 생성 시 DUPLICATE_FAQ 예외 발생")
    void save_Duplicate() {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.ORDER, "중복 제목", "내용");
        given(faqRepository.existsByTypeAndTitleAndDeletedAtIsNull(QnaType.ORDER, "중복 제목"))
                .willReturn(true);

        // when / then
        assertThatThrownBy(() -> adminFaqService.save(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResponseCode.DUPLICATE_FAQ.getMessage());
    }

    @Test
    @DisplayName("update - FAQ 수정 성공")
    void update_Success() {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.SHIPPING, "수정된 제목", "수정된 내용");
        Faq faq = Faq.builder()
                .type(QnaType.ORDER)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(faq));
        given(faqRepository.existsByTypeAndTitleAndIdNotAndDeletedAtIsNull(QnaType.SHIPPING, "수정된 제목", 1L))
                .willReturn(false);

        // when
        AdminFaqResponse result = adminFaqService.update(1L, request);

        // then
        assertThat(result.title()).isEqualTo("수정된 제목");
        assertThat(result.type()).isEqualTo(QnaType.SHIPPING);
    }

    @Test
    @DisplayName("update - FAQ가 없으면 FAQ_NOT_FOUND 예외 발생")
    void update_NotFound() {
        // given
        AdminFaqRequest request = new AdminFaqRequest(QnaType.ORDER, "제목", "내용");
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> adminFaqService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResponseCode.FAQ_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("delete - FAQ 소프트 삭제 성공")
    void delete_Success() {
        // given
        Faq faq = Faq.builder()
                .type(QnaType.OTHER)
                .title("삭제될 FAQ")
                .content("내용")
                .build();
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(faq));

        // when
        adminFaqService.delete(1L);

        // then
        assertThat(faq.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("delete - FAQ가 없으면 FAQ_NOT_FOUND 예외 발생")
    void delete_NotFound() {
        // given
        given(faqRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> adminFaqService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResponseCode.FAQ_NOT_FOUND.getMessage());
    }
}