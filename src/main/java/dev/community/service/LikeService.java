package dev.community.service;

import dev.community.entity.Like;
import dev.community.entity.LikeId;
import dev.community.entity.Post;
import dev.community.entity.User;
import dev.community.repository.LikeRepository;
import dev.community.repository.PostRepository;
import dev.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LikeId likeId = new LikeId(user.getUserId(), post.getPostId());

        Optional<Like> likeOptional = likeRepository.findById(likeId);

        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(newLike);
        }
    }

    public long countLikes(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalStateException("Post not found");
        }
        return likeRepository.countByLikeId_PostId(postId);
    }
}
