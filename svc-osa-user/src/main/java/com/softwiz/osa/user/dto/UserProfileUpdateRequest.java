package com.softwiz.osa.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private long mobileNumber;
    private String username;
    private String email;
    private String password;

    public UserProfileUpdateRequest() {
    }
}
