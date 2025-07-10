package dev.community.controller;

import dev.community.dto.PostRequestDto;
import dev.community.dto.PostResponseDto;
import dev.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("requestDto") PostRequestDto postRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponseDto postResponseDto = postService.createPost(postRequestDto, imageFiles, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        PostResponseDto postResponseDto = postService.getPostById(postId);
        return ResponseEntity.ok(postResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        List<PostResponseDto> posts;
        if (keyword != null) {
            posts = postService.searchPosts(keyword);
        } else if (categoryId != null) {
            posts = postService.getPostsByCategory(categoryId);
        } else {
            posts = postService.getAllPosts();
        }
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestPart("requestDto") PostRequestDto postRequestDto,
            @RequestPart(value = "addImages", required = false) List<MultipartFile> addImageFiles,
            @RequestParam(value = "deleteImages", required = false) List<Long> deleteImageIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponseDto postResponseDto = postService.updatePost(postId, postRequestDto, addImageFiles, deleteImageIds, userDetails);
        return ResponseEntity.ok(postResponseDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
