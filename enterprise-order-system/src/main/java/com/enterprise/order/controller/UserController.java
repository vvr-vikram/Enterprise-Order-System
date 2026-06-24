package com.enterprise.order.controller;

import com.enterprise.order.user.User;
import com.enterprise.order.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
