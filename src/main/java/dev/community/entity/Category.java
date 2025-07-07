package dev.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_status")
    @Enumerated(EnumType.STRING)
    private CategoryStatus categoryStatus;

    public void setCategoryStatus(CategoryStatus CategoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    // Category(1) to Post(Many)
    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();
}
