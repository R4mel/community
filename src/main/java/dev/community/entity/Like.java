package dev.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @EmbeddedId
    private LikeId likeId;

    @Column(name = "is_liked")
    private boolean isLike; // default: false

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
