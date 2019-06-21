package com.nurda.choco_bitcoin.mvp.model;

public class GraphResponse {
    private String date;
    private double price;

    public GraphResponse(String date, double price) {
        this.date = date;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}
