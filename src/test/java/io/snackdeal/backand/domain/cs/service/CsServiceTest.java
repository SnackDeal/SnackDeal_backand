package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CsServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @InjectMocks
    private CsService csService;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findNoticeList - TODO")
    void findNoticeList_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findNoticeById - TODO")
    void findNoticeById_Success() {
        fail("not implemented");
    }

    @Test
    @DisplayName("findFaqList - 전체 FAQ 조회 성공")
    void findFaqList_All_Success() {
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
        List<FaqResponse> result = csService.findFaqList(null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(QnaType.ORDER);
        assertThat(result.get(1).type()).isEqualTo(QnaType.SHIPPING);
    }

    @Test
    @DisplayName("findFaqList - 타입별 FAQ 조회 성공")
    void findFaqList_ByType_Success() {
        // given
        Faq faq = Faq.builder()
                .type(QnaType.ORDER)
                .title("주문 내역은 어디서 확인하나요?")
                .content("로그인 후 마이페이지에서 확인할 수 있습니다.")
                .build();
        given(faqRepository.findAllByTypeAndDeletedAtIsNullOrderByIdAsc(QnaType.ORDER))
                .willReturn(List.of(faq));

        // when
        List<FaqResponse> result = csService.findFaqList(QnaType.ORDER);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo(QnaType.ORDER);
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findMyQnaList - TODO")
    void findMyQnaList_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("createQna - TODO")
    void createQna_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("findQnaById - TODO")
    void findQnaById_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("updateQna - TODO")
    void updateQna_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("deleteQna - TODO")
    void deleteQna_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("askChatbot - TODO")
    void askChatbot_Success() {
        fail("not implemented");
    }

}