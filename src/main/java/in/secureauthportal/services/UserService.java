package in.secureauthportal.services;

import in.secureauthportal.entities.Role;
import in.secureauthportal.entities.User;

import java.util.List;

public interface UserService {

    // Save / Register a new user
    User saveUser(User user);

    // Validate user credentials for login
    User validateUser(String email, String password);

    // Look up a user by email (used to populate profile page after Spring Security auth)
    User findByEmail(String email);

    // List all users (admin-only use case)
    List<User> findAllUsers();

    // Change a user's role (admin-only use case) -- returns the updated user,
    // or null if no user exists with that id
    User updateUserRole(int userId, Role newRole);

    // Marks the user matching this verification token as enabled.
    // Returns the updated user, or null if the token doesn't match anyone.
    User verifyUserByToken(String token);

    // Generates a reset token + expiry for the given email and sends the
    // reset link via EmailService. Silently does nothing if no user has
    // that email (so the endpoint can't be used to enumerate accounts).
    void requestPasswordReset(String email);

    // Returns true if a non-expired user exists for this reset token.
    boolean isResetTokenValid(String token);

    // Sets a new password for the user matching this reset token, then
    // clears the token (one-time use). Returns false if the token was
    // missing/invalid/expired.
    boolean resetPassword(String token, String newPassword);
}