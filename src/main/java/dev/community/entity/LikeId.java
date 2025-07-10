package dev.community.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Embeddable
@Getter
public class LikeId implements Serializable {
    private Long userId;
    private Long postId;

    public LikeId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;

        return Objects.equals(userId, likeId.userId) && Objects.equals(postId, likeId.postId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}
