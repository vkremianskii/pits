package com.github.vkremianskii.pits.auth.infra;

import com.github.vkremianskii.pits.auth.model.UserId;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class InternalAuthentication extends AbstractAuthenticationToken {

    private final UserId userId;

    public InternalAuthentication(UserId userId,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = requireNonNull(userId);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return "[ERASED]";
    }
}
