package com.softwiz.osa.user.service;

import com.softwiz.osa.user.component.JwtTokenProvider;
import com.softwiz.osa.user.dto.UserLoginRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.exception.UnauthorizedException;
import com.softwiz.osa.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public String authenticate(UserLoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail());
            if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new UnauthorizedException("Invalid email or password");
            }
            return tokenProvider.generateToken(user.getUsername());
        } catch (UnauthorizedException e) {
            //System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // Log or handle other exceptions
            //System.out.println("Unexpected error during authentication: " + e.getMessage());
            throw new RuntimeException("Unexpected error during authentication", e);
        }
    }
}