package dev.community.dto;

import dev.community.entity.PostImage;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageResponseDto {
    private Long postImageId;
    private String imageUrl;

    public PostImageResponseDto(PostImage postImage) {
        this.postImageId = postImage.getPostImageId();
        this.imageUrl = postImage.getImageUrl();
    }
}