package com.example.mainserver.controller;

import com.example.mainserver.LoadBalancer;
import com.example.mainserver.entity.Calculation;
import com.example.mainserver.entity.User;
import com.example.mainserver.repository.CalculationRepository;
import com.example.mainserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FactorialController {
    private Long idOper = Long.valueOf(100);


    @Autowired
    private CalculationRepository calculationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    private volatile boolean cancelFlag = false;
    @Autowired
    private AsyncTaskExecutor taskExecutor;
    @PostMapping("/cancel-calculation")
    public String cancelCalculation() {
        cancelFlag = true; // Встановлюємо флаг скасування
        return "index";
    }

    @GetMapping("/active-threads")
    public int getActiveThreads() {
        if (taskExecutor instanceof ThreadPoolTaskExecutor) {
            int activeThreads = ((ThreadPoolTaskExecutor) taskExecutor).getActiveCount();

            // Надіслати інформацію на головний порт
            restTemplate.getForObject("http://localhost:8081/active-threads?activeThreads=" + activeThreads, String.class);

            return activeThreads;
        }
        return -1;
    }

    private LoadBalancer loadBalancer = new LoadBalancer();

    @PostMapping("/calculate")
    public String calculateFactorial(@RequestParam("number") int number, Model model) {
        if (number < 0 || number > 200000) {
            throw new IllegalArgumentException("Input must be a non-negative integer and less then 200000");
        }

        //BigInteger factorialResult = restTemplate.postForObject(factorialServiceUrl, null, BigInteger.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String currentEmail = userDetails.getUsername(); // Отримуємо email користувача
            User currentUser = userRepository.findByEmail(currentEmail); // Використовуємо findByEmail
            Long currentUserId = currentUser.getId(); // Отримуємо ID користувача
//
            if (loadBalancer.isQueueFull()){
                model.addAttribute("number", "Дуже багато запитів, спробуйте пізніше");
                return "index";
            }
            System.out.println(loadBalancer.getAllInfo());
            loadBalancer.addNumberToQueue(number);
            String freePort = loadBalancer.getFreePort();
            System.out.println(freePort);
            idOper += 1;


  //
            //model.addAttribute("number", number);
           // model.addAttribute("result", factorial);
            Calculation calculation = new Calculation();
            calculation.setIdResult(idOper);
            calculation.setNumber(loadBalancer.getNumberFromQueue());

            calculation.setResult("0/"+number);
            calculation.setTime(LocalDateTime.now());
            calculation.setPort(Integer.valueOf(freePort));
            String factorialServiceUrl = "http://localhost:" + freePort + "/calculate?number=" + number+ "&idNumber=" + idOper;
            String factorial = restTemplate.postForObject(factorialServiceUrl, null, String.class);

            calculation.setUser(userRepository.findById(currentUserId).orElse(null));
            calculationRepository.save(calculation);
            List<Calculation> operations = calculationRepository.findByUser_IdOrderByTimeDesc(currentUserId);
            model.addAttribute("operations", operations);
        }


        return "index"; // Назва вашої сторінки
    }

    @PostMapping("/refreshHistory")
    public String refreshHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String currentEmail = userDetails.getUsername(); // Отримуємо email користувача
            User currentUser = userRepository.findByEmail(currentEmail); // Використовуємо findByEmail
            Long currentUserId = currentUser.getId();

            List<Calculation> operations = calculationRepository.findByUser_IdOrderByTimeDesc(currentUserId);
            for (Calculation op: operations) {
                if(op.getResult().contains("/") || op.getResult().contains("Іде обчислення")){
                    String resultServiceUrl = "http://localhost:" + op.getPort() + "/refreshResult?idResult=" + op.getIdResult();
                    String newResult = restTemplate.postForObject(resultServiceUrl, null, String.class);
                    op.setResult(newResult);
                    calculationRepository.save(op);

                }
            }

            model.addAttribute("operations", operations);
        }
        return "index";
    }

}
