package com.softwiz.osa.user.security.Impl;

import com.softwiz.osa.user.security.AuthenticationFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    @Override
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // or throw an exception, depending on your needs
        }
        return (UserDetails) authentication.getPrincipal();
    }
}
