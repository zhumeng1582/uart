package com.industio.uart.bean;


import java.io.Serializable;

public class AccessPara implements Serializable {
    public AccessPara(String name, String port, String gpio) {
        this.name = name;
        this.port = port;
        this.gpio = gpio;
    }

    String name;
    String port;
    String gpio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getGpio() {
        return gpio;
    }

    public void setGpio(String gpio) {
        this.gpio = gpio;
    }
}
