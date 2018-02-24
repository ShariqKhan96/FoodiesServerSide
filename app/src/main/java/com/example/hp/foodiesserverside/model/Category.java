package com.example.hp.foodiesserverside.model;

/**
 * Created by hp on 2/13/2018.
 */

public class Category {
    private String Image;
    private String Name;
    private String Id;


    public Category() {
    }

    public Category(String image, String name, String id) {
        Image = image;
        Name = name;
        this.Id= id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
