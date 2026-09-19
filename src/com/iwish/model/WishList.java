package com.iwish.model;

public class WishList {

    private int id;
    private int userId;

    public WishList() {
    }

    public WishList(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public WishList(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}