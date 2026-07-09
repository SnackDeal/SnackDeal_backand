package io.snackdeal.backand.domain.cs.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QnaType type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String attachmentUrl;

    private boolean isAnswered;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Long memberId;

    @Builder
    public Qna(QnaType type, String title, String content, String attachmentUrl, Long memberId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.isAnswered = false;
        this.memberId = memberId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(QnaType type, String title, String content, String attachmentUrl) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAnswered() {
        this.isAnswered = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
