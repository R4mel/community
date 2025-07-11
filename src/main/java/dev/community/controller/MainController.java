package dev.community.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        final Logger logger = LoggerFactory.getLogger(MainController.class);
        if (authentication != null && authentication.getPrincipal() instanceof dev.community.entity.User user) {
                String nickname = user.getNickname();
                String profileImage = user.getProfileImage();

            model.addAttribute("nickname", nickname);
            model.addAttribute("profileImage", profileImage);
        }

        return "index";
    }
}
