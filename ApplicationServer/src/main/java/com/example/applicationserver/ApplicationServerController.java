package com.example.applicationserver;

import com.example.applicationserver.AsyncConfig;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.core.task.AsyncTaskExecutor;


@RestController
public class ApplicationServerController {
    private boolean isCanseled = false;
    Map<Long, String> myMap = new HashMap<>();
    //Map<Integer, Long> timeMap = new HashMap<>();

    AsyncConfig executor = new AsyncConfig();

    @Autowired
    @Qualifier("factorialExecutor")
    private AsyncTaskExecutor taskExecutor;

    @PostMapping("/calculate")
    @Async("factorialExecutor")
    public CompletableFuture<String> calculateFactorial(@RequestParam int number, @RequestParam Long idNumber) {
        if (number < 0 || number > 200000) {
            throw new IllegalArgumentException("Input must be a non-negative integer and less then 200000");
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " for return " + number);
        CompletableFuture.runAsync(() -> {
            System.out.println("Thread: " + Thread.currentThread().getName() + " started processing " + number);

                    BigInteger result = BigInteger.ONE;
                    for (int i = 1; i <= number; i++) {
                        if (isCanseled) break;
                        result = result.multiply(BigInteger.valueOf(i));
                        if (number >= 40000 && i % 1000 == 0){

                            myMap.put(idNumber, i + "/" +number);

                        }
                    }
                    isCanseled = false;

            synchronized (myMap) {

                myMap.put(idNumber, result.toString());
            }
            System.out.println("Thread " + Thread.currentThread().getName() + " finished processing " + number);

            }, taskExecutor);


        return CompletableFuture.completedFuture("Запит прийнято");

    }


    @PostMapping("/refreshResult")
    public String refreshHistory(@RequestParam Long idResult){
        Set<Long> keys = myMap.keySet();

        for (Long key : keys) {
            System.out.println("Ключ: " + key);
        }
        if (myMap.containsKey(idResult)){
            String result = myMap.get(idResult).toString();
            //myMap.remove(idResult);
            return (result);
        }
        return "Іде обчислення";
    }

    @PostMapping("/cancelCalculate")
    public String cancelCalculate(@RequestParam Long idCancel){
        Set<Long> keys = myMap.keySet();

        if (myMap.containsKey(idCancel)){
//            String result = "myMap.get(idResult).toString();"
            //myMap.remove(idResult);
            isCanseled = true;
            return ("Скасовано");
        }
        return "Іде обчислення";
    }
}
