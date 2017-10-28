package com.jeevan.NewsFeed.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    RecyclerView.LayoutManager mLayoutManager;
    private boolean loading;
    // last loaded page, initially no page is there in list, hence 0
    private int lastLoadedPage = 0;
    // load if the no of items after current scroll position is this threshold
    private int loadingThreshold = 5;
    // if the current scroll position is at this threshold, show the go to top button
    private int topButtonVisibilityThreshold = 10;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public int getMaxSpaceTakingItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getMaxSpaceTakingItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        if (!loading && lastVisibleItemPosition + loadingThreshold > totalItemCount) {
            loading = true;
            onLoadMore(lastLoadedPage + 1, totalItemCount, view);
        }

        if (lastVisibleItemPosition > topButtonVisibilityThreshold) {
            toggleScrollToTop(true);
        } else {
            toggleScrollToTop(false);
        }
    }

    // load the page specified by pageNum
    // call postLoad after loading the page
    public abstract void onLoadMore(int pageNum, int totalItemsCount, RecyclerView view);

    // change the scrollListener's state after loading a page
    // is called after actual loading is done
    public void postLoad() {
        loading = false;
        lastLoadedPage++;
    }

    public void resetState() {
        loading = false;
        lastLoadedPage = 0;
    }

    public abstract void toggleScrollToTop(boolean enable);

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}