package dev.community.dto;

import dev.community.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long userId;
    private Long socialId;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isAdmin;
    private Integer totalPoints;

    public UserResponseDto(User user) {
        this.userId = user.getUserId();
        this.socialId = user.getSocialId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isAdmin = user.isAdmin();
        this.totalPoints = user.getTotalPoints();
    }
}
