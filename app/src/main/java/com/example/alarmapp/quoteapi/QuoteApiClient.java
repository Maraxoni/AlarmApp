package com.example.alarmapp.quoteapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuoteApiClient {
    private static final String BASE_URL = "https://api.quotable.io/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static QuoteApi getQuoteApi() {
        return retrofit.create(QuoteApi.class);
    }
}