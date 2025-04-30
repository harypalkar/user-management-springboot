package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();

        if (deleted) {
            response.put("message", "UserId#" + id + " deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "UserId#" + id + " is not a valid input, please enter a valid userId");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        Map<String, String> response = new HashMap<>();

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            response.put("message", "User# " + email + " is not available in the system. Please register..");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(password)) {
            response.put("message", "Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        user.setLoggedIn(true);
        userService.saveUser(user);

        response.put("message", "User# " + email + " login successful");
        return ResponseEntity.ok(response);
    }


    @PutMapping("/logout/{userId}")
    public ResponseEntity<Map<String, String>> logoutUser(@PathVariable Long userId) {
        Map<String, String> response = new HashMap<>();
        Optional<User> userOptional = userService.findById(userId);

        if (userOptional.isEmpty()) {
            response.put("message", "UserId# " + userId + " is not valid. Please enter a valid userId.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();

        if (!user.isLoggedIn()) {
            response.put("message", "User# " + user.getEmail() + " is not logged in. Please login to use this functionality...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setLoggedIn(false);
        userService.saveUser(user);

        response.put("message", "User# " + user.getEmail() + " logout successfully");
        return ResponseEntity.ok(response);
    }

}
