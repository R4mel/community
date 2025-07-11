package dev.community.oauth2.service;

import dev.community.entity.User;
import dev.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String socialId = String.valueOf(attributes.get("id"));
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");

        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .nickname(nickname)
                                .profileImage(profileImage)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .totalPoints(0)
                                .isAdmin(false)
                                .build()
                ));

        user.setAttributes(attributes);
        return user;
    }
}