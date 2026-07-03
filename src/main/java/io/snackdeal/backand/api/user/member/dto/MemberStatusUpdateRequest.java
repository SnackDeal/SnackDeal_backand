package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.MemberStatus;
import jakarta.validation.constraints.NotNull;

public record MemberStatusUpdateRequest(
        @NotNull MemberStatus status
) {
}
