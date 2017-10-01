package com.jeevan.inshorts.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.adapters.EndlessRecyclerViewScrollListener;
import com.jeevan.inshorts.adapters.NewsFeedAdapter;
import com.jeevan.inshorts.api.NewsAPI;
import com.jeevan.inshorts.api.NewsAPIClient;
import com.jeevan.inshorts.dao.DbTransactions;
import com.jeevan.inshorts.dao.NewsFeed;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.news_feed_swipe_refresh)
    SwipeRefreshLayout newsFeedRefreshLayout;
    @BindView(R.id.news_feed_list)
    RecyclerView newsFeedList;
    NewsFeedAdapter newsFeedAdapter;
    @BindView(R.id.error_layout)
    View errorLayout;
    @BindView(R.id.txt_error)
    TextView txtError;
    @BindView(R.id.btn_try_again)
    Button btnTryAgain;

    Context context;
    DbTransactions dbTransactions;

    EndlessRecyclerViewScrollListener scrollListener;
    ProgressDialog progressDialog;
    int DEFAULT_RECORD_SIZE = 20;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        ButterKnife.bind(this, view);

        dbTransactions = DbTransactions.getDbInstance(getActivity());

        newsFeedAdapter = new NewsFeedAdapter(getActivity());
        newsFeedList.setAdapter(newsFeedAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsFeedList.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadPageFromDB(page);
            }
        };
        newsFeedList.addOnScrollListener(scrollListener);
        newsFeedRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFeed();
    }

    private void refreshFeed() {
        // retrofit to fetch the data
        NewsAPI client = NewsAPIClient.getRetrofit().create(NewsAPI.class);
        Call<List<NewsFeed>> call = client.getNewsFeed();
        call.enqueue(new Callback<List<NewsFeed>>() {
            @Override
            public void onResponse(Call<List<NewsFeed>> call, Response<List<NewsFeed>> response) {
                if (response.isSuccessful()) {
                    errorLayout.setVisibility(View.INVISIBLE);
                    newsFeedRefreshLayout.setVisibility(View.VISIBLE);
                    List<NewsFeed> newsFeed = response.body();
                    dbTransactions.saveNewsFeed(newsFeed);
                    loadPageFromDB(1);

                } else {
                    newsFeedAdapter.setNewsFeed(null);
                    errorLayout.setVisibility(View.VISIBLE);
                    newsFeedRefreshLayout.setVisibility(View.INVISIBLE);
                    try {
                        txtError.setText(response.errorBody().string());
                    } catch (IOException e) {
                        txtError.setText(R.string.generic_error_msg);
                    }
                }
                newsFeedRefreshLayout.setRefreshing(false);
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<List<NewsFeed>> call, Throwable t) {
                newsFeedAdapter.setNewsFeed(null);
                errorLayout.setVisibility(View.VISIBLE);
                newsFeedRefreshLayout.setVisibility(View.INVISIBLE);
                // TODO: check wether it is an internet error
                txtError.setText(R.string.no_internet_error_msg);
                newsFeedRefreshLayout.setRefreshing(false);
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
            }
        });
    }

    @OnClick(R.id.btn_try_again)
    public void tryAgain(View view) {
        refreshFeed();
    }

    @Override
    public void onRefresh() {
        refreshFeed();
    }

    private void loadPageFromDB(final int pageNum) {
        // dummy delay to show infinite scrolling
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newsFeedAdapter.addItems(dbTransactions.getNewsFeed(DEFAULT_RECORD_SIZE, pageNum));
            }
        }, 2000);
    }
}
