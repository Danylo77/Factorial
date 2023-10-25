package com.example.mainserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.min;

public class LoadBalancer {
    private final String[] ports = {"8081"};

    public String[] getPorts(){
        return ports;
    }
    private Queue<Integer> queue = new LinkedList<>();

    public void addNumberToQueue(int number){
        if (!isQueueFull()){
            queue.offer(number);
        }
    }



    public int getNumberFromQueue(){
        if (!queue.isEmpty()){
            return queue.poll();
        }
        return 0;
    }

    public boolean isQueueFull(){
        System.out.println(queue);
        if (queue.size() <= 10){
            return false;
        }
        return true;
    }
    public String getAllInfo(){
        String allInfo = "";
        for(int i = 0; i < ports.length; i++){
            allInfo += ports[i] + ":"+getInfo(ports[i]);

        }

        return allInfo;
    }
    public String getInfo(String port) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:"+Integer.valueOf(port)+"/active-threads", String.class);
        String responseBody = responseEntity.getBody();
        System.out.println(responseBody);
        return responseBody;
    }

    public boolean isThreadFull(String port){
        if (getInfo(port) == "3") return true;
        return false;
    }
    public boolean isQueueEmpty(){
        return queue.isEmpty();
    }
    public Boolean isFull(){
        for (String port: ports) {
            if (!isThreadFull(port)) return false;
        }
        return true;
    }

    public String getFreePort(){
        String freePort = "Wait";
        if(isFull()){
            return freePort;
        }
        freePort = ports[findMinIndex()].toString();
        return freePort;
    }
    public int findMinIndex() {
        if (ports == null || ports.length == 0) {
            throw new IllegalArgumentException("Масив порожній або не існує");
        }

        int minIndex = 0;
        int minValue = Integer.parseInt(getInfo(ports[0]));

        for (int i = 1; i < ports.length; i++) {
            int current = Integer.parseInt(getInfo(ports[i]));
            if (current < minValue) {
                minValue = current;
                minIndex = i;
            }
        }

        return minIndex;
    }
}
