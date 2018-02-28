package com.example.hp.foodiesserverside.model;

/**
 * Created by hp on 2/23/2018.
 */

public class Order {
    public String id;
    public String discount;
    public String quantity;
    public String name;
    public String price;

    public Order() {
    }

    public Order(String name, String discount, String price, String quantity) {
    this.name = name;
    this.discount = discount;
    this.price = price;
    this.quantity = quantity;
    }
}
