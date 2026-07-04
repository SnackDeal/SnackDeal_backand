package io.snackdeal.backand.domain.member.repository;

import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
            select m from Member m
            where (:keyword is null
                   or lower(m.email) like lower(concat('%', :keyword, '%'))
                   or lower(m.name) like lower(concat('%', :keyword, '%')))
              and (:status is null or m.status = :status)
            """)
    Page<Member> search(@Param("keyword") String keyword,
                        @Param("status") MemberStatus status,
                        Pageable pageable);
}
