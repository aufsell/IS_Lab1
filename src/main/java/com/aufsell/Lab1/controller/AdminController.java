package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.dto.UserDTO;
import com.aufsell.Lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/{userId}/grant-admin")
    public ResponseEntity<String> grantAdmin(@PathVariable Long userId) {
        userService.grantAdmin(userId);
        return ResponseEntity.ok("User granted admin rights.");
    }

    @PutMapping("/users/{userId}/revoke-admin")
    public ResponseEntity<String> revokeAdmin(@PathVariable Long userId) {
        userService.revokeAdmin(userId);
        return ResponseEntity.ok("User admin rights revoked.");
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}

