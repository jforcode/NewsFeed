package com.jeevan.inshorts.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jeevan on 9/14/17.
 */

public class NewsAPIClient {
    private static final String BASE_URL = "http://starlord.hackerearth.com/";
    private static Retrofit retrofitInstance;

    public static Retrofit getRetrofit() {
        if (retrofitInstance != null) {
            return retrofitInstance;
        }
        retrofitInstance = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofitInstance;
    }
}
