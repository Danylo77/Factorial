package com.example.mainserver.controller;

import com.example.mainserver.entity.Calculation;
import com.example.mainserver.entity.User;
import com.example.mainserver.repository.CalculationRepository;
import com.example.mainserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FactorialController {

    @Autowired
    private CalculationRepository calculationRepository;
    @Autowired
    private UserRepository userRepository;
    private volatile boolean cancelFlag = false;

    @PostMapping("/cancel-calculation")
    public String cancelCalculation() {
        cancelFlag = true; // Встановлюємо флаг скасування
        return "index";
    }

    @PostMapping("/calculate")
    public String calculateFactorial(@RequestParam("number") int number, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String currentEmail = userDetails.getUsername(); // Отримуємо email користувача
            User currentUser = userRepository.findByEmail(currentEmail); // Використовуємо findByEmail
            Long currentUserId = currentUser.getId(); // Отримуємо ID користувача

            BigInteger factorial = calculateFactorial(number);
            model.addAttribute("number", number);
            model.addAttribute("result", factorial);
            Calculation calculation = new Calculation();
            calculation.setNumber(number);
            calculation.setResult(factorial.toString());
            calculation.setTime(LocalDateTime.now());

            calculation.setUser(userRepository.findById(currentUserId).orElse(null));
            calculationRepository.save(calculation);
            List<Calculation> operations = calculationRepository.findByUser_IdOrderByTimeDesc(currentUserId);
            model.addAttribute("operations", operations);
        }


        return "index"; // Назва вашої сторінки
    }

    private BigInteger calculateFactorial(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Input must be a non-negative integer");
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= number; i++) {
            System.out.println(i);
            if (cancelFlag) {
                cancelFlag = false; // Скидаємо флаг скасування
                return BigInteger.valueOf(0); // Повертаємо помилку скасування
            }
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
