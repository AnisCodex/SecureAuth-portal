package in.secureauthportal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on controller/service methods (used in AdminController)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Our JWT cookie is HttpOnly + SameSite=Strict (set in MyController),
            // which already blocks cross-site browser submission in modern
            // browsers. We still keep CSRF protection enabled for safety, and
            // only exempt /login and /register since those happen before a
            // JWT cookie exists yet (i.e. there's no session/state to forge).
            .csrf(csrf -> csrf.ignoringRequestMatchers("/login", "/register", "/forgotPassword", "/resetPassword"))
            .authorizeHttpRequests(auth -> auth
                // public pages
                .requestMatchers("/", "/loginPage", "/registerPage", "/register", "/login", "/health", "/verify",
                        "/forgotPassword", "/resetPassword").permitAll()
                // admin-only: requires the ADMIN role from the JWT
                .requestMatchers("/adminPage", "/admin/**").hasRole("ADMIN")
                // anything else (e.g. /profilePage) needs a valid JWT
                .anyRequest().authenticated()
            )
            // no more formLogin() -- /login is now a plain controller endpoint
            // (see MyController) that authenticates manually and issues a JWT
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginPage")
                .deleteCookies(JwtAuthFilter.JWT_COOKIE_NAME)
                .permitAll()
            )
            // No HttpSession needed -- every request is authenticated fresh
            // from the JWT cookie via JwtAuthFilter
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // run our JWT check before Spring's built-in username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
