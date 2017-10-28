package com.jeevan.NewsFeed.api;

import com.jeevan.NewsFeed.dao.NewsFeed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jeevan on 9/14/17.
 */

public interface NewsAPI {
    @GET("newsjson")
    Call<List<NewsFeed>> getNewsFeed();
}