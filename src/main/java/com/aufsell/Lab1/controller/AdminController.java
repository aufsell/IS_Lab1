package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        List<User> filteredUsers = users.stream()
                .filter(user -> !"ROLE_ADMIN".equals(user.getRole().getName()))
                .toList();

        model.addAttribute("users", filteredUsers);
        return "admin";
    }
    @PostMapping("/approve/{id}")
    public String giveAdminRole(@PathVariable Long id) {
        userService.giveAdminRole(id);
        return "redirect:/admin/users";
    }
}
