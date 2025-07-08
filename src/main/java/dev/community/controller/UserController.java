package dev.community.controller;

import dev.community.dto.UserRequestDto;
import dev.community.dto.UserResponseDto;
import dev.community.dto.UserUpdateRequestDto;
import dev.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/social-login")
    public ResponseEntity<UserResponseDto> socialLoginOrRegister(@RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.socialLoginOrRegister(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        UserResponseDto responseDto = userService.getUserProfile(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserInfo(@PathVariable Long userId, @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto responseDto = userService.updateUserInfo(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}