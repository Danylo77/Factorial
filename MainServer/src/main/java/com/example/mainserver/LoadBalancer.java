package com.example.mainserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static java.lang.Math.min;

public class LoadBalancer {
    private final String[] ports = {"8081","8082"};

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

    public Boolean isFull(){
        for(int i = 0; i < ports.length; i++){
            if (ports[i] != "3"){
                return false;
            }
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
