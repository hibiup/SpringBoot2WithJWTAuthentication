package samples.springboot2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import samples.springboot2.models.User;


@Service
public class UserService implements UserDetailsService {
    @Autowired PasswordEncoder passwordEncoder;
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new User("mockuser", passwordEncoder.encode("mockpass")) ;
    }
}
