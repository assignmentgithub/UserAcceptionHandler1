package com.softwiz.osa.user.controller;

import com.softwiz.osa.user.dto.UserLoginRequest;
import com.softwiz.osa.user.dto.UserProfileUpdateRequest;
import com.softwiz.osa.user.dto.UserRegistrationRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.service.AuthService;
import com.softwiz.osa.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/osa/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            User createUser = userService.registerUser(registrationRequest);
            if (createUser != null) {
                return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
            }else {
                return new ResponseEntity<>("Failed to create user", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        try {
            String token = authService.authenticate(loginRequest);
            return ResponseEntity.ok("User login successful. JWT Token: " + token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User with ID " + userId + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteUserById/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        try {
            boolean isDeleted = userService.deleteUserById(userId);

            if (isDeleted) {
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User with ID " + userId + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest updateRequest){
        try {
            userService.updateProfile(updateRequest);
            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}