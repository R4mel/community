package dev.community.service;

import dev.community.dto.PostRequestDto;
import dev.community.dto.PostResponseDto;
import dev.community.entity.Category;
import dev.community.entity.Post;
import dev.community.entity.User;
import dev.community.repository.CategoryRepository;
import dev.community.repository.PostRepository;
import dev.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostImageService postImageService;

    /**
     * 게시글 생성
     *
     * @param postRequestDto 게시글 제목, 내용, 카테고리 ID
     * @param imageFiles     업로드할 이미지 파일 목록
     * @param userDetails    현재 인증된 사용자 정보
     * @return 생성된 게시글의 정보를 담은 DTO(PostResponseDto)
     */
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, List<MultipartFile> imageFiles, UserDetails userDetails) {
        User user = userRepository.findBySocialId(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userDetails.getUsername()));

        Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + postRequestDto.getCategoryId()));

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .user(user)
                .category(category)
                .build();

        Post savedPost = postRepository.save(post);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            postImageService.addImageToPost(savedPost, imageFiles);
        }

        return new PostResponseDto(savedPost);
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param postId         수정할 게시글의 ID
     * @param postRequestDto 수정할 제목, 내용, 카테고리 ID
     * @param addImageFiles  새롭게 추가할 이미지 파일 목록
     * @param deleteImageIds 삭제할 기존 이미지들의 ID 목록
     * @param userDetails    현재 인증된 사용자 정보
     * @return 수정된 게시글의 상세 정보가 담긴 DTO
     */
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, List<MultipartFile> addImageFiles, List<Long> deleteImageIds, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getSocialId().equals(userDetails.getUsername())) {
            throw new SecurityException("게시글 수정 권한이 없습니다.");
        }

        Category category = categoryRepository.findById(postRequestDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + postRequestDto.getCategoryId()));

        post.update(postRequestDto.getTitle(), postRequestDto.getContent(), category);

        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            postImageService.deleteImagesFromPost(post, deleteImageIds);
        }

        if (addImageFiles != null && !addImageFiles.isEmpty()) {
            postImageService.addImageToPost(post, addImageFiles);
        }

        return new PostResponseDto(post);
    }

    /**
     * 모든 게시글을 조회합니다.
     *
     * @return 모든 게시글의 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 게시글을 상세 조회합니다.
     *
     * @param postId 조회할 게시글의 ID
     * @return 특정 게시글의 상세 정보가 담긴 DTO
     */
    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));
        post.increaseViewCount();
        return new PostResponseDto(post);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId      삭제할 게시글의 ID
     * @param userDetails 현재 인증된 사용자 정보
     */
    @Transactional
    public void deletePost(Long postId, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다: " + postId));

        if (!post.getUser().getSocialId().equals(userDetails.getUsername())) {
            throw new SecurityException("게시글 삭제 권한이 없습니다.");
        }

        postImageService.deleteAllImagesFromPost(post);
        postRepository.delete(post);
    }

    /**
     * 특정 카테고리의 모든 게시물을 조회합니다.
     *
     * @param categoryId 조회할 카테고리의 ID
     * @return 해당 카테고리의 모든 게시글 정보가 담긴 DTO 리스트
     */

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + categoryId));

        return postRepository.findByCategory(category).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 모든 게시글을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 모든 게시글 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));

        return postRepository.findByUser(user).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 키워드로 게시글을 검색합니다.
     *
     * @param keyword 검색할 키워드
     * @return 검색된 게시글의 정보가 담긴 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> searchPosts(String keyword) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }
}
