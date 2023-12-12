package com.softwiz.osa.user.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationFacade {
    UserDetails getCurrentUser();
}
