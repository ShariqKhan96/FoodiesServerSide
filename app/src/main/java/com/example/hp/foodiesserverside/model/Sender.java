package com.example.hp.foodiesserverside.model;

/**
 * Created by hp on 3/7/2018.
 */

public class Sender {
    public String to;
    public Notification notification;

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

    public Sender(String to, Notification notification) {

        this.to = to;
        this.notification = notification;
    }
}
