package com.fifo.compasstep.security.userDetails;



import com.fifo.compasstep.admin.domain.Admin;
import com.fifo.compasstep.admin.enums.Role;
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
@EqualsAndHashCode(of = "admin")
public class AdminUserDetails implements UserDetails {

    private final Admin admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name()));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public String getUsername() {
        return admin.getEmail();
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
        return admin.getEmail();
    }

    public Role getRole() {
        return admin.getRole();
    }

    public Admin getAdmin() {
        return admin;
    }
}