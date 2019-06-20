package com.nurda.choco_bitcoin.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String mBaseStampUrl = "https://www.bitstamp.net/";
    private static final String mBaseCoindeskUrl = "https://api.coindesk.com/v1/bpi/";

    public static Retrofit provideBitstamp() {
        return new Retrofit.Builder()
                .baseUrl(mBaseStampUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit provideCoindesk() {
        return new Retrofit.Builder()
                .baseUrl(mBaseCoindeskUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
