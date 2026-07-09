package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Faq;
import io.snackdeal.backand.domain.cs.entity.QnaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findAllByDeletedAtIsNullOrderByTypeAscIdAsc();

    List<Faq> findAllByTypeAndDeletedAtIsNullOrderByIdAsc(QnaType type);

    Optional<Faq> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByTypeAndTitleAndDeletedAtIsNull(QnaType type, String title);

    boolean existsByTypeAndTitleAndIdNotAndDeletedAtIsNull(QnaType type, String title, Long id);
}
