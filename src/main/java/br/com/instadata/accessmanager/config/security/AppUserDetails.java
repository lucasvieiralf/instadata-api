package br.com.instadata.accessmanager.config.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.instadata.accessmanager.entity.User;
import br.com.instadata.accessmanager.enums.UserStatus;

public class AppUserDetails implements UserDetails {

    private final User userEntity;
    private final Set<GrantedAuthority> authorities;

    public AppUserDetails(User userEntity, Set<GrantedAuthority> authorities) {
        this.userEntity = userEntity;
        this.authorities = authorities;
    }

    public User getUserEntity() {
        return userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Não implementado
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getStatus() != UserStatus.INACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Não implementado
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getStatus() == UserStatus.ACTIVE;
    }
}