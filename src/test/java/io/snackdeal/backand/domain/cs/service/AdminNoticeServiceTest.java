package io.snackdeal.backand.domain.cs.service;

import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeCreateRequest;
import io.snackdeal.backand.api.admin.cs.dto.AdminNoticeUpdateRequest;
import io.snackdeal.backand.api.user.cs.dto.NoticeResponse;
import io.snackdeal.backand.api.user.cs.dto.NoticeSummaryResponse;
import io.snackdeal.backand.domain.cs.entity.Notice;
import io.snackdeal.backand.domain.cs.repository.NoticeRepository;
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

@ExtendWith(MockitoExtension.class)
class AdminNoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private AdminNoticeService adminNoticeService;

    @Test
    @DisplayName("findList - 삭제되지 않은 공지사항 목록을 반환한다")
    void findList_Success() {
        // given
        Notice notice = Notice.builder().title("공지").content("내용").isPinned(false).build();
        given(noticeRepository.findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDescIdDesc())
                .willReturn(List.of(notice));

        // when
        List<NoticeSummaryResponse> result = adminNoticeService.findList();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("공지");
    }

    @Test
    @DisplayName("findById - 삭제되지 않은 공지사항 상세를 반환한다")
    void findById_Success() {
        // given
        Notice notice = Notice.builder().title("공지").content("내용").isPinned(false).build();
        given(noticeRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(notice));

        // when
        NoticeResponse result = adminNoticeService.findById(1L);

        // then
        assertThat(result.title()).isEqualTo("공지");
    }

    @Test
    @DisplayName("findById - 존재하지 않으면 NOTICE_NOT_FOUND 예외가 발생한다")
    void findById_NotFound() {
        // given
        given(noticeRepository.findByIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.findById(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("responseCode")
                .isEqualTo(ResponseCode.NOTICE_NOT_FOUND);
    }

    @Test
    @DisplayName("create - 공지사항을 생성하고 응답을 반환한다")
    void create_Success() {
        // given
        AdminNoticeCreateRequest request = new AdminNoticeCreateRequest("제목", "내용", true);
        Notice saved = Notice.builder().title("제목").content("내용").isPinned(true).build();
        given(noticeRepository.save(any(Notice.class))).willReturn(saved);

        // when
        NoticeResponse result = adminNoticeService.create(request);

        // then
        assertThat(result.title()).isEqualTo("제목");
        assertThat(result.content()).isEqualTo("내용");
        assertThat(result.pinned()).isTrue();
    }

    @Test
    @DisplayName("update - 공지사항을 수정하고 응답을 반환한다")
    void update_Success() {
        // given
        Notice existing = Notice.builder().title("옛제목").content("옛내용").isPinned(false).build();
        AdminNoticeUpdateRequest request = new AdminNoticeUpdateRequest("새제목", "새내용", true);
        given(noticeRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(existing));

        // when
        NoticeResponse result = adminNoticeService.update(1L, request);

        // then
        assertThat(result.title()).isEqualTo("새제목");
        assertThat(result.content()).isEqualTo("새내용");
        assertThat(result.pinned()).isTrue();
    }

    @Test
    @DisplayName("update - 존재하지 않으면 NOTICE_NOT_FOUND 예외가 발생한다")
    void update_NotFound() {
        // given
        given(noticeRepository.findByIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.update(999L, new AdminNoticeUpdateRequest("제목", "내용", false)))
                .isInstanceOf(BusinessException.class)
                .extracting("responseCode")
                .isEqualTo(ResponseCode.NOTICE_NOT_FOUND);
    }

    @Test
    @DisplayName("delete - 공지사항을 소프트 삭제한다")
    void delete_Success() {
        // given
        Notice existing = Notice.builder().title("공지").content("내용").isPinned(false).build();
        given(noticeRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(existing));

        // when
        adminNoticeService.delete(1L);

        // then
        assertThat(existing.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("delete - 존재하지 않으면 NOTICE_NOT_FOUND 예외가 발생한다")
    void delete_NotFound() {
        // given
        given(noticeRepository.findByIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminNoticeService.delete(999L))
                .isInstanceOf(BusinessException.class)
                .extracting("responseCode")
                .isEqualTo(ResponseCode.NOTICE_NOT_FOUND);
    }
}