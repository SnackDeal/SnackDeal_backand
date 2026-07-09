package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminQnaAnswerCreateRequest;
import io.snackdeal.backand.api.user.cs.dto.QnaResponse;
import io.snackdeal.backand.domain.cs.entity.Qna;
import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.QnaAnswerRepository;
import io.snackdeal.backand.domain.cs.repository.QnaRepository;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminQnaServiceTest {

    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private QnaAnswerRepository qnaAnswerRepository;

    @InjectMocks
    private AdminQnaService adminQnaService;

    private static final Long QNA_ID = 10L;
    private static final Long MEMBER_ID = 1L;

    private Qna createQna(Long id, boolean answered, boolean deleted) {
        Qna qna = Qna.builder()
                .type(QnaType.ORDER)
                .title("Test title")
                .content("Test content")
                .attachmentUrl(null)
                .memberId(MEMBER_ID)
                .build();
        try {
            var idField = Qna.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(qna, id);
            if (answered) {
                var answeredField = Qna.class.getDeclaredField("isAnswered");
                answeredField.setAccessible(true);
                answeredField.set(qna, true);
            }
            if (deleted) {
                var deletedAtField = Qna.class.getDeclaredField("deletedAt");
                deletedAtField.setAccessible(true);
                deletedAtField.set(qna, LocalDateTime.now());
            }
            var createdAtField = Qna.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(qna, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return qna;
    }

    @Test
    @DisplayName("관리자 답변 등록 성공")
    void answer_Success() {
        // given
        Qna qna = createQna(QNA_ID, false, false);
        given(qnaRepository.findByIdAndDeletedAtIsNull(QNA_ID)).willReturn(Optional.of(qna));
        given(qnaAnswerRepository.existsByQnaId(QNA_ID)).willReturn(false);
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        // when
        QnaResponse response = adminQnaService.answer(QNA_ID, request);

        // then
        assertThat(response.answerContent()).isEqualTo("Answer content");
    }

    @Test
    @DisplayName("관리자 답변 중복 등록 차단")
    void answer_Duplicate_Blocked() {
        // given
        Qna qna = createQna(QNA_ID, true, false);
        given(qnaRepository.findByIdAndDeletedAtIsNull(QNA_ID)).willReturn(Optional.of(qna));
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        // when & then
        assertThatThrownBy(() -> adminQnaService.answer(QNA_ID, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("존재하지 않는 QNA 답변 차단")
    void answer_NotFound_Blocked() {
        // given
        given(qnaRepository.findByIdAndDeletedAtIsNull(QNA_ID)).willReturn(Optional.empty());
        AdminQnaAnswerCreateRequest request = new AdminQnaAnswerCreateRequest("Answer content");

        // when & then
        assertThatThrownBy(() -> adminQnaService.answer(QNA_ID, request))
                .isInstanceOf(BusinessException.class);
    }
}