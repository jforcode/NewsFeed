package com.jeevan.NewsFeed.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jeevan.NewsFeed.R;
import com.jeevan.NewsFeed.activities.FilterActivity;
import com.jeevan.NewsFeed.activities.MainActivity;
import com.jeevan.NewsFeed.adapters.EndlessRecyclerViewScrollListener;
import com.jeevan.NewsFeed.adapters.NewsFeedAdapter;
import com.jeevan.NewsFeed.api.NewsAPI;
import com.jeevan.NewsFeed.api.NewsAPIClient;
import com.jeevan.NewsFeed.dao.DbTransactions;
import com.jeevan.NewsFeed.dao.NewsFeed;
import com.jeevan.NewsFeed.dao.NewsFeedTable;
import com.jeevan.NewsFeed.interfaces.MainActivityChangeListener;
import com.jeevan.NewsFeed.util.Constants;

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
public class HomePageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
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
    String sortBy, filterCategory, searchQuery;

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
        searchQuery = "";

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

    private void refreshFeed() {
        if (isConnectedToInternet()) {
            if (!isDataLoaded()) {
                loadDataFromAPI();
            } else {
                resetNewsFeedList();
                loadPageFromDB(1);
                cancelAllLoadingIndicators();
            }
        } else {
            resetNewsFeedList();
            cancelAllLoadingIndicators();
            toggleFeedList(false);
            txtError.setText(R.string.no_internet_error_msg);
        }
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
        if (isConnectedToInternet()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    newsFeedAdapter.addItems(dbTransactions.getNewsFeed(DEFAULT_RECORD_SIZE, pageNum, sortBy, filterCategory, searchQuery));
                    scrollListener.postLoad();
                    toggleFeedList(true);
                    checkForEmptyList();
                }
            }, 1000);
        } else {
            toggleFeedList(false);
            txtError.setText(R.string.no_internet_error_msg);
        }
    }

    private void loadDataFromAPI() {
        // retrofit to fetch the data
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        NewsAPI client = NewsAPIClient.getRetrofit().create(NewsAPI.class);
        Call<List<NewsFeed>> call = client.getNewsFeed();
        call.enqueue(new Callback<List<NewsFeed>>() {
            @Override
            public void onResponse(Call<List<NewsFeed>> call, Response<List<NewsFeed>> response) {
                if (response.isSuccessful()) {
                    toggleFeedList(true);
                    List<NewsFeed> newsFeed = response.body();
                    dbTransactions.clearNewsFeed();
                    dbTransactions.saveNewsFeed(newsFeed);
                    resetNewsFeedList();
                    loadPageFromDB(1);
                    cancelAllLoadingIndicators();
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                            .edit()
                            .putBoolean(Constants.SHARED_PREF_FEED_LOADED, true)
                            .commit();

                } else {
                    resetNewsFeedList();
                    toggleFeedList(false);
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
                toggleFeedList(false);
                txtError.setText(t.getLocalizedMessage());
                cancelAllLoadingIndicators();
            }
        });
    }

    private void toggleFeedList(boolean showFeed) {
        if (showFeed) {
            errorLayout.setVisibility(View.GONE);
            newsFeedRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            newsFeedRefreshLayout.setVisibility(View.GONE);
        }
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
            toggleFeedList(false);
            txtError.setText(R.string.no_records_error_msg);
        } else {
            toggleFeedList(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_page, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.home_menu_search).getActionView();
        searchView.setOnQueryTextListener(this);
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
            String[] keys = {Constants.CAT_BUSINESS, Constants.CAT_TECH, Constants.CAT_ENTERTAINMENT, Constants.CAT_SPORTS};
            String[] values = {"'b',", "'t',", "'e',", "'s',"};
            StringBuilder filterCategorySB = new StringBuilder();
            for (int i=0;i<keys.length;i++) {
                if (sortFilterData.getBooleanExtra(keys[i], false)) {
                    filterCategorySB.append(values[i]);
                }
            }
            if (filterCategorySB.length() > 0) {
                filterCategorySB.deleteCharAt(filterCategorySB.length() - 1);
            }
            filterCategory = filterCategorySB.toString();
        }
        resetNewsFeedList();
        loadPageFromDB(1);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean isDataLoaded() {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean(Constants.SHARED_PREF_FEED_LOADED, false);
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
    public boolean onQueryTextSubmit(String query) {
        searchQuery = query;
        resetNewsFeedList();
        loadPageFromDB(1);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchQuery = newText;
        resetNewsFeedList();
        loadPageFromDB(1);
        return true;
    }

}
