package io.snackdeal.backand.domain.cs.repository;

import io.snackdeal.backand.domain.cs.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
