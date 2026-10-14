package com.aijobtrainer.controller;

import com.aijobtrainer.dto.PasswordChangeRequest;
import com.aijobtrainer.dto.ProfileUpdateRequest;
import com.aijobtrainer.dto.UserResponse;
import com.aijobtrainer.security.UserPrincipal;
import com.aijobtrainer.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse user = userService.getProfile(principal.getId());
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                             @RequestBody ProfileUpdateRequest request) {
        UserResponse user = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully.",
                "user", user
        ));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@AuthenticationPrincipal UserPrincipal principal,
                                                              @Valid @RequestBody PasswordChangeRequest request) {
        userService.updatePassword(principal.getId(), request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }

    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteAccount(principal.getId());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));
    }
}
