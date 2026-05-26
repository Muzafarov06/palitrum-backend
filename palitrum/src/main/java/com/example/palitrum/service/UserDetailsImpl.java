package com.example.palitrum.service;

import com.example.palitrum.model.Permission;
import com.example.palitrum.model.RolePermission;
import com.example.palitrum.model.User;
import com.example.palitrum.model.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Getter
    private final Long userId;

    public UserDetailsImpl(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            for (UserRole ur : user.getRoles()) {
                if (ur.getRole() == null) continue;
                String roleName = ur.getRole().getName();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                if (ur.getRole().getRolePermissions() != null) {
                    for (RolePermission rp : ur.getRole().getRolePermissions()) {
                        Permission perm = rp.getPermission();
                        if (perm != null && perm.getCode() != null) {
                            authorities.add(new SimpleGrantedAuthority(perm.getCode()));
                        }
                    }
                }
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}