package com.wust.ucms.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {

    private LoginInfo user;

    String permission;

    public LoginUser(LoginInfo user, String permission) {
        this.user = user;
        this.permission = permission;
    }

    @JSONField(serialize = false)
    SimpleGrantedAuthority authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (authorities != null) return Collections.singleton(authorities);

        authorities = new SimpleGrantedAuthority(permission);

        return Collections.singleton(authorities);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId().toString();
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
