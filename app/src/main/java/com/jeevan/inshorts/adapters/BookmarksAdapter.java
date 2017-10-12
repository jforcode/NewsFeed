package com.jeevan.inshorts.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jeevan.inshorts.R;
import com.jeevan.inshorts.dao.NewsFeed;
import com.jeevan.inshorts.interfaces.BookmarkEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeevan on 9/14/17.
 */

public class BookmarksAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {
    Context context;
    List<NewsFeed> newsFeeds;
    BookmarkEventListener bookmarkEventListener;

    public BookmarksAdapter(Context context) {
        this.context = context;
        this.newsFeeds = new ArrayList<>();
        if (context instanceof BookmarkEventListener) {
            this.bookmarkEventListener = (BookmarkEventListener) context;
        }
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
            }
        });
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsFeeds.size();
    }

}