package com.aufsell.Lab1.controller;

import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.repository.UserRepository;
import com.aufsell.Lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/collections")
    public String collectionPage(@AuthenticationPrincipal UserDetails user, Model model) {

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        return "collection";
    }

}