package in.secureauthportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.secureauthportal.entities.Role;
import in.secureauthportal.entities.User;
import in.secureauthportal.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        User saved = userRepository.save(user);

        String verificationLink = "http://localhost:8080/verify?token=" + saved.getVerificationToken();
        emailService.sendVerificationEmail(saved.getEmail(), verificationLink);

        return saved;
    }

    // Login is now handled entirely by Spring Security's AuthenticationManager
    // (via CustomUserDetailsService + DaoAuthenticationProvider), so this
    // manual check is no longer used by the login flow. Kept only if you
    // need a programmatic credential check elsewhere.
    @Override
    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserRole(int userId, Role newRole) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Override
    public User verifyUserByToken(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) {
            return null;
        }
        user.setEnabled(true);
        return userRepository.save(user);
    }

    // 30 minutes, matching the JWT expiry convention used elsewhere in the app
    private static final long RESET_TOKEN_VALID_MS = 30 * 60 * 1000;

    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Deliberately do nothing visible here. If we returned an error
            // for "email not found", an attacker could use this endpoint to
            // check which emails are registered. The controller always shows
            // the same generic message regardless of whether this matched.
            return;
        }

        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(System.currentTimeMillis() + RESET_TOKEN_VALID_MS);
        userRepository.save(user);

        String resetLink = "http://localhost:8080/resetPassword?token=" + user.getResetToken();
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    public boolean isResetTokenValid(String token) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry() == null) {
            return false;
        }
        return System.currentTimeMillis() < user.getResetTokenExpiry();
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry() == null) {
            return false;
        }
        if (System.currentTimeMillis() >= user.getResetTokenExpiry()) {
            return false; // expired
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        // One-time use: clear the token so this link can't be reused.
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }
}