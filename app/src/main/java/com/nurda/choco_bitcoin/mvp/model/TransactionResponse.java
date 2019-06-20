package com.nurda.choco_bitcoin.mvp.model;

import java.util.ArrayList;

public class TransactionResponse {
    private ArrayList<Transaction> transactions;

    public TransactionResponse(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
