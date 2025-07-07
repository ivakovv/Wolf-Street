package com.wolfstreet.security_lib.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class JwtDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final Collection<SimpleGrantedAuthority> authorities;

    @Override
    public String getPassword() {
        return "";
    }
}
