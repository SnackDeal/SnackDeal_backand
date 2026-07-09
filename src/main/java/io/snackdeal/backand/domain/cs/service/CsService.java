package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.user.cs.dto.*;
import io.snackdeal.backand.domain.cs.entity.Qna;
import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import io.snackdeal.backand.domain.cs.repository.QnaAnswerRepository;
import io.snackdeal.backand.domain.cs.repository.QnaRepository;
import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsService {

    private final QnaRepository qnaRepository;
    private final QnaAnswerRepository qnaAnswerRepository;

    private final FaqRepository faqRepository;

    public Object findNoticeList() {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findNoticeById(Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public List<FaqResponse> findFaqList(QnaType type) {
        List<Faq> faqs;
        if (type == null) {
            faqs = faqRepository.findAllByDeletedAtIsNullOrderByTypeAscIdAsc();
        } else {
            faqs = faqRepository.findAllByTypeAndDeletedAtIsNullOrderByIdAsc(type);
        }
        return faqs.stream().map(FaqResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<QnaSummaryResponse> findMyQnaList(Long memberId) {
        List<Qna> qnaList = qnaRepository.findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId);
        return qnaList.stream()
                .map(qna -> new QnaSummaryResponse(
                        qna.getId(),
                        qna.getType(),
                        qna.getTitle(),
                        qna.isAnswered(),
                        qna.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public QnaResponse createQna(Long memberId, QnaCreateRequest request) {
        Qna qna = Qna.builder()
                .type(request.type())
                .title(request.title())
                .content(request.content())
                .attachmentUrl(request.attachmentUrl())
                .memberId(memberId)
                .build();
        qnaRepository.save(qna);
        return toQnaResponse(qna, null);
    }

    @Transactional(readOnly = true)
    public QnaResponse findQnaById(Long memberId, Long id) {
        Qna qna = qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(id, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.QNA_NOT_FOUND));
        QnaAnswer answer = qnaAnswerRepository.findByQnaId(qna.getId()).orElse(null);
        return toQnaResponse(qna, answer);
    }

    @Transactional
    public QnaResponse updateQna(Long memberId, Long id, QnaUpdateRequest request) {
        Qna qna = qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(id, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.QNA_NOT_FOUND));

        if (qna.isAnswered()) {
            throw new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED);
        }

        qna.update(request.type(), request.title(), request.content(), request.attachmentUrl());
        QnaAnswer answer = qnaAnswerRepository.findByQnaId(qna.getId()).orElse(null);
        return toQnaResponse(qna, answer);
    }

    @Transactional
    public void deleteQna(Long memberId, Long id) {
        Qna qna = qnaRepository.findByIdAndMemberIdAndDeletedAtIsNull(id, memberId)
                .orElseThrow(() -> new BusinessException(ResponseCode.QNA_NOT_FOUND));

        if (qna.isAnswered()) {
            throw new BusinessException(ResponseCode.QNA_ALREADY_ANSWERED);
        }

        qna.delete();
    }

    public Object askChatbot(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
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
