package com.prj.m8eat.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.prj.m8eat.model.dto.User;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole(); // 예: "user"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }



    @Override
    public String getPassword() {
        return null; // 우리는 비밀번호로 인증하지 않음
    }

    @Override
    public String getUsername() {
        return user.getId(); // 고유 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
