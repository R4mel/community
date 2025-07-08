package dev.community.dto;

import dev.community.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDto {
    private Long userId;
    private Long postId;
    private boolean isLike;

    public LikeResponseDto(Like like) {
        this.userId = like.getLikeId().getUserId();
        this.postId = like.getLikeId().getPostId();
        this.isLike = like.isLike();
    }
}
