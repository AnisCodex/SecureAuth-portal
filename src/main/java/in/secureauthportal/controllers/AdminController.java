package in.secureauthportal.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.secureauthportal.entities.Role;
import in.secureauthportal.entities.User;
import in.secureauthportal.services.UserService;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    // Belt-and-suspenders: SecurityConfig already restricts this URL to
    // ROLE_ADMIN at the filter-chain level. @PreAuthorize re-asserts the same
    // rule at the method level, so this stays protected even if the URL
    // mapping changes later.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/adminPage")
    public String adminPage(Authentication authentication, Model model) {
        // Extra restriction (Stage 4): even an ADMIN can't use the dashboard
        // until their own email is verified.
        User currentAdmin = userService.findByEmail(authentication.getName());
        if (currentAdmin == null || !currentAdmin.isEnabled()) {
            return "redirect:/profilePage?verifyRequired=true";
        }

        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", Role.values());
        return "admin";
    }

    // Promote or demote a user by changing their role.
    // Re-renders the same dashboard with the updated list.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/role")
    public String updateUserRole(@PathVariable("id") int id,
                                  @RequestParam("role") Role role,
                                  Authentication authentication,
                                  Model model) {
        User currentAdmin = userService.findByEmail(authentication.getName());
        if (currentAdmin == null || !currentAdmin.isEnabled()) {
            return "redirect:/profilePage?verifyRequired=true";
        }

        userService.updateUserRole(id, role);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", Role.values());
        return "admin";
    }
}
