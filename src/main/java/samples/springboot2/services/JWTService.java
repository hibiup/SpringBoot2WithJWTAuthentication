package samples.springboot2.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import samples.springboot2.models.User;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JWTService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Integer maxAgeSeconds = 86400;
    private String secret = "must_change";
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Autowired
    public JWTService() throws UnsupportedEncodingException {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).acceptExpiresAt(0).build();
    }

    public String encode(User user) {
        LocalDateTime now = LocalDateTime.now();
        try {
            return JWT.create()
                    .withSubject(user.getUsername())
                    .withIssuedAt(Date
                            .from(now.atZone(ZoneId.systemDefault())
                                    .toInstant()))
                    .withExpiresAt(Date
                            .from(now.plusSeconds(maxAgeSeconds)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()))
                    .withArrayClaim("role", user.getRoles().toArray(new String[user.getRoles().size()]))
                    .withClaim("user", user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException ex) {
            logger.error("Cannot properly create token", ex);
            throw ex;
        }
    }

    public DecodedJWT decode(String token) {
        return this.verifier.verify(token);
    }
}
