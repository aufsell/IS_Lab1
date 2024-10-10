package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin";
    }
    @PostMapping("/changerole/{id}")
    public String assignRole(@PathVariable Long id) {
        userService.changeRole(id);
        return "redirect:/admin/users";
    }
}