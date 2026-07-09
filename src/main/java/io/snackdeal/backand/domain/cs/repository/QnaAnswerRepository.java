package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.QnaAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Long> {

    Optional<QnaAnswer> findByQnaId(Long qnaId);

    boolean existsByQnaId(Long qnaId);
}
