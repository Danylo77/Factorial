package com.example.mainserver;

import com.example.mainserver.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Main implements CommandLineRunner{
    private final UserRepository userRepository;

    public Main(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Отримайте всіх користувачів і виведіть їх у консоль
        //userRepository.findAll().forEach(user -> System.out.println(user.getName() + " "+ user.getPassword() + " "+ user.getRoles()));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            System.out.println(auth.getName() + " " + "TRUE");
        }
        else{
            System.out.println(auth);
        }

    }
}
