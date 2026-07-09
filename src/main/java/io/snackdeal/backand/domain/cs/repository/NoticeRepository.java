package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByDeletedAtIsNullOrderByIsPinnedDescCreatedAtDescIdDesc();

    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);
}
