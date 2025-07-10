package dev.community.repository;

import dev.community.entity.Category;
import dev.community.entity.Post;
import dev.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(Category category);

    List<Post> findByUser(User user);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
