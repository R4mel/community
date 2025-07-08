package dev.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotNull(message = "소셜 ID는 필수입니다.")
    private Long socialId;

    @NotBlank(message = "닉네임을 입력하새요.")
    private String nickname;

    @NotBlank(message = "프로필 사진을 첨부하세요.")
    private String profileImage;
}
