package samples.springboot2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;
import samples.springboot2.models.User;
import samples.springboot2.services.JWTAuthenticationService;
import samples.springboot2.services.UserService;
import samples.springboot2.services.JWTService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@EnableAutoConfiguration
@Configuration
public class ApplicationConfiguration {
    @Bean
    public View jsonView() {
        return new MappingJackson2JsonView();
    }

    @Bean
    public View xmlView() {
        final MappingJackson2XmlView view = new MappingJackson2XmlView();
        return view;
    }

    @Bean
    public ContentNegotiatingViewResolver contentViewResolver(ContentNegotiationManager manager) {
        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setDefaultViews(Arrays.asList(new View[]{xmlView(), jsonView()}));
        resolver.setContentNegotiationManager(manager);
        resolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return resolver;
    }

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public class SecurityConfig {
        private UserService service;
        private PasswordEncoder passwordEncoder;

        @Autowired
        public SecurityConfig(UserService service, PasswordEncoder passwordEncoder) {
            this.service = service;
            this.passwordEncoder = passwordEncoder;
        }
    }

    @Configuration
    @Order(1)
    public class JWTConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/token")
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .httpBasic()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @Autowired
        UserService userService;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
        }

        @Autowired JWTService tokenService;
        @RestController
        class AuthorizationRESTController {
            @RequestMapping("/api/token")
            String getToken(@AuthenticationPrincipal User principal) {
                return tokenService.encode(principal);
            }
        }
    }

    @Configuration
    @Order(2)
    public class TokenAuthConfig extends WebSecurityConfigurerAdapter {
        private JWTAuthenticationService service;

        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Autowired
        public TokenAuthConfig(JWTAuthenticationService service) {
            this.service = service;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/user")
                    .authorizeRequests()
                    .mvcMatchers(HttpMethod.POST, "/api/user").anonymous()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(authFilter(), RequestHeaderAuthenticationFilter.class)
                    .authenticationProvider(preAuthProvider())
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf().disable();
        }

        @RestController
        class UserRestController {
            @RequestMapping( value = "/api/user",
                    method = RequestMethod.GET,
                    consumes = {MediaType.ALL_VALUE},
                    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
            @ResponseBody User getUser(@AuthenticationPrincipal User user) {
                return user;
            }
        }

        @Bean
        public AuthenticationTokenFilter authFilter() {
            return new AuthenticationTokenFilter();
        }

        @Bean
        public AuthenticationProvider preAuthProvider() {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(service);
            return provider;
        }
    }

    class AuthenticationTokenFilter extends AbstractPreAuthenticatedProcessingFilter {
        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            logger.debug("Retrieving principal from token");
            return request.getHeader("Authorization").split("JWT ")[1];
        }

        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            return request.getHeader("Authorization").split("JWT ")[1];
        }

        @Override
        @Autowired
        public void setAuthenticationManager(AuthenticationManager authenticationManager) {
            super.setAuthenticationManager(authenticationManager);
        }
    }

}
