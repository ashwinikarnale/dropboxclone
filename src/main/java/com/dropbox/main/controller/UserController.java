package com.dropbox.main.controller;

import com.dropbox.main.config.web.UserRegistrationDto;
import com.dropbox.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String userSignUp(@ModelAttribute("user") UserRegistrationDto userRegistrationDto) {
        userService.save(userRegistrationDto);
        return "redirect:/signup?success";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}