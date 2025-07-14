package dev.community.dto;

import dev.community.entity.PostImage;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImageResponseDto {
    private Long imageId;
    private String imageUrl;
    private String originalFileName;

    public PostImageResponseDto(PostImage postImage) {
        this.imageId = postImage.getPostImageId();
        this.imageUrl = postImage.getImageUrl();
        this.originalFileName = postImage.getOriginalFilename() != null ? 
            postImage.getOriginalFilename() : extractFileNameFromUrl(postImage.getImageUrl());
    }
    
    private String extractFileNameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return "unknown.jpg";
        }
        String[] parts = imageUrl.split("/");
        if (parts.length > 0) {
            String fileName = parts[parts.length - 1];
            // Remove UUID prefix if present (format: uuid_originalname)
            if (fileName.contains("_")) {
                String[] nameParts = fileName.split("_", 2);
                if (nameParts.length > 1) {
                    return nameParts[1]; // Return original filename without UUID
                }
            }
            return fileName;
        }
        return "unknown.jpg";
    }
}