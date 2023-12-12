package com.softwiz.osa.user.controller;

import com.softwiz.osa.user.dto.UserLoginRequest;
import com.softwiz.osa.user.dto.UserProfileUpdateRequest;
import com.softwiz.osa.user.dto.UserRegistrationRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.exception.UnauthorizedException;
import com.softwiz.osa.user.service.AuthService;
import com.softwiz.osa.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testRegisterUser_Success() {
        //Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(new User());
        //Act
        ResponseEntity<?> response = userController.register(registrationRequest);
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody());
    }
    @Test
    public void testRegisterUser_Failure() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(null);
        // Act
        ResponseEntity<?> response = userController.register(registrationRequest);
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to create user", response.getBody());
    }
    @Test
    public void testRegisterUser_Exception() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenThrow(new RuntimeException("Bad Request"));
        // Act
        ResponseEntity<?> response = userController.register(registrationRequest);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody());
    }
    @Test
    void testLogin_Success() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("username", "password");
        Mockito.when(authService.authenticate(any(UserLoginRequest.class))).thenReturn("mockedToken");
        // Act
        ResponseEntity<?> responseEntity = userController.login(loginRequest);
        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("User login successful. JWT Token: mockedToken", responseEntity.getBody());
    }
    @Test
    void testLoginFailure_Unauthorized() {
        // Arrange
        UserLoginRequest loginRequest = new UserLoginRequest("username", "password");
        Mockito.when(authService.authenticate(any(UserLoginRequest.class))).thenThrow(new UnauthorizedException("Unauthorized"));
        // Act
        ResponseEntity<?> responseEntity = userController.login(loginRequest);
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Unauthorized", responseEntity.getBody());
    }
    @Test
    void testGetUserById_Success() {
        // Mocking the userService behavior
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setFirstName("Pradeep");
        mockUser.setLastName("Raj");
        mockUser.setUsername("Pradeip");
        mockUser.setEmail("pradeip@gmail.com");
        mockUser.setMobileNumber(1234567890);
        when(userService.getUserById(userId)).thenReturn(mockUser);
        // Calling the controller method
        ResponseEntity<?> responseEntity = userController.getUserById(userId);
        // Verifying the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUser, responseEntity.getBody());
    }
    @Test
    void testGetUserById_NotFound() {
        // Mocking the userService behavior for a not found scenario
        Long userId = 2L;
        when(userService.getUserById(userId)).thenReturn(null);
        // Calling the controller method
        ResponseEntity<?> responseEntity = userController.getUserById(userId);
        // Verifying the response
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + userId + " not found", responseEntity.getBody());
    }
    @Test
    void testGetUserByIdInternalServerError() {
        // Mocking the userService behavior for an internal server error scenario
        Long userId = 3L;
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("Internal Server Error"));
        // Calling the controller method
        ResponseEntity<?> responseEntity = userController.getUserById(userId);
        // Verifying the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal Server Error", responseEntity.getBody());
    }
    @Test
    public void testGetAllUsers() {
        // Mocking the service method
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(mockUsers);
        // Calling the controller method
        ResponseEntity<List<User>> responseEntity = userController.getAllUsers();
        // Verifying the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUsers, responseEntity.getBody());
    }
    @Test
    public void testGetAllUsersError() {
        // Mocking the service method to throw an exception
        when(userService.getAllUsers()).thenThrow(new RuntimeException("User not found"));
        // Calling the controller method
        ResponseEntity<List<User>> responseEntity = userController.getAllUsers();
        // Verifying the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    public void testDeleteUserById_Success() {
        Long userId = 1L;
        when(userService.deleteUserById(userId)).thenReturn(true);
        ResponseEntity<?> response = userController.deleteUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
    }
    @Test
    public void testDeleteUserById_UserNotFound() {
        Long userId = 2L;
        when(userService.deleteUserById(userId)).thenReturn(false);
        ResponseEntity<?> response = userController.deleteUserById(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with ID " + userId + " not found", response.getBody());
    }
    @Test
    public void testDeleteUserByIdWith_NullUserId() {
        // Arrange
        Long nullUserId = null;
        // Mock the userService to throw an IllegalArgumentException when a null user ID is provided
        when(userService.deleteUserById(nullUserId)).thenThrow(new IllegalArgumentException("User ID cannot be null"));
        // Act
        ResponseEntity<?> responseEntity = userController.deleteUserById(nullUserId);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("User ID cannot be null", responseEntity.getBody());
    }
    @Test
    public void testDeleteUserByIdNotFound() {
        // Arrange
        Long userIdNotFound = 1L;
        // Mock the userService to throw a NoSuchElementException when the user is not found
        when(userService.deleteUserById(userIdNotFound)).thenThrow(new NoSuchElementException("User not found"));
        // Act
        ResponseEntity<?> responseEntity = userController.deleteUserById(userIdNotFound);
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User not found", responseEntity.getBody());
    }
    @Test
    public void testDeleteUserById_InternalServerError() {
        Long userId = 3L;
        // Simulating an exception in the service layer
        when(userService.deleteUserById(userId)).thenThrow(new RuntimeException("Some internal error"));
        ResponseEntity<?> response = userController.deleteUserById(userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());
    }
    @Test
    public void testUpdateProfile_Success() {
        // Arrange
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(/* provide necessary data */);
        // Mock the userService behavior
        Mockito.doNothing().when(userService).updateProfile(updateRequest);
        // Act
        ResponseEntity<?> responseEntity = userController.updateProfile(updateRequest);
        // Assert
        assertEquals("Profile updated successfully", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateProfile_ValidationFailure() {
        // Arrange
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(/* provide necessary data */);
        // Mock the userService behavior to throw IllegalArgumentException
        Mockito.doThrow(new IllegalArgumentException("Invalid input")).when(userService).updateProfile(updateRequest);
        // Act
        ResponseEntity<?> responseEntity = userController.updateProfile(updateRequest);
        // Assert
        assertEquals("Invalid input", responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateProfile_InternalServerError() {
        // Arrange
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(/* provide necessary data */);
        // Mock the userService behavior to throw a generic exception
        Mockito.doThrow(new RuntimeException("Something went wrong")).when(userService).updateProfile(updateRequest);
        // Act
        ResponseEntity<?> responseEntity = userController.updateProfile(updateRequest);
        // Assert
        assertEquals("Internal Server Error", responseEntity.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}