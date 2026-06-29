package in.secureauthportal.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.secureauthportal.entities.User;
import in.secureauthportal.security.JwtAuthFilter;
import in.secureauthportal.security.JwtUtil;
import in.secureauthportal.services.UserService;

@Controller
public class MyController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;


	@GetMapping("/")
	public String home() {
		return "index";
	}


	@GetMapping("/loginPage")
	public String loginPage() {
		return "login";
	}


	@GetMapping("/registerPage")
	public String registerPage(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}


	@PostMapping("/register")
	public String register(@ModelAttribute("user") User user, Model model) {
		try {
			userService.saveUser(user);
			model.addAttribute("successMsg", "Registration successful! Please login.");
			return "login";
		} catch (Exception e) {
			model.addAttribute("errorMsg", "Email already exists.");
			return "register";
		}
	}


	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password,
			HttpServletResponse response, Model model) {

		try {
			// Delegates to CustomUserDetailsService + the PasswordEncoder
			// (wired in SecurityBeans) -- throws if credentials are bad.
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(email, password)
			);
		} catch (BadCredentialsException e) {
			model.addAttribute("errorMsg", "Invalid email or password");
			return "login";
		}

		User user = userService.findByEmail(email);
		String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

		Cookie jwtCookie = new Cookie(JwtAuthFilter.JWT_COOKIE_NAME, token);
		jwtCookie.setHttpOnly(true);   // not readable by JS -- mitigates XSS token theft
		jwtCookie.setSecure(false);    // set true once you're serving over HTTPS
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(30 * 60);  // 30 min, matches jwt.expiration-ms
		jwtCookie.setAttribute("SameSite", "Strict"); // mitigates CSRF via cross-site cookie sending
		response.addCookie(jwtCookie);

		return "redirect:/profilePage";
	}


	@GetMapping("/profilePage")
	public String profile(Authentication authentication, Model model) {
		// authentication.getName() is the email -- set by JwtAuthFilter
		// after validating the JWT cookie on this request.
		String email = authentication.getName();
		User user = userService.findByEmail(email);

		model.addAttribute("modelName", user.getName());
		model.addAttribute("isVerified", user.isEnabled());
		return "profile";
	}


	// Public endpoint -- clicking the link from the verification email
	// (or console log, for now) hits this, marking the account as verified.
	@GetMapping("/verify")
	public String verify(@RequestParam String token, Model model) {
		User user = userService.verifyUserByToken(token);

		if (user == null) {
			model.addAttribute("verifyMsg", "Invalid or expired verification link.");
			model.addAttribute("verifySuccess", false);
		} else {
			model.addAttribute("verifyMsg", "Your email has been verified! You can now log in.");
			model.addAttribute("verifySuccess", true);
		}
		return "verify-result";
	}


	@GetMapping("/forgotPassword")
	public String forgotPasswordPage() {
		return "forgot-password";
	}


	@PostMapping("/forgotPassword")
	public String forgotPasswordSubmit(@RequestParam String email, Model model) {
		userService.requestPasswordReset(email);
		// Always show the same message whether or not the email exists --
		// this prevents the form from being used to discover which emails
		// are registered.
		model.addAttribute("infoMsg",
				"If an account exists for that email, a password reset link has been sent. " +
				"Check the application console for now.");
		return "forgot-password";
	}


	@GetMapping("/resetPassword")
	public String resetPasswordPage(@RequestParam String token, Model model) {
		if (!userService.isResetTokenValid(token)) {
			model.addAttribute("invalidToken", true);
			return "reset-password";
		}
		model.addAttribute("token", token);
		return "reset-password";
	}


	@PostMapping("/resetPassword")
	public String resetPasswordSubmit(@RequestParam String token,
			@RequestParam String newPassword, Model model) {

		boolean success = userService.resetPassword(token, newPassword);

		if (!success) {
			model.addAttribute("invalidToken", true);
			return "reset-password";
		}

		model.addAttribute("resetSuccess", true);
		return "reset-password";
	}


	@PostMapping("/logoutManual")
	public String logoutManual(HttpServletResponse response) {
		Cookie jwtCookie = new Cookie(JwtAuthFilter.JWT_COOKIE_NAME, null);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(0); // expires immediately
		response.addCookie(jwtCookie);
		return "redirect:/loginPage";
	}

	// NOTE: GET /logout is also handled by Spring Security's logout filter
	// (see SecurityConfig), which deletes the same cookie. Either route works;
	// logoutManual exists as a manual fallback / for testing.
}