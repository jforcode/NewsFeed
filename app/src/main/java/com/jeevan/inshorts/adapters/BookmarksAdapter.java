package com.jeevan.inshorts.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.activities.WebViewActivity;
import com.jeevan.inshorts.dao.NewsFeed;
import com.jeevan.inshorts.interfaces.BookmarkEventListener;
import com.jeevan.inshorts.interfaces.ListRemovalListener;
import com.jeevan.inshorts.util.Constants;
import com.jeevan.inshorts.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeevan on 9/14/17.
 */

public class BookmarksAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {
    Context context;
    List<NewsFeed> newsFeeds;
    BookmarkEventListener bookmarkEventListener;
    ListRemovalListener listRemovalListener;

    public BookmarksAdapter(Context context, ListRemovalListener listRemovalListener) {
        this.context = context;
        this.newsFeeds = new ArrayList<>();
        if (context instanceof BookmarkEventListener) {
            this.bookmarkEventListener = (BookmarkEventListener) context;
        }
        this.listRemovalListener = listRemovalListener;
    }

    public void setNewsFeed(List<NewsFeed> newsFeeds) {
        if (newsFeeds == null) {
            newsFeeds = new ArrayList<>();
        }
        this.newsFeeds = newsFeeds;
        notifyDataSetChanged();
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsFeedViewHolder(LayoutInflater.from(context).inflate(R.layout.li_home_news, parent, false));
    }

    public void onBindViewHolder(final NewsFeedViewHolder holder, final int position) {
        final NewsFeed news = newsFeeds.get(position);
        holder.txtTitle.setText(news.getTitle());
        holder.txtPublisher.setText(news.getPublisher());
        String cat = "";
        switch (news.getCategory().charAt(0)) {
            case 'b': cat = "Business"; break;
            case 't': cat = "Science & Technology"; break;
            case 's': cat = "Sports"; break;
            default: cat = "Others";
        }
        holder.txtCategory.setText(cat);
        holder.txtTimestamp.setText(Util.getDateDisplayString(news.getTimeStamp()));
        holder.imgBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                news.setBookmarked(!news.isBookmarked());
                Toast.makeText(context, "Removed bookmark - " + news.getTitle(), Toast.LENGTH_SHORT).show();
                bookmarkEventListener.bookmark(news);
                int changedCount = getItemCount() - position;
                newsFeeds.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, changedCount);

                if (getItemCount() == 0 && listRemovalListener != null) {
                    listRemovalListener.showNoItemsLayout();
                }
            }
        });
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(context, WebViewActivity.class);
                browserIntent.putExtra(Constants.IP_URL, news.getUrl());
                browserIntent.putExtra(Constants.IP_TITLE, news.getTitle());
                browserIntent.putExtra(Constants.IP_SUBTITLE, news.getHostName());
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsFeeds.size();
    }

}
