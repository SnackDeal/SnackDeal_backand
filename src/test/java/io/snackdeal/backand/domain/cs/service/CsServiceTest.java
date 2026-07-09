package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.user.cs.dto.*;
import io.snackdeal.backand.domain.cs.entity.Notice;
import io.snackdeal.backand.domain.cs.entity.Qna;
import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.NoticeRepository;
import io.snackdeal.backand.domain.cs.repository.QnaAnswerRepository;
import io.snackdeal.backand.domain.cs.repository.QnaRepository;
import io.snackdeal.backand.domain.cs.service.CsService;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CsServiceTest {

    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private QnaAnswerRepository qnaAnswerRepository;

    @InjectMocks
    private CsService csService;

    private static final Long MEMBER_ID = 1L;
    private static final Long OTHER_MEMBER_ID = 2L;
    private static final Long QNA_ID = 10L;

    @Mock
    private FaqRepository faqRepository;

    @Mock
    private NoticeRepository noticeRepository;

    private Qna createQna(Long id, Long memberId, boolean answered) {
        Qna qna = Qna.builder()
                .type(QnaType.ORDER)
                .title("Test title")
                .content("Test content")
                .attachmentUrl(null)
                .memberId(memberId)
                .build();
        ReflectionTestUtils.setField(qna, "id", id);
        ReflectionTestUtils.setField(qna, "isAnswered", answered);
        ReflectionTestUtils.setField(qna, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(qna, "updatedAt", LocalDateTime.now());
        return qna;
    }

    @Test
    @DisplayName("QNA 생성 성공")
    void createQna_Success() {
        // given
        QnaCreateRequest request = new QnaCreateRequest(QnaType.ORDER, "Test title", "Test content", null);
        given(qnaRepository.save(any(Qna.class))).willAnswer(invocation -> {
            Qna savedQna = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedQna, "id", QNA_ID);
            return savedQna;
        });

        // when
        QnaResponse response = csService.createQna(MEMBER_ID, request);

        // then
        assertThat(response.id()).isEqualTo(QNA_ID);
    }

    @Test
    @DisplayName("내 QNA 목록 조회 성공")
    void findMyQnaList_Success() {
        // given
        Qna qna1 = createQna(1L, MEMBER_ID, false);
        Qna qna2 = createQna(2L, MEMBER_ID, true);
        given(qnaRepository.findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(MEMBER_ID))
                .willReturn(List.of(qna1, qna2));

        // when
        List<QnaSummaryResponse> result = csService.findMyQnaList(MEMBER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).answered()).isFalse();
        assertThat(result.get(1).answered()).isTrue();
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

    @Test
    @DisplayName("findNoticeList - 고정글이 위로 오도록 정렬되어 조회된다")
    void findNoticeList_Success() {
        // given
        Notice pinned = Notice.builder().title("점검 안내").content("정기 점검 안내").isPinned(true).build();
        ReflectionTestUtils.setField(pinned, "id", 1L);
        Notice normal = Notice.builder().title("이벤트 안내").content("여름 이벤트 안내").isPinned(false).build();
        ReflectionTestUtils.setField(normal, "id", 2L);
        given(noticeRepository.findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDescIdDesc())
                .willReturn(List.of(pinned, normal));

        // when
        List<NoticeSummaryResponse> result = csService.findNoticeList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).pinned()).isTrue();
    }

    @Test
    @DisplayName("findNoticeById - 공지사항 상세 조회 성공")
    void findNoticeById_Success() {
        // given
        Notice notice = Notice.builder().title("점검 안내").content("정기 점검 안내").isPinned(true).build();
        ReflectionTestUtils.setField(notice, "id", 1L);
        given(noticeRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(notice));

        // when
        NoticeResponse response = csService.findNoticeById(1L);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("점검 안내");
    }

    @Test
    @DisplayName("findNoticeById - 존재하지 않으면 예외")
    void findNoticeById_NotFound() {
        // given
        given(noticeRepository.findByIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> csService.findNoticeById(999L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("내 QNA 상세 조회 성공")
    void findQnaById_Success() {
        // given
        Qna qna = createQna(QNA_ID, MEMBER_ID, false);
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, MEMBER_ID))
                .willReturn(Optional.of(qna));
        given(qnaAnswerRepository.findByQnaId(QNA_ID)).willReturn(Optional.empty());

        // when
        QnaResponse response = csService.findQnaById(MEMBER_ID, QNA_ID);

        // then
        assertThat(response.id()).isEqualTo(QNA_ID);
    }

    @Test
    @DisplayName("다른 사용자 QNA 상세 조회 차단")
    void findQnaById_OtherMember_Blocked() {
        // given
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, OTHER_MEMBER_ID))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> csService.findQnaById(OTHER_MEMBER_ID, QNA_ID))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("QNA 수정 성공")
    void updateQna_Success() {
        // given
        Qna qna = createQna(QNA_ID, MEMBER_ID, false);
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, MEMBER_ID))
                .willReturn(Optional.of(qna));
        given(qnaAnswerRepository.findByQnaId(QNA_ID)).willReturn(Optional.empty());
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated title", "Updated content", null);

        // when
        QnaResponse response = csService.updateQna(MEMBER_ID, QNA_ID, request);

        // then
        assertThat(response.title()).isEqualTo("Updated title");
    }

    @Test
    @DisplayName("다른 사용자 QNA 수정 차단")
    void updateQna_OtherMember_Blocked() {
        // given
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, OTHER_MEMBER_ID))
                .willReturn(Optional.empty());
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated", "Updated", null);

        // when & then
        assertThatThrownBy(() -> csService.updateQna(OTHER_MEMBER_ID, QNA_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("답변 완료된 QNA 수정 차단")
    void updateQna_Answered_Blocked() {
        // given
        Qna qna = createQna(QNA_ID, MEMBER_ID, true);
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, MEMBER_ID))
                .willReturn(Optional.of(qna));
        QnaUpdateRequest request = new QnaUpdateRequest(QnaType.SHIPPING, "Updated", "Updated", null);

        // when & then
        assertThatThrownBy(() -> csService.updateQna(MEMBER_ID, QNA_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("QNA 삭제 성공")
    void deleteQna_Success() {
        // given
        Qna qna = createQna(QNA_ID, MEMBER_ID, false);
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, MEMBER_ID))
                .willReturn(Optional.of(qna));

        // when
        csService.deleteQna(MEMBER_ID, QNA_ID);

        // then
        assertThat(qna.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("다른 사용자 QNA 삭제 차단")
    void deleteQna_OtherMember_Blocked() {
        // given
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, OTHER_MEMBER_ID))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> csService.deleteQna(OTHER_MEMBER_ID, QNA_ID))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("답변 완료된 QNA 삭제 차단")
    void deleteQna_Answered_Blocked() {
        // given
        Qna qna = createQna(QNA_ID, MEMBER_ID, true);
        given(qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(QNA_ID, MEMBER_ID))
                .willReturn(Optional.of(qna));

        // when & then
        assertThatThrownBy(() -> csService.deleteQna(MEMBER_ID, QNA_ID))
                .isInstanceOf(BusinessException.class);
    }
}
