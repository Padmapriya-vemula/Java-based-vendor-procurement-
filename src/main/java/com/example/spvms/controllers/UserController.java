package com.example.spvms.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public String getAllUsers() {
        return "Get all users";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        return "Get user";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody Object user) {
        return "Update user";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "Delete user";
    }
}