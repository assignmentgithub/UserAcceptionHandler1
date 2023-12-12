package com.softwiz.osa.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    private String email;
    private String password;


}
