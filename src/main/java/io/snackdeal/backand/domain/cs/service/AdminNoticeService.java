package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeCreateRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeUpdateRequest;
import io.snackdeal.backand.api.user.cs.dto.NoticeResponse;
import io.snackdeal.backand.api.user.cs.dto.NoticeSummaryResponse;
import io.snackdeal.backand.domain.cs.entity.Notice;
import io.snackdeal.backand.domain.cs.repository.NoticeRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public List<NoticeSummaryResponse> findList() {
        return noticeRepository.findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDescIdDesc()
                .stream()
                .map(NoticeSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticeResponse findById(Long id) {
        return noticeRepository.findByIdAndDeletedAtIsNull(id)
                .map(NoticeResponse::from)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOTICE_NOT_FOUND));
    }

    @Transactional
    public NoticeResponse create(AdminNoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .isPinned(request.pinned())
                .build();
        return NoticeResponse.from(noticeRepository.save(notice));
    }

    @Transactional
    public NoticeResponse update(Long id, AdminNoticeUpdateRequest request) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOTICE_NOT_FOUND));
        notice.update(request.title(), request.content(), request.pinned());
        return NoticeResponse.from(notice);
    }

    @Transactional
    public void delete(Long id) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOTICE_NOT_FOUND));
        notice.delete();
    }
}