package io.snackdeal.backand.domain.member.mapper;

import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.domain.member.entity.Member;

public class MemberMapper {

    public static MemberDescription toDescription(Member member) {
        return new MemberDescription(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getBirth(),
                member.getGender(),
                member.getStatus(),
                member.getRole(),
                member.getCreatedAt(),
                member.getLastLogin()
        );
    }

    public static MemberDetails toDetails(Member member) {
        return new MemberDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getRole()
        );
    }
}
