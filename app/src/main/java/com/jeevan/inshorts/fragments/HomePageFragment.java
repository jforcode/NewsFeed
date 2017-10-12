package com.jeevan.inshorts.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.activities.FilterActivity;
import com.jeevan.inshorts.activities.MainActivity;
import com.jeevan.inshorts.adapters.EndlessRecyclerViewScrollListener;
import com.jeevan.inshorts.adapters.NewsFeedAdapter;
import com.jeevan.inshorts.api.NewsAPI;
import com.jeevan.inshorts.api.NewsAPIClient;
import com.jeevan.inshorts.dao.DbTransactions;
import com.jeevan.inshorts.dao.NewsFeed;
import com.jeevan.inshorts.dao.NewsFeedTable;
import com.jeevan.inshorts.interfaces.MainActivityChangeListener;
import com.jeevan.inshorts.util.Constants;

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
    @BindView(R.id.btn_go_to_top)
    FloatingActionButton btnScrollToTop;

    Context context;
    DbTransactions dbTransactions;

    EndlessRecyclerViewScrollListener scrollListener;
    ProgressDialog progressDialog;
    int DEFAULT_RECORD_SIZE = 20;
    String sortBy, filterCategory;

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
        if (context instanceof MainActivityChangeListener) {
            ((MainActivityChangeListener) context).setToolbarTitle("Home");
        }
        sortBy = NewsFeedTable.KEY_TIMESTAMP;

        newsFeedAdapter = new NewsFeedAdapter(getActivity());
        newsFeedList.setAdapter(newsFeedAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsFeedList.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadPageFromDB(page);
            }

            @Override
            public void toggleScrollToTop(boolean enable) {
                btnScrollToTop.setVisibility(enable ? View.VISIBLE : View.GONE);
            }
        };
        newsFeedList.addOnScrollListener(scrollListener);
        newsFeedRefreshLayout.setOnRefreshListener(this);

        refreshFeed();

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
    }

    private void refreshFeed() {
        // retrofit to fetch the data
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        NewsAPI client = NewsAPIClient.getRetrofit().create(NewsAPI.class);
        Call<List<NewsFeed>> call = client.getNewsFeed();
        call.enqueue(new Callback<List<NewsFeed>>() {
            @Override
            public void onResponse(Call<List<NewsFeed>> call, Response<List<NewsFeed>> response) {
                if (response.isSuccessful()) {
                    errorLayout.setVisibility(View.INVISIBLE);
                    newsFeedRefreshLayout.setVisibility(View.VISIBLE);
                    List<NewsFeed> newsFeed = response.body();
                    dbTransactions.clearNewsFeed();
                    dbTransactions.saveNewsFeed(newsFeed);
                    resetNewsFeedList();
                    loadPageFromDB(1);

                } else {
                    resetNewsFeedList();
                    errorLayout.setVisibility(View.VISIBLE);
                    btnTryAgain.setVisibility(View.VISIBLE);
                    newsFeedRefreshLayout.setVisibility(View.INVISIBLE);
                    try {
                        txtError.setText(response.errorBody().string());
                    } catch (IOException e) {
                        txtError.setText(R.string.generic_error_msg);
                    }
                    cancelAllLoadingIndicators();
                }
            }

            @Override
            public void onFailure(Call<List<NewsFeed>> call, Throwable t) {
                resetNewsFeedList();
                errorLayout.setVisibility(View.VISIBLE);
                btnTryAgain.setVisibility(View.VISIBLE);
                newsFeedRefreshLayout.setVisibility(View.INVISIBLE);
                // TODO: check wether it is an internet error
                txtError.setText(R.string.no_internet_error_msg);
                cancelAllLoadingIndicators();
            }
        });
    }

    @OnClick(R.id.btn_try_again)
    public void tryAgain(View view) {
        refreshFeed();
    }

    @OnClick(R.id.btn_go_to_top)
    public void scrollToTop(View view) {
        if (newsFeedAdapter.getItemCount() > 0) {
            if (newsFeedAdapter.getItemCount() > 20) {
                newsFeedList.scrollToPosition(20);
            }
            newsFeedList.smoothScrollToPosition(0);
        }
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
                newsFeedAdapter.addItems(dbTransactions.getNewsFeed(DEFAULT_RECORD_SIZE, pageNum, sortBy, filterCategory));
                scrollListener.postLoad();
                cancelAllLoadingIndicators();
                checkForEmptyList();
            }
        }, 1000);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_filter:
                Intent sortIntent = new Intent(context, FilterActivity.class);
                sortIntent.putExtra(Constants.EX_EXISTING_FILTERS, filterCategory);
                ((MainActivity) context).startActivityForResult(sortIntent, Constants.RQ_FILTER);
                break;
            case R.id.home_menu_sort_time:
                sortBy = NewsFeedTable.KEY_TIMESTAMP;
                applySortAndFilter(null);
                break;
            case R.id.home_menu_sort_title:
                sortBy = NewsFeedTable.KEY_TITLE;
                applySortAndFilter(null);
                break;
            case R.id.home_menu_sort_publisher:
                sortBy = NewsFeedTable.KEY_PUBLISHER;
                applySortAndFilter(null);
                break;
        }
        return true;
    }

    public void applySortAndFilter(Intent sortFilterData) {
        if (sortFilterData != null) {
            // FIXME: this can be better
            StringBuilder filterCategorySB = new StringBuilder();
            if (sortFilterData.getBooleanExtra(Constants.CAT_BUSINESS, false)) {
                filterCategorySB.append("'b',");
            }
            if (sortFilterData.getBooleanExtra(Constants.CAT_TECH, false)) {
                filterCategorySB.append("'t',");
            }
            if (sortFilterData.getBooleanExtra(Constants.CAT_ENTERTAINMENT, false)) {
                filterCategorySB.append("'e',");
            }
            if (sortFilterData.getBooleanExtra(Constants.CAT_SPORTS, false)) {
                filterCategorySB.append("'s',");
            }
            if (filterCategorySB.length() > 0) {
                filterCategorySB.deleteCharAt(filterCategorySB.length() - 1);
            }
            filterCategory = filterCategorySB.toString();
        }
        resetNewsFeedList();
        loadPageFromDB(1);
    }

    private void resetNewsFeedList() {
        newsFeedAdapter.setNewsFeed(null);
        scrollListener.resetState();
    }

    private void cancelAllLoadingIndicators() {
        newsFeedRefreshLayout.setRefreshing(false);
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    private void checkForEmptyList() {
        if (newsFeedAdapter.getItemCount() == 0) {
            resetNewsFeedList();
            errorLayout.setVisibility(View.VISIBLE);
            newsFeedRefreshLayout.setVisibility(View.GONE);
            txtError.setText("No records.");
            btnTryAgain.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            newsFeedRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
