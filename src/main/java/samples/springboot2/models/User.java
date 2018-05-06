package samples.springboot2.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class User implements UserDetails {
    private String username;
    private String password;
    private List<String> authorities = Arrays.asList("ROLE_USER");

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public <R> User(String username, String password, List<String> authorities) {
        this(username, password);
        this.authorities = authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    public List<String> getRoles() {
        return authorities;
    }
}
