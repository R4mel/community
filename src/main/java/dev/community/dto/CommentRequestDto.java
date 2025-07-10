package dev.community.dto;

import dev.community.entity.Comment;
import dev.community.entity.Post;
import dev.community.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .content(this.content)
                .post(post)
                .user(user)
                .build();
    }
}
