package com.example.hp.foodiesserverside.model;

/**
 * Created by hp on 2/23/2018.
 */

public class foods {

    public String Name;
    public String Image;
    public String Description;
    public String Price;
    public String Discount;
    public String MenuId;
    public String food_id;

    public foods() {
    }

    public foods(String name, String image, String description, String price, String discount, String menuId, String food_id) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        this.food_id = food_id;
    }

    //
//    public String getMenuId() {
//        return MenuId;
//    }
//
//    public void setMenuId(String menuId) {
//        MenuId = menuId;
//    }
//
//    public String getName() {
//        return Name;
//    }
//
//    public void setName(String name) {
//        Name = name;
//    }
//
//    public String getImage() {
//        return Image;
//    }
//
//    public void setImage(String image) {
//        Image = image;
//    }
//
//    public String getDescription() {
//        return Description;
//    }
//
//    public void setDescription(String description) {
//        Description = description;
//    }
//
//    public String getPrice() {
//        return Price;
//    }
//
//    public void setPrice(String price) {
//        Price = price;
//    }
//
//    public String getDiscount() {
//        return Discount;
//    }
//
//    public void setDiscount(String discount) {
//        Discount = discount;
//    }

}

