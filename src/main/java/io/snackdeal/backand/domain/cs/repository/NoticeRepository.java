package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
