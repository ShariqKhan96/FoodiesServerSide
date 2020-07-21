package com.example.hp.foodiesserverside.model;

import java.util.Map;

/**
 * Created by hp on 3/7/2018.
 */

public class Sender {
    public String to;
    public Notification notification;
    public Map<String,String> data;

    public Sender() {
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Sender(String to, Notification notification, Map<String, String> data) {
        this.to = to;
        this.notification = notification;
        this.data = data;
    }
}
