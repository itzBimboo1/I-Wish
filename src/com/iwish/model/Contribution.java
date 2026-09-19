package com.iwish.model;

public class Contribution {

    private int id;
    private int wishlistItemId;
    private int buyerId;
    private double amount;

    public Contribution() {
    }

    public Contribution(
            int id,
            int wishlistItemId,
            int buyerId,
            double amount
    ) {
        this.id = id;
        this.wishlistItemId = wishlistItemId;
        this.buyerId = buyerId;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWishlistItemId() {
        return wishlistItemId;
    }

    public void setWishlistItemId(int wishlistItemId) {
        this.wishlistItemId = wishlistItemId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}