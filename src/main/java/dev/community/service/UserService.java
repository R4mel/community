package dev.community.service;

import dev.community.dto.UserRequestDto;
import dev.community.dto.UserResponseDto;
import dev.community.dto.UserUpdateRequestDto;
import dev.community.entity.User;
import dev.community.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto socialLoginOrRegister(UserRequestDto requestDto) {
        Optional<User> existingUser = userRepository.findBySocialId(requestDto.getSocialId());

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (!requestDto.getNickname().equals(user.getNickname())) {
                user.setNickname(requestDto.getNickname());
            }
            if (!requestDto.getProfileImage().equals(user.getProfileImage())) {
                user.setProfileImage(requestDto.getProfileImage());
            }
            user.setUpdatedAt(LocalDateTime.now());
        } else {
            user = User.builder()
                    .socialId(requestDto.getSocialId())
                    .nickname(requestDto.getNickname())
                    .profileImage(requestDto.getProfileImage())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isAdmin(false)
                    .totalPoints(0)
                    .build();
            userRepository.save(user);
        }
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (requestDto.getNickname() != null && !requestDto.getNickname().isEmpty()) {
            user.setNickname(requestDto.getNickname());
        }

        if (requestDto.getProfileImage() != null && !requestDto.getProfileImage().isEmpty()) {
            user.setProfileImage(requestDto.getProfileImage());
        }
        user.setUpdatedAt(LocalDateTime.now());

        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        userRepository.delete(userToDelete);

        log.info("User with ID {} has been successfully deleted.", userId);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }
}
