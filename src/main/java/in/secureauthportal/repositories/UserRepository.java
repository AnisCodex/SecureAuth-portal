package in.secureauthportal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.secureauthportal.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByVerificationToken(String verificationToken);

    User findByResetToken(String resetToken);
}