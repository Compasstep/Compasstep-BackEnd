package com.fifo.compasstep.security.userDetails;

import com.fifo.compasstep.admin.domain.Admin;
import com.fifo.compasstep.admin.enums.Role;
import com.fifo.compasstep.user.domain.User;
import com.fifo.compasstep.user.enums.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "user")
public class UserUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("STATUS_" + user.getStatus()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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

    // @AuthenticationPrincipal에서 사용할 getter 메서드들
    public String getEmail() {
        return user.getEmail();
    }

    public Status getStatus() {
        return user.getStatus();
    }

    public User getUser() {
        return user;
    }
}
