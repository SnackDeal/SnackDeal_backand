package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminQnaAnswerCreateRequest;
import io.snackdeal.backand.api.user.cs.dto.QnaResponse;
import io.snackdeal.backand.domain.cs.entity.Qna;
import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import io.snackdeal.backand.domain.cs.repository.QnaAnswerRepository;
import io.snackdeal.backand.domain.cs.repository.QnaRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminQnaService {

    private final QnaRepository qnaRepository;
    private final QnaAnswerRepository qnaAnswerRepository;

    @Transactional(readOnly = true)
    public List<QnaResponse> findList() {
        List<Qna> qnaList = qnaRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        return qnaList.stream()
                .map(qna -> {
                    QnaAnswer answer = qnaAnswerRepository.findByQnaId(qna.getId()).orElse(null);
                    return toQnaResponse(qna, answer);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QnaResponse findById(Long id) {
        Qna qna = qnaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.QNA_NOT_FOUND));
        QnaAnswer answer = qnaAnswerRepository.findByQnaId(qna.getId()).orElse(null);
        return toQnaResponse(qna, answer);
    }

    @Transactional
    public QnaResponse answer(Long id, AdminQnaAnswerCreateRequest request) {
        Qna qna = qnaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.QNA_NOT_FOUND));

        if (qna.isAnswered() || qnaAnswerRepository.existsByQnaId(id)) {
            throw new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED);
        }

        QnaAnswer answer = QnaAnswer.builder()
                .qnaId(id)
                .content(request.content())
                .build();
        qnaAnswerRepository.save(answer);
        qna.markAnswered();

        return toQnaResponse(qna, answer);
    }

    private QnaResponse toQnaResponse(Qna qna, QnaAnswer answer) {
        return new QnaResponse(
                qna.getId(),
                qna.getType(),
                qna.getTitle(),
                qna.getContent(),
                qna.getAttachmentUrl(),
                qna.isAnswered(),
                qna.getCreatedAt(),
                answer != null ? answer.getContent() : null,
                answer != null ? answer.getAnsweredAt() : null
        );
    }
}