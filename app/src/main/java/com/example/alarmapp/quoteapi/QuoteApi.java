package com.example.alarmapp.quoteapi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApi {
    @GET("random")
    Call<QuoteResponse> getRandomQuote();
}