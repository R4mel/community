package dev.community.service;

import dev.community.entity.Post;
import dev.community.entity.PostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private final FileStorageService fileStorageService;

    @Transactional
    public void addImageToPost(Post post, List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        for (MultipartFile imageFile : imageFiles) {
            String imageUrl = fileStorageService.uploadFile(imageFile);
            PostImage postImage = PostImage.builder()
                    .imageUrl(imageUrl)
                    .post(post)
                    .build();

            post.getPostImages().add(postImage);
        }
    }

    @Transactional
    public void deleteImagesFromPost(Post post, List<Long> postImageIds) {
        if (postImageIds == null || postImageIds.isEmpty()) {
            return;
        }

        List<PostImage> imagesToDelete = post.getPostImages().stream()
                .filter(image -> postImageIds.contains(image.getPostImageId()))
                .toList();

        for (PostImage image : imagesToDelete) {
            fileStorageService.deleteFile(image.getImageUrl());
            post.getPostImages().remove(image);
        }
    }

    @Transactional
    public void deleteAllImagesFromPost(Post post) {
        if (post.getPostImages() == null || post.getPostImages().isEmpty()) {
            return;
        }
        post.getPostImages()
                .forEach(image -> fileStorageService.deleteFile(image.getImageUrl()));
        post.getPostImages().clear();
    }
}
