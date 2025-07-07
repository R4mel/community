package dev.community.entity;

import lombok.Getter;

@Getter
public enum CategoryStatus {
    FREE(0L, "자유게시판"),
    TIP(1L, "팁게시판"),
    QUESTION(2L, "질문게시판");

    private final Long code;
    private final String description;

    CategoryStatus(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
