package com.iwish.model;

public class Item {

    private int id;
    private String name;
    private String description;
    private double defaultPrice;

    public Item() {
    }

    public Item(int id, String name, String description, double defaultPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultPrice = defaultPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    @Override
    public String toString() {
        return name + " - " + defaultPrice;
    }
}