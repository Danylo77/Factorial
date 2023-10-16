package com.example.applicationserver;

import com.example.applicationserver.AsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class ApplicationServerController {

    @PostMapping("/calculate")
    @Async("factorialExecutor")
    public CompletableFuture<String> calculateFactorial(@RequestParam int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Input must be a non-negative integer");
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " started processing " + number);

        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= number; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        System.out.println("Thread " + Thread.currentThread().getName() + " finished processing " + number);

        return CompletableFuture.completedFuture(result.toString());
    }

}
