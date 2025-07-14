package dev.community.dto;

import dev.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Long userId;
    private String authorNickname;
    private Long categoryId;
    private String categoryName;
    private List<PostImageResponseDto> postImages;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.viewCount = post.getViewCount() != null ? post.getViewCount() : 0;
        this.userId = post.getUser().getUserId();
        this.authorNickname = post.getUser().getNickname();
        this.categoryId = post.getCategory().getCategoryId();
        this.categoryName = post.getCategory().getCategoryStatus().getDescription();
        this.postImages = post.getPostImages().stream()
                .map(PostImageResponseDto::new)
                .collect(Collectors.toList());
        this.comments = post.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }
}
