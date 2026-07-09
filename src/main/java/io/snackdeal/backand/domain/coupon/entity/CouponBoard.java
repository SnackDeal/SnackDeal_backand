package io.snackdeal.backand.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String thumbnailUrl;

    private boolean isActive;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Builder
    public CouponBoard(String title, String content, String thumbnailUrl, Boolean isActive,
                       LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.isActive = isActive == null || isActive;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content, String thumbnailUrl, Boolean isActive,
                       LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.isActive = isActive == null ? this.isActive : isActive;
        this.startAt = startAt;
        this.endAt = endAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = this.deletedAt;
    }
}
