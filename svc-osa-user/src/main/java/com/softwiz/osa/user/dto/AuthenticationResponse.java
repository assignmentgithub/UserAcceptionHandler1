package com.softwiz.osa.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;

    public void setToken(String token) {
        this.token = token;
    }
}
