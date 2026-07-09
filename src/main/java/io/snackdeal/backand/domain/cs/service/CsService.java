package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.user.cs.dto.FaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsService {

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

    public Object findMyQnaList(String email) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object createQna(String email, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object findQnaById(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object updateQna(String email, Long id, Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public void deleteQna(String email, Long id) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }

    public Object askChatbot(Object request) {
        throw new BusinessException(ResponseCode.NOT_IMPLEMENTED);
    }
}
