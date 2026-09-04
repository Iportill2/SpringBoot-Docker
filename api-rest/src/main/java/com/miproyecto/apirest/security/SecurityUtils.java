package com.miproyecto.apirest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.miproyecto.apirest.model.Users;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Users currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Users)) {
            return null;
        }
        return (Users) auth.getPrincipal();
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public static boolean isAdminOrSelf(Integer userId) {
        Users current = currentUser();
        if (current == null) {
            return false;
        }
        return isAdmin() || current.getId().equals(userId);
    }
}
