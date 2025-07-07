package dev.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id")
    private Long postImageId;

    @Column(name = "image_url", columnDefinition = "")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY) // Many(PostImage) to One(Post)
    @JoinColumn(name = "post_id")
    private Post post;
}
