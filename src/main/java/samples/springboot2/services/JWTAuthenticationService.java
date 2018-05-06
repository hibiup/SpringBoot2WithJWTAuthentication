package samples.springboot2.services;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import samples.springboot2.models.User;

@Service
public class JWTAuthenticationService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    @Autowired JWTService tokenService;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken) throws UsernameNotFoundException {
        if (preAuthenticatedAuthenticationToken.getPrincipal() != null
                && preAuthenticatedAuthenticationToken.getPrincipal() instanceof String
                && preAuthenticatedAuthenticationToken.getCredentials() instanceof String) {
            DecodedJWT token;
            try {
                token = tokenService.decode((String) preAuthenticatedAuthenticationToken.getPrincipal());
            } catch (InvalidClaimException ex) {
                throw new UsernameNotFoundException("Token has been expired", ex);
            }
            return new User(
                    token.getClaim("user").asString(),
                    (String) preAuthenticatedAuthenticationToken.getCredentials(),
                    token.getClaim("role").asList(String.class)
                    );
        } else {
            throw new UsernameNotFoundException("Could not retrieve user details for '" + preAuthenticatedAuthenticationToken.getPrincipal() + "'");
        }
    }
}
