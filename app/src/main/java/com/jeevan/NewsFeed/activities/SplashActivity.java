package com.jeevan.NewsFeed.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jeevan.NewsFeed.R;
import com.jeevan.NewsFeed.api.NewsAPI;
import com.jeevan.NewsFeed.api.NewsAPIClient;
import com.jeevan.NewsFeed.dao.DbTransactions;
import com.jeevan.NewsFeed.dao.NewsFeed;
import com.jeevan.NewsFeed.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if (!sharedPref.getBoolean(Constants.SHARED_PREF_FEED_LOADED, false)) {
            loadFeed();
        } else {
            startMainActivity();
        }
    }

    private void loadFeed() {
        NewsAPI client = NewsAPIClient.getRetrofit().create(NewsAPI.class);
        Call<List<NewsFeed>> call = client.getNewsFeed();
        call.enqueue(new Callback<List<NewsFeed>>() {
            @Override
            public void onResponse(Call<List<NewsFeed>> call, Response<List<NewsFeed>> response) {
                if (response.isSuccessful()) {
                    DbTransactions dbTransactions = DbTransactions.getDbInstance(SplashActivity.this);
                    List<NewsFeed> newsFeed = response.body();
                    dbTransactions.clearNewsFeed();
                    dbTransactions.saveNewsFeed(newsFeed);
                    sharedPref.edit().putBoolean(Constants.SHARED_PREF_FEED_LOADED, true).commit();
                }
                startMainActivity();
            }

            @Override
            public void onFailure(Call<List<NewsFeed>> call, Throwable t) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
