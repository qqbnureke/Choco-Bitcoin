package com.nurda.choco_bitcoin.mvp.model;

public class GraphData {
    String date;
    double price;

    public GraphData(String date, double price) {
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
