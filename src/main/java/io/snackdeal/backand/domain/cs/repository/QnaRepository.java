package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Long> {
}
