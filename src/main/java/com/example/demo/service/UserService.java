package com.example.demo.service;

import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.User;
import com.example.demo.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.exception.PasswordMismatchException;

import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ResourceNotFoundException;
import java.util.List;
import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PermissionRepository permissionRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));
    }

    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        ));
        userRepository.delete(existingUser);
    }
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));
    }
    public User updateMyProfile(
            String email,
            UpdateProfileRequest request) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());

        return userRepository.save(existingUser);
    }
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        ));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new PasswordMismatchException(
                    "Current password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }
    public User assignPermission(
            Long userId,
            Long permissionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        ));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Permission not found with id: " + permissionId
                        ));

        if (user.getPermissions() == null) {
            user.setPermissions(new HashSet<>());
        }

        user.getPermissions().add(permission);

        return userRepository.save(user);
    }
    public Set<Permission> getUserPermissions(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId));

        return user.getPermissions();
    }
}