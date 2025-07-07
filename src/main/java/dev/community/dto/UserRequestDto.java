package dev.community.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotNull(message = "소셜 ID는 필수입니다.")
    private Long socialId;
    private String nickname;
    private String profileImage;
}
