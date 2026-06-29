package in.secureauthportal.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    // Whether this user has clicked their email verification link.
    // Defaults to false -- every new registration starts unverified.
    @Column(nullable = false)
    private boolean enabled = false;

    // Random token emailed to the user; matched against on /verify.
    // Nullable because once verified we don't strictly need to keep it,
    // though we leave it in place for simplicity (it's just inert after use).
    @Column(name = "verification_token")
    private String verificationToken;

    // One-time password-reset token, set when the user requests a reset and
    // cleared as soon as it's used (or replaced if they request again).
    @Column(name = "reset_token")
    private String resetToken;

    // When the reset token stops being valid. Stored as epoch millis for
    // simplicity rather than a full LocalDateTime/timestamp column.
    @Column(name = "reset_token_expiry")
    private Long resetTokenExpiry;

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public Long getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(Long resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }
}