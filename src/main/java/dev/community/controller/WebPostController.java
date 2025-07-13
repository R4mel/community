package dev.community.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.community.dto.PostRequestDto;
import dev.community.entity.User;
import dev.community.service.CategoryService;
import dev.community.service.PostService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public String getPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        // Add categories for edit form if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof User) {
            model.addAttribute("categories", categoryService.getAllCategories());
        }
        return "posts/detail";
    }

    @GetMapping("/new")
    public String newPostForm(Model model) {
        logger.info("Accessing /posts/new");
        model.addAttribute("post", new PostRequestDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "posts/new";
    }

    @PostMapping
    public String createPost(@ModelAttribute PostRequestDto postRequestDto,
                             @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated when submitting post.");
            return "redirect:/login";
        }

        User user = null;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
            logger.info("Principal is User. Username: {}", user.getUsername());
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("Principal is UserDetails. Username: {}", userDetails.getUsername());
            // You might need to fetch the User entity here if needed
        } else {
            logger.warn("Principal is unknown type: {}", authentication.getPrincipal().getClass().getName());
            return "redirect:/login";
        }

        if (user == null) {
            logger.error("Could not retrieve User from authentication principal.");
            return "redirect:/login";
        }

        postService.createPost(postRequestDto, images, user);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "posts/edit";
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute PostRequestDto postRequestDto,
                             @RequestParam(value = "addImages", required = false) List<MultipartFile> addImages,
                             @RequestParam(value = "deleteImages", required = false) List<Long> deleteImages,
                             @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        postService.updatePost(id, postRequestDto, addImages, deleteImages, user);
        return "redirect:/posts/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        postService.deletePost(id, user);
        return "redirect:/posts";
    }
}
