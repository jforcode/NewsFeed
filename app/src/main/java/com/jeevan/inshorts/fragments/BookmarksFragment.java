package com.jeevan.inshorts.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.adapters.BookmarksAdapter;
import com.jeevan.inshorts.adapters.NewsFeedAdapter;
import com.jeevan.inshorts.dao.DbTransactions;
import com.jeevan.inshorts.interfaces.MainActivityChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {
    @BindView(R.id.bookmarks_list)
    RecyclerView bookmarksList;

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

        bookmarksAdapter = new BookmarksAdapter(getActivity());
        bookmarksList.setAdapter(bookmarksAdapter);
        bookmarksList.setLayoutManager(new LinearLayoutManager(getActivity()));

        bookmarksAdapter.setNewsFeed(dbTransactions.getBookmarkedNewsFeed());

        return view;
    }



}
