package in.secureauthportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Holds the core Security beans (password hashing + authentication manager),
 * kept separate from SecurityConfig so that file only deals with the
 * HTTP filter chain / URL authorization rules.
 */
@Configuration
public class SecurityBeans {

    // BCrypt for hashing/verifying passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Wires our UserDetailsService + PasswordEncoder into authentication.
    // Spring injects the CustomUserDetailsService bean automatically as the
    // UserDetailsService parameter -- no manual @Autowired field needed.
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        // Current Spring Security API: UserDetailsService is passed into the
        // constructor (the old no-arg constructor + setUserDetailsService()
        // setter pattern is removed in recent versions).
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}
