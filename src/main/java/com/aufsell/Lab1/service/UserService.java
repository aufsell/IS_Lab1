package com.aufsell.Lab1.service;

import com.aufsell.Lab1.model.Role;
import com.aufsell.Lab1.model.User;
import com.aufsell.Lab1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void changeRole(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> currentRoles = user.getRoles();

        if (currentRoles.contains(Role.ROLE_ADMIN)) {
            currentRoles.remove(Role.ROLE_ADMIN);
            currentRoles.add(Role.ROLE_USER);
        } else {
            currentRoles.remove(Role.ROLE_USER);
            currentRoles.add(Role.ROLE_ADMIN);
        }

        user.setRoles(currentRoles);
        userRepository.save(user);
    }
}
