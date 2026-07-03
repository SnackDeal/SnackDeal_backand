package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {
}
