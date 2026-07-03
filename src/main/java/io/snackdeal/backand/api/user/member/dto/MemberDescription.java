package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberDescription(
        Long id,
        String email,
        String name,
        String phone,
        LocalDate birth,
        Gender gender,
        MemberStatus status,
        MemberRole role,
        LocalDateTime createdAt
) {
}
