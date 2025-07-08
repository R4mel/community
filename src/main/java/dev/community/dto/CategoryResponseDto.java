package dev.community.dto;

import dev.community.entity.Category;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    private Long categoryId;
    private Long categoryStatusCode;
    private String categoryStatusCodeDescription;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryStatusCode = category.getCategoryStatus().getCode();
        this.categoryStatusCodeDescription = category.getCategoryStatus().getDescription();
    }
}
