package com.example.mainserver;

public class Port {
    private String port;
    private String info;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Port(String port, String info) {
        this.port = port;
        this.info = info;
    }
}
