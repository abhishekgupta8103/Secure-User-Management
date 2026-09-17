package com.example.demo.controller;

import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(
            @RequestBody Permission permission) {

        return ResponseEntity.ok(
                permissionService.createPermission(permission)
        );
    }
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {

        return ResponseEntity.ok(
                permissionService.getAllPermissions()
        );
    }
}