package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminFaqRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminFaqResponse;
import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import io.snackdeal.backand.domain.cs.repository.FaqRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminFaqService {

    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<AdminFaqResponse> findList(QnaType type) {
        List<Faq> faqs;
        if (type == null) {
            faqs = faqRepository.findAllByDeletedAtIsNullOrderByTypeAscIdAsc();
        } else {
            faqs = faqRepository.findAllByTypeAndDeletedAtIsNullOrderByIdAsc(type);
        }
        return faqs.stream().map(AdminFaqResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AdminFaqResponse findById(Long id) {
        Faq faq = faqRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.FAQ_NOT_FOUND));
        return AdminFaqResponse.from(faq);
    }

    public AdminFaqResponse save(AdminFaqRequest request) {
        if (faqRepository.existsByTypeAndTitleAndDeletedAtIsNull(request.type(), request.title())) {
            throw new BusinessException(ResponseCode.DUPLICATE_FAQ);
        }
        Faq faq = Faq.builder()
                .type(request.type())
                .title(request.title())
                .content(request.content())
                .build();
        Faq saved = faqRepository.save(faq);
        return AdminFaqResponse.from(saved);
    }

    public AdminFaqResponse update(Long id, AdminFaqRequest request) {
        Faq faq = faqRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.FAQ_NOT_FOUND));
        if (faqRepository.existsByTypeAndTitleAndIdNotAndDeletedAtIsNull(request.type(), request.title(), id)) {
            throw new BusinessException(ResponseCode.DUPLICATE_FAQ);
        }
        faq.update(request.type(), request.title(), request.content());
        return AdminFaqResponse.from(faq);
    }

    public void delete(Long id) {
        Faq faq = faqRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.FAQ_NOT_FOUND));
        faq.delete();
    }
}