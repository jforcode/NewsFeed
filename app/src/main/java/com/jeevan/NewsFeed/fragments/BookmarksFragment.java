package com.jeevan.NewsFeed.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jeevan.NewsFeed.R;
import com.jeevan.NewsFeed.adapters.BookmarksAdapter;
import com.jeevan.NewsFeed.dao.DbTransactions;
import com.jeevan.NewsFeed.dao.NewsFeed;
import com.jeevan.NewsFeed.interfaces.ListRemovalListener;
import com.jeevan.NewsFeed.interfaces.MainActivityChangeListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment implements ListRemovalListener{
    @BindView(R.id.bookmarks_list)
    RecyclerView bookmarksList;
    @BindView(R.id.error_layout)
    View errorLayout;
    @BindView(R.id.txt_error)
    TextView txtError;
    @BindView(R.id.btn_try_again)
    Button btnTryAgain;

    DbTransactions dbTransactions;
    BookmarksAdapter bookmarksAdapter;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, view);

        dbTransactions = DbTransactions.getDbInstance(getActivity());
        if (getActivity() instanceof MainActivityChangeListener) {
            ((MainActivityChangeListener) getActivity()).setToolbarTitle("Bookmarks");
        }

        bookmarksAdapter = new BookmarksAdapter(getActivity(), this);
        bookmarksList.setAdapter(bookmarksAdapter);
        bookmarksList.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<NewsFeed> bookmarked = dbTransactions.getBookmarkedNewsFeed();
        if (bookmarked.size() == 0) {
            errorLayout.setVisibility(View.VISIBLE);
            btnTryAgain.setVisibility(View.GONE);
            txtError.setText(R.string.no_bookmarks_error_msg);
            bookmarksList.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            bookmarksList.setVisibility(View.VISIBLE);
            bookmarksAdapter.setNewsFeed(bookmarked);
        }

        return view;
    }

    @Override
    public void showNoItemsLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.GONE);
        txtError.setText(R.string.no_bookmarks_error_msg);
        bookmarksList.setVisibility(View.GONE);
    }
}
