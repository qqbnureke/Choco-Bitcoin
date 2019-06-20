package com.nurda.choco_bitcoin.mvp.model;

public class Transaction {
    private String date;
    private String tid;
    private String price;
    private String amount;
    private String type;

    public Transaction(String date, String tid, String price, String amount, String type) {
        this.date = date;
        this.tid = tid;
        this.price = price;
        this.amount = amount;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getTid() {
        return tid;
    }

    public String getPrice() {
        return price;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
