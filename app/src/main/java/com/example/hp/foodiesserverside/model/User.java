package com.example.hp.foodiesserverside.model;

public class User {
    public String isStaff, Password, name, restaurantId, secureCode, homeAddress, balance;

    public User() {
    }

    public User(String isStaff, String password, String name, String restaurantId, String secureCode, String homeAddress, String balance) {
        this.isStaff = isStaff;
        this.Password = password;
        this.name = name;
        this.restaurantId = restaurantId;
        this.secureCode = secureCode;
        this.homeAddress = homeAddress;
        this.balance = balance;
    }


}
