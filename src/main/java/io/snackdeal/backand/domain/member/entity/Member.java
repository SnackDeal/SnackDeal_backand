package io.snackdeal.backand.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDateTime lastLogin;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String phone;

    @Builder
    public Member(String email, String password, String name, LocalDate birth, Gender gender, String phone, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = MemberStatus.ACTIVE;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
        this.role = (role != null) ? role : MemberRole.USER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String phone, String password) {
        if (phone != null) {
            this.phone = phone;
        }
        if (password != null) {
            this.password = password;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == MemberStatus.DELETED) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
