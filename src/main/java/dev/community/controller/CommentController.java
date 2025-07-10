package dev.community.controller;

import dev.community.dto.CommentRequestDto;
import dev.community.dto.CommentResponseDto;
import dev.community.entity.User;
import dev.community.repository.UserRepository;
import dev.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findBySocialId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CommentResponseDto commentResponseDto = commentService.createComment(postId, commentRequestDto, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findBySocialId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        CommentResponseDto commentResponseDto = commentService.updateComment(commentId, commentRequestDto, user.getUserId());
        return ResponseEntity.ok(commentResponseDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findBySocialId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        commentService.deleteComment(commentId, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}





