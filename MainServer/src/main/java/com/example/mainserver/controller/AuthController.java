package com.example.mainserver.controller;

import com.example.mainserver.entity.Calculation;
import com.example.mainserver.repository.CalculationRepository;
import com.example.mainserver.repository.UserRepository;
import jakarta.validation.Valid;
import com.example.mainserver.dto.UserDto;
import com.example.mainserver.entity.User;
import com.example.mainserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    @Autowired
    private CalculationRepository calculationRepository;
    @Autowired
    private UserRepository userRepository;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // handler method to handle home page request
    @GetMapping("/index")
    public String index(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String currentEmail = userDetails.getUsername(); // Отримуємо email користувача
            User currentUser = userRepository.findByEmail(currentEmail); // Використовуємо findByEmail
            Long currentUserId = currentUser.getId(); // Отримуємо ID користувача
            List<Calculation> operations = calculationRepository.findByUser_IdOrderByNumberAsc(currentUserId);
            model.addAttribute("operations", operations);
        }
        return "index";
    }

    // handler method to handle login request
    @GetMapping("/login")
    public String login(){
        return "login";
    }


    // handler method to handle user registration form request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        // create model object to store form data
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model){
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }

    // handler method to handle list of users

    @GetMapping("/users")
    public String users(Model model){
        List<com.example.mainserver.dto.UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }



}