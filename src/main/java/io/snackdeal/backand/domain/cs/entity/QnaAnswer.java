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
public class QnaAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime answeredAt;

    private Long qnaId;

    @Builder
    public QnaAnswer(String content, Long qnaId) {
        this.content = content;
        this.qnaId = qnaId;
        this.answeredAt = LocalDateTime.now();
    }
}
