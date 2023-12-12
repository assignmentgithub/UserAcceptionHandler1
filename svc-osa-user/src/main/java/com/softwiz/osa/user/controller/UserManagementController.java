package com.softwiz.osa.user.controller;

import com.softwiz.osa.user.entity.CustomErrorResponse;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.exception.CustomException;
import com.softwiz.osa.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserManagementController {
    @GetMapping("/{id}")
    public ResponseEntity<CustomErrorResponse> getUserById(@PathVariable Long id) {
        // Simulating a scenario where the user is not found
        if (id == null || id <= 0) {
            // Throw a custom exception for user not found scenario
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        // Retrieve user logic (dummy user creation for demonstration)
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(id, "Lavanya", "lavanya@gmail.com");

        return ResponseEntity.ok().body(customErrorResponse);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        if (newUser == null) {
            // If the received user object is null, return a bad request response
            return ResponseEntity.badRequest().build();
        }
        Long generatedId = generateUniqueId();
        newUser.setId(generatedId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    private Long generateUniqueId() {
        // Simulated unique ID generation logic (replace this with a proper ID generation mechanism)
        return System.currentTimeMillis(); // For demonstration, using current time as ID
    }
}
