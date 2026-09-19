package com.iwish.model;

public class WishListItem {

    private int id;
    private int wishlistId;
    private int itemId;
    private String itemName;
    private String description;
    private double targetPrice;
    private double collectedAmount;
    private String status;

    public WishListItem() {
    }

    public WishListItem(
            int id,
            int wishlistId,
            int itemId,
            String itemName,
            String description,
            double targetPrice,
            double collectedAmount,
            String status
    ) {
        this.id = id;
        this.wishlistId = wishlistId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.targetPrice = targetPrice;
        this.collectedAmount = collectedAmount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return itemName + " - " + targetPrice;
    }
}