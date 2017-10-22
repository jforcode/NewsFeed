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
import com.jeevan.inshorts.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeevan on 9/14/17.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedViewHolder> {
    Context context;
    List<NewsFeed> newsFeeds;
    BookmarkEventListener bookmarkEventListener;

    public NewsFeedAdapter(Context context) {
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

    @Override
    public void onBindViewHolder(final NewsFeedViewHolder holder, int position) {
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
                if (news.isBookmarked()) {
                    Toast.makeText(context, "Added Bookmark - " + news.getTitle(), Toast.LENGTH_SHORT).show();
                    holder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_filled));
                } else {
                    Toast.makeText(context, "Removed bookmark - " + news.getTitle(), Toast.LENGTH_SHORT).show();
                    holder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_blank));
                }
                bookmarkEventListener.bookmark(news);
            }
        });
        if (news.isBookmarked()) {
            holder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_filled));
        } else {
            holder.imgBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_blank));
        }
        holder.txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareItem(news);
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

    public void addItems(List<NewsFeed> newsFeeds) {
        int itemCount = this.newsFeeds.size();
        this.newsFeeds.addAll(newsFeeds);
        notifyItemRangeInserted(itemCount, getItemCount());
        notifyItemRangeChanged(itemCount, getItemCount());
    }

    private void shareItem(NewsFeed news) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, news.getTitle() + "\n- " + news.getPublisher() + "\n" + news.getUrl());
        shareIntent.setType("text/plain");
        context.startActivity(shareIntent);
    }

}
