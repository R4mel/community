package dev.community.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "social_id", nullable = false)
    private Long socialId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_image", length = 255, columnDefinition = "")
    private String profileImage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin; // null 체크가 필요없으니까 boolean 사용 -> unboxing 해줄 필요가 없기 때문에 시간 단축, default: false

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    // 연관 관계의 주인이 아님 (읽기만 가능)
    // User(One) to Post(Many)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
    /*
   1. @OneToMany: User 엔티티 하나가 Post 엔티티 여러 개와 관계를 맺는다는 것을 명시합니다. "일대다" 관계입니다.
   * `mappedBy = "user"`: (양방향 관계의 핵심) 이 속성은 이 관계의 주인이 누구인지를 알려줍니다. 값으로는 `Post` 엔티티에 있는
     `User` 타입 필드의 이름(`private User user;`)을 적어줍니다.
   * mappedBy는 "나는 주인이 아니며, Post 엔티티의 user 필드에 의해 매핑되었다"라고 선언하는 것과 같습니다. 이를 통해 JPA는
     users 테이블에 불필요한 post_id 컬럼을 만들지 않고, posts.user_id를 통해 이미 존재하는 관계를 재사용합니다. 이 속성이 없으면
     JPA는 중간에 별도의 조인 테이블을 만들어버리므로 반드시 설정해야 합니다.
   * `cascade = CascadeType.ALL`: '영속성 전이' 옵션입니다. User 엔티티에 대한 변경(저장, 삭제 등)이 연관된 Post 엔티티에도
     전파되도록 합니다. 예를 들어, 어떤 User를 삭제하면 그 유저가 작성한 모든 Post도 함께 데이터베이스에서 삭제됩니다.
   * `orphanRemoval = true`: '고아 객체 제거' 옵션입니다. user.getPosts().remove(post)처럼 User의 posts 컬렉션에서
     특정 Post를 제거하면, 그 Post는 데이터베이스에서도 삭제됩니다. 부모(User)와 관계가 끊어진 자식(Post)을 자동으로 제거하는 기능입니다.

   2. private List<Post> posts = new ArrayList<>();: User가 작성한 Post 목록을 담기 위한 컬렉션 필드입니다.
   ArrayList로 초기화해두면 NullPointerException을 방지할 수 있습니다.

    핵심 요약
    * 관계의 주인: 외래키(user_id)가 있는 Post 엔티티가 주인입니다.
    * 주인의 설정 (`Post`): @ManyToOne으로 관계를 명시하고, @JoinColumn으로 실제 FK 컬럼을 지정합니다.
    * 주인이 아닌 쪽의 설정 (`User`): @OneToMany로 관계를 명시하고, mappedBy 속성을 사용해 "나는 주인이 아니다"라고 선언하며,
    주인이 되는 필드명을 값으로 넣어줍니다.
    * 성능 최적화: N+1 문제를 피하기 위해 Fetch 전략은 항상 LAZY (지연 로딩)로 설정하는 것이 좋습니다.

    이와 같이 설정하면, Post를 저장할 때 post.setUser(user)를 통해 외래키가 올바르게 posts 테이블에 저장되며, User 객체를 통해서는
    user.getPosts()로 해당 유저의 모든 게시글을 쉽게 조회할 수 있습니다.
     */

