package in.secureauthportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecureAuthPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureAuthPortalApplication.class, args);
		System.out.println("Application start");
	}

}
