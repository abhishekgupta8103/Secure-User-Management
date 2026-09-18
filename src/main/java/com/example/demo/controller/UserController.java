package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.PermissionResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;
    }

    // =========================
    // GET USER BY ID
    // =========================

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(
                userMapper.toUserResponse(user)
        );
    }

    // =========================
    // UPDATE USER
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User updatedUser) {

        User user = userService.updateUser(id, updatedUser);

        return ResponseEntity.ok(
                userMapper.toUserResponse(user)
        );
    }

    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }

    // =========================
    // MY PROFILE
    // =========================

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                userMapper.toUserResponse(user)
        );
    }

    // =========================
    // UPDATE MY PROFILE
    // =========================

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        User user = userService.updateMyProfile(
                email,
                request
        );

        return ResponseEntity.ok(
                userMapper.toUserResponse(user)
        );
    }

    // =========================
    // CHANGE PASSWORD
    // =========================

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(
                email,
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }

    // =========================
    // ASSIGN PERMISSION
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/permissions/{permissionId}")
    public ResponseEntity<UserResponse> assignPermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {

        User user = userService.assignPermission(
                userId,
                permissionId
        );

        return ResponseEntity.ok(
                userMapper.toUserResponse(user)
        );
    }

    // =========================
    // GET USER PERMISSIONS
    // =========================

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}/permissions")
    public ResponseEntity<Set<PermissionResponse>> getUserPermissions(
            @PathVariable Long userId) {

        Set<PermissionResponse> response =
                userService.getUserPermissions(userId)
                        .stream()
                        .map(permission -> new PermissionResponse(
                                permission.getId(),
                                permission.getName()
                        ))
                        .collect(Collectors.toSet());

        return ResponseEntity.ok(response);
    }
}