package com.nurda.choco_bitcoin.api;

import com.nurda.choco_bitcoin.mvp.model.Transaction;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("api/transactions/")
    Call<List<Transaction>> getTransactions();

    @GET("currentprice/{code}.json")
    Call<Object> currentPrice(@Path(value = "code") String code);
}
