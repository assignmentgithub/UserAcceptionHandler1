package com.softwiz.osa.user.service;

import com.softwiz.osa.user.dto.UserProfileUpdateRequest;
import com.softwiz.osa.user.dto.UserRegistrationRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.repository.UserRepository;
import com.softwiz.osa.user.security.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationFacade authenticationFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeep@gmail.com");
        registrationRequest.setPassword("Password@123");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        // Act
        User result = userService.registerUser(registrationRequest);
        // Assert
        assertEquals(registrationRequest.getFirstName(), result.getFirstName());
        assertEquals(registrationRequest.getLastName(), result.getLastName());
        assertEquals(registrationRequest.getMobileNumber(), result.getMobileNumber());
        assertEquals(registrationRequest.getUsername(), result.getUsername());
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        // Password should be encoded
        assertTrue(passwordEncoder.matches(registrationRequest.getPassword(), result.getPassword()));
    }
    @Test
    public void testRegisterUser_InvalidEmailFormat() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeip@gmail.i");
        registrationRequest.setPassword("Password@123");
        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Invalid email address format", exception.getMessage());
    }
    @Test
    public void testRegisterUser_EmailAlreadyInUse() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeep@gmail.com");
        registrationRequest.setPassword("Password@123");
        // Mock behavior for userRepository and passwordEncoder
        when(userRepository.findByEmail(eq(registrationRequest.getEmail()))).thenReturn(new User());
        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Email already in use", exception.getMessage());
    }
    @Test
    public void testRegisterUser_UsernameAlreadyInUse() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeep@gmail.com");
        registrationRequest.setPassword("Password@123");
        // Mock behavior for userRepository and passwordEncoder
        when(userRepository.findByUsername(eq(registrationRequest.getUsername()))).thenReturn(new User());
        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Username already in use", exception.getMessage());
    }
    @Test
    public void testRegisterUser_MobileNumberAlreadyInUse() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeep@gmail.com");
        registrationRequest.setPassword("Password@123");
        // Mock behavior for userRepository and passwordEncoder
        when(userRepository.findByMobileNumber(eq(registrationRequest.getMobileNumber()))).thenReturn(new User());
        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Mobile number already in use", exception.getMessage());
    }
    @Test
    public void testRegisterUser_InvalidPasswordFormat() {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstName("Pradeep");
        registrationRequest.setLastName("Raj");
        registrationRequest.setMobileNumber(1234567890);
        registrationRequest.setUsername("Pradeep");
        registrationRequest.setEmail("pradeep@gmil.com");
        registrationRequest.setPassword("Pr@1");
        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationRequest));
        assertEquals("Invalid password format.\nPassword must be at least 8 characters long and \n" +
                "contain at least one uppercase letter, \none lowercase letter, \none digit, and \none special character (@#$%^&+=!).", exception.getMessage());
    }
    @Test
    public void testGetAllUsers() {
        // Mocking the repository method
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(mockUsers);
        // Calling the service method
        List<User> result = userService.getAllUsers();
        // Verifying the result
        assertEquals(mockUsers, result);
    }
    @Test
    public void testGetUserById() {
        // Mocking the repository method
        User mockUser = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        // Calling the service method
        User result = userService.getUserById(1L);
        // Verifying the result
        assertEquals(mockUser, result);
    }
    @Test
    public void testGetUserByIdNotFound() {
        // Mocking the repository method for a case where the user is not found
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Calling the service method
        User result = userService.getUserById(1L);
        // Verifying the result
        assertEquals(null, result);
    }
    @Test
    public void testDeleteUserById_Success() {
        // Arrange
        Long userIdToDelete = 1L;
        User userToDelete = new User();  // Assuming you have a User class
        when(userRepository.findById(userIdToDelete)).thenReturn(Optional.of(userToDelete));
        // Act
        boolean isDeleted = userService.deleteUserById(userIdToDelete);
        // Assert
        assertTrue(isDeleted);
        verify(userRepository, times(1)).delete(userToDelete);
    }
    @Test
    public void testDeleteUserById_NotFound() {
        // Arrange
        Long userIdNotFound = 2L;
        when(userRepository.findById(userIdNotFound)).thenReturn(Optional.empty());
        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(userIdNotFound));
    }
    @Test
    public void testUpdateProfile() {
        // Arrange
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setEmail("test@example.com");
        updateRequest.setUsername("testUser");
        updateRequest.setMobileNumber(1234567890);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(authenticationFacade.getCurrentUser()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn("testUsername");

        User existingUser = new User(); // Set up an existing user as needed
        when(userRepository.findByUsername("testUsername")).thenReturn(existingUser);
        when(userRepository.findByEmail(updateRequest.getEmail())).thenReturn(null);
        when(userRepository.findByUsername(updateRequest.getUsername())).thenReturn(null);
        when(userRepository.findByMobileNumber(updateRequest.getMobileNumber())).thenReturn(null);

        userService.updateProfile(updateRequest);
    }
}