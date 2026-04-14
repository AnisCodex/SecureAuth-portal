package in.springbootreglog.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.springbootreglog.entities.User;
import in.springbootreglog.services.UserService;

@Controller
public class MyController {

	@Autowired
	private UserService userService;

	
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
	public String login(@RequestParam String email, @RequestParam String password, Model model,
			HttpServletRequest request) {

		User validUser = userService.validateUser(email, password);

		if (validUser != null) {
			HttpSession session = request.getSession();
			session.setAttribute("loggedUser", validUser);

			return "redirect:/profilePage";
		}

		model.addAttribute("errorMsg", "Invalid email or password");
		return "login";
	}

	
	@GetMapping("/profilePage")
	public String profile(HttpServletRequest request, Model model) {

		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("loggedUser") == null) {
			return "redirect:/loginPage";
		}

		User user = (User) session.getAttribute("loggedUser");
		model.addAttribute("modelName", user.getName());

		return "profile";
	}


	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/loginPage";
	}
}