package in.springbootreglog.services;

import in.springbootreglog.entities.User;

public interface UserService {

    // Save / Register a new user
    User saveUser(User user);

    // Validate user credentials for login
    User validateUser(String email, String password);
}