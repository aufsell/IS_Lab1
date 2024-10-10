package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.model.Role;
import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.HashSet;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("message", "User exists");
            return "registration";
        }
        if (userRepository.count() == 0) {
            user.setRoles(new HashSet<>(Collections.singleton(Role.ROLE_ADMIN)));
        } else {
            user.setRoles(new HashSet<>(Collections.singleton(Role.ROLE_USER)));
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "redirect:/login";

    }
}
