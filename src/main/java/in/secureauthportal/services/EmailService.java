package in.secureauthportal.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stand-in for a real email service. Right now this just logs the
 * verification link to the console instead of sending an actual email --
 * useful for development without needing SMTP credentials set up yet.
 *
 * When ready to send real emails (e.g. via Gmail SMTP), only this class
 * needs to change -- everything that calls it (UserServiceImpl) stays
 * exactly the same, since the method signature won't change.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendVerificationEmail(String toEmail, String verificationLink) {
        // TODO: replace this with real SMTP sending (e.g. JavaMailSender)
        // once Gmail App Password / SMTP credentials are set up.
        logger.info("=================================================");
        logger.info("VERIFICATION EMAIL (console stand-in)");
        logger.info("To: {}", toEmail);
        logger.info("Click to verify: {}", verificationLink);
        logger.info("=================================================");
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        // Same console stand-in pattern as sendVerificationEmail above.
        logger.info("=================================================");
        logger.info("PASSWORD RESET EMAIL (console stand-in)");
        logger.info("To: {}", toEmail);
        logger.info("Click to reset your password: {}", resetLink);
        logger.info("This link expires in 30 minutes.");
        logger.info("=================================================");
    }
}
