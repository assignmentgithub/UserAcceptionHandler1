package com.softwiz.osa.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationRequest {

    private String firstName;
    private String lastName;
    private long mobileNumber;
    private String username;
    private String email;
    private String password;

    @Override
    public String toString() {
        return "UserRegistrationRequest{" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNumber=" + mobileNumber +
                ", userName='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
