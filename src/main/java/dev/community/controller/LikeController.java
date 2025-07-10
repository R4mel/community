package dev.community.controller;

import dev.community.dto.LikeResponseDto;
import dev.community.entity.User;
import dev.community.repository.UserRepository;
import dev.community.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}")
public class LikeController {
    private final LikeService likeService;
    private final UserRepository userRepository;

    @PostMapping("/like")
    public ResponseEntity<LikeResponseDto> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findBySocialId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        likeService.toggleLike(postId, user.getUserId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long likeCount = likeService.countLikes(postId);
        return ResponseEntity.ok(likeCount);
    }
}

