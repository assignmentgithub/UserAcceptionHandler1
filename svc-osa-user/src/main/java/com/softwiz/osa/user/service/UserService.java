package com.softwiz.osa.user.service;

import com.softwiz.osa.user.dto.UserProfileUpdateRequest;
import com.softwiz.osa.user.dto.UserRegistrationRequest;
import com.softwiz.osa.user.entity.User;
import com.softwiz.osa.user.repository.UserRepository;
import com.softwiz.osa.user.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationFacade authenticationFacade;

    public User registerUser(UserRegistrationRequest registrationRequest) {

        //E-mail pattern matching
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPatternReg = Pattern.compile(emailPattern);
        Matcher emailMatcherReg = emailPatternReg.matcher(registrationRequest.getEmail());
        if (!emailMatcherReg.matches()) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        // Check if the email is already used
        if (userRepository.findByEmail(registrationRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.findByUsername(registrationRequest.getUsername()) != null){
            throw new IllegalArgumentException("Username already in use");
        }
        if(userRepository.findByMobileNumber(registrationRequest.getMobileNumber()) != null){
            throw new IllegalArgumentException("Mobile number already in use");
        }

        //Password patter matching
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        Pattern passwordPatternReg = Pattern.compile(passwordPattern);
        Matcher passwordMatcher = passwordPatternReg.matcher(registrationRequest.getPassword());

        if (!passwordMatcher.matches()) {
            // The provided password does not match the pattern
            throw new IllegalArgumentException("Invalid password format." + "\n"+
                    "Password must be at least 8 characters long and "+ "\n" +
                    "contain at least one uppercase letter, " +"\n" +
                    "one lowercase letter, " +"\n" +
                    "one digit, and "+"\n" +
                    "one special character (@#$%^&+=!).");
        }
        //Create a new user
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setMobileNumber(registrationRequest.getMobileNumber());
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        //Save the user to repository
        userRepository.save(user);
        return user;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElse(null);
    }

    public boolean deleteUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return true;
        } else {
            throw new NoSuchElementException("User with ID " + userId + " not found");
        }
    }
    public void updateProfile(UserProfileUpdateRequest updateRequest) {
        // Get the current user from the authentication facade
        System.out.println("authenticationFacade.getCurrentUser() is "+ authenticationFacade.getCurrentUser());
        UserDetails currentUser = authenticationFacade.getCurrentUser();
        String username = currentUser.getUsername(); //or another unique identifier
        User user = userRepository.findByUsername(username);
        System.out.println("userRepository.findByUsername(username) ->"+userRepository.findByUsername(username));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        if (userRepository.findByEmail(updateRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.findByUsername(updateRequest.getUsername()) != null){
            throw new IllegalArgumentException("Username already in use");
        }
        if(userRepository.findByMobileNumber(updateRequest.getMobileNumber()) != null){
            throw new IllegalArgumentException("Mobile number already in use");
        }
        // Update the user's profile with the new information
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setMobileNumber(updateRequest.getMobileNumber());
        user.setUsername(updateRequest.getUsername());
        user.setEmail(updateRequest.getEmail());
        user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        // Save the updated user back to the database
        userRepository.save(user);
    }
}