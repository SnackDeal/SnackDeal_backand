package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    List<Qna> findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId);

    Optional<Qna> findByIdAndDeletedAtIsNull(Long id);

    Optional<Qna> findByIdAndMemberIdAndDeletedAtIsNull(Long id, Long memberId);

    List<Qna> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
}
