package dev.community.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.community.dto.PostRequestDto;
import dev.community.dto.PostResponseDto;
import dev.community.entity.User;
import dev.community.service.CategoryService;
import dev.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class WebPostController {

    private static final Logger logger = LoggerFactory.getLogger(WebPostController.class);

    private final PostService postService;
    private final CategoryService categoryService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public String getPosts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Accessing posts list page");
        try {
            List<PostResponseDto> posts = postService.getAllPosts();
            logger.info("Found {} posts", posts.size());
            model.addAttribute("posts", posts);
            
            // Add nickname to model if user is authenticated
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                model.addAttribute("nickname", user.getNickname());
            }
        } catch (Exception e) {
            logger.error("Error getting posts: ", e);
            model.addAttribute("posts", List.of());
        }
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Accessing post detail page for ID: {}", id);
        try {
            PostResponseDto post = postService.getPostById(id);
            logger.info("Found post: {} with ID: {}", post.getTitle(), post.getPostId());
            
            // Debug image information
            if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
                logger.info("Post has {} images:", post.getPostImages().size());
                post.getPostImages().forEach(image -> 
                    logger.info("  Image: {} -> URL: {}", image.getOriginalFileName(), image.getImageUrl()));
            } else {
                logger.info("Post has no images");
            }
            
            model.addAttribute("post", post);
            
            // Add nickname to model if user is authenticated
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                model.addAttribute("nickname", user.getNickname());
            }
            
            // Add categories for edit form if user is authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                authentication.getPrincipal() instanceof User) {
                model.addAttribute("categories", categoryService.getAllCategories());
            }
        } catch (Exception e) {
            logger.error("Error getting post with ID {}: ", id, e);
            return "redirect:/posts";
        }
        return "posts/detail";
    }

    @GetMapping("/new")
    public String newPostForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Accessing new post form");
        try {
            model.addAttribute("post", new PostRequestDto());
            model.addAttribute("categories", categoryService.getAllCategories());
            
            // Add nickname to model if user is authenticated
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                model.addAttribute("nickname", user.getNickname());
            }
            
            logger.info("New post form loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading new post form: ", e);
        }
        return "posts/new";
    }

    @PostMapping
    public String createPost(@ModelAttribute PostRequestDto postRequestDto,
                             @RequestParam(value = "images", required = false) List<MultipartFile> images,
                             @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Creating new post with title: {}", postRequestDto.getTitle());
        
        if (userDetails == null) {
            logger.error("User is not authenticated when submitting post.");
            return "redirect:/login";
        }

        try {
            postService.createPost(postRequestDto, images, userDetails);
            logger.info("Post created successfully");
        } catch (Exception e) {
            logger.error("Error creating post: ", e);
        }
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Accessing edit form for post ID: {}", id);
        try {
            PostResponseDto post = postService.getPostById(id);
            model.addAttribute("post", post);
            model.addAttribute("categories", categoryService.getAllCategories());
            
            // Add nickname to model if user is authenticated
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                model.addAttribute("nickname", user.getNickname());
            }
            
            logger.info("Edit form loaded successfully for post: {}", post.getTitle());
        } catch (Exception e) {
            logger.error("Error loading edit form for post ID {}: ", id, e);
            return "redirect:/posts";
        }
        return "posts/edit";
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute PostRequestDto postRequestDto,
                             @RequestParam(value = "addImages", required = false) List<MultipartFile> addImages,
                             @RequestParam(value = "deleteImages", required = false) List<Long> deleteImages,
                             @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Updating post with ID: {}", id);
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            postService.updatePost(id, postRequestDto, addImages, deleteImages, userDetails);
            logger.info("Post updated successfully");
        } catch (Exception e) {
            logger.error("Error updating post: ", e);
        }
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Deleting post with ID: {}", id);
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            postService.deletePost(id, userDetails);
            logger.info("Post deleted successfully");
            return "success";
        } catch (Exception e) {
            logger.error("Error deleting post: ", e);
            return "error";
        }
    }

    @GetMapping("/debug/images")
    @ResponseBody
    public String debugImages() {
        try {
            java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
            boolean exists = java.nio.file.Files.exists(uploadPath);
            boolean isDirectory = java.nio.file.Files.isDirectory(uploadPath);
            
            StringBuilder result = new StringBuilder();
            result.append("Upload directory: ").append(uploadDir).append("\n");
            result.append("Exists: ").append(exists).append("\n");
            result.append("Is directory: ").append(isDirectory).append("\n");
            
            if (exists && isDirectory) {
                result.append("Files in directory:\n");
                java.nio.file.Files.list(uploadPath)
                    .forEach(file -> result.append("  ").append(file.getFileName()).append("\n"));
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Error checking upload directory: " + e.getMessage();
        }
    }
}
