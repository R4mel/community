package dev.community.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.community.dto.PostRequestDto;
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
                             @RequestParam("images") List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User is not authenticated when submitting post.");
            // Handle unauthenticated user, e.g., redirect to login page
            return "redirect:/login"; // Or an appropriate error page
        }

        UserDetails userDetails = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("Principal is UserDetails. Username: {}", userDetails.getUsername());
        } else if (authentication.getPrincipal() instanceof String) {
            logger.warn("Principal is String: {}. This might indicate anonymous user or misconfiguration.", authentication.getPrincipal());
        } else {
            logger.warn("Principal is unknown type: {}", authentication.getPrincipal().getClass().getName());
        }

        if (userDetails == null) {
            logger.error("Could not retrieve UserDetails from authentication principal.");
            return "redirect:/error"; // Or an appropriate error page
        }

        postService.createPost(postRequestDto, images, userDetails);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editPostForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        return "posts/edit";
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute PostRequestDto postRequestDto,
                             @RequestParam("addImages") List<MultipartFile> addImages,
                             @RequestParam("deleteImages") List<Long> deleteImages,
                             @AuthenticationPrincipal UserDetails userDetails) {
        postService.updatePost(id, postRequestDto, addImages, deleteImages, userDetails);
        return "redirect:/posts/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails);
        return "redirect:/posts";
    }
}
