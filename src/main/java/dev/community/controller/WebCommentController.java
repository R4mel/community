package dev.community.controller;

import dev.community.dto.CommentRequestDto;
import dev.community.entity.User;
import dev.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class WebCommentController {

    private final CommentService commentService;

    @PostMapping
    public String createComment(@RequestParam Long postId,
                               @RequestParam String content,
                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            CommentRequestDto commentRequestDto = new CommentRequestDto();
            commentRequestDto.setContent(content);
            
            Long userId;
            if (userDetails instanceof User) {
                userId = ((User) userDetails).getUserId();
            } else {
                // Handle case where userDetails is not a User instance
                return "redirect:/login";
            }
            
            commentService.createComment(postId, commentRequestDto, userId);
        } catch (Exception e) {
            // Log error
        }
        
        return "redirect:/posts/" + postId;
    }

    @DeleteMapping("/{commentId}")
    @ResponseBody
    public String deleteComment(@PathVariable Long commentId,
                               @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "error";
        }

        try {
            Long userId;
            if (userDetails instanceof User) {
                userId = ((User) userDetails).getUserId();
            } else {
                return "error";
            }
            
            commentService.deleteComment(commentId, userId);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
} 