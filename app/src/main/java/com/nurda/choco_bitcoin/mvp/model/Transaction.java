package com.nurda.choco_bitcoin.mvp.model;

public class Transaction {
    private String date;
    private int tid;
    private String price;
    private String amount;
    private int type;

    public Transaction(String date, int tid, String price, String amount, int type) {
        this.date = date;
        this.tid = tid;
        this.price = price;
        this.amount = amount;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public int getTid() {
        return tid;
    }

    public String getPrice() {
        return price;
    }

    public String getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }
}
