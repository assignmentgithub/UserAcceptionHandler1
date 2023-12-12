package com.softwiz.osa.user.service;

import com.softwiz.osa.user.component.JwtTokenProvider;
import com.softwiz.osa.user.dto.UserLoginRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.exception.UnauthorizedException;
import com.softwiz.osa.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticate_Successful() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("pradeip@gmail.com", "Password@123");
        User mockUser = new User();
        mockUser.setEmail("pradeip@gmail.com");
        mockUser.setPassword(passwordEncoder.encode("Password@123"));

        when(userRepository.findByEmail("pradeip@gmail.com")).thenReturn(mockUser);
        when(passwordEncoder.matches("Password@123", mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken("pradeip@gmail.com")).thenReturn("mockedToken");
        // Act
        String token = authService.authenticate(loginRequest);
        // Assert
        assertEquals("mockedToken", token);
        verify(userRepository, times(1)).findByEmail("pradeip@gmail.com");
        verify(passwordEncoder, times(1)).matches("Password@123", mockUser.getPassword());
        verify(jwtTokenProvider, times(1)).generateToken("pradeip@gmail.com");
    }

    @Test
    void testAuthenticateFailure_InvalidPassword() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("test@example.com", "wrongPassword");
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword(passwordEncoder.encode("password"));

        Mockito.when(userRepository.findByEmail(any(String.class))).thenReturn(mockUser);
        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(loginRequest));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", mockUser.getPassword());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void testAuthenticateFailure_EmailNotFound() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("nonexistent@gmail.com", "Password@123");

        Mockito.when(userRepository.findByEmail(any(String.class))).thenReturn(null);

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(loginRequest));

        verify(userRepository, times(1)).findByEmail("nonexistent@gmail.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void testAuthenticate_UnauthorizedException() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenThrow(new UnauthorizedException("Invalid email or password"));

        // Act and Assert
        assertThrows(UnauthorizedException.class, () -> authService.authenticate(loginRequest));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void testAuthenticate_RuntimeException() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenThrow(new RuntimeException("Unexpected error"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> authService.authenticate(loginRequest));

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}