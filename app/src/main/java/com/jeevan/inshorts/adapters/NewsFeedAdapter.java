package com.jeevan.inshorts.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeevan.inshorts.interfaces.BookmarkEventListener;
import com.jeevan.inshorts.R;
import com.jeevan.inshorts.dao.NewsFeed;
import com.jeevan.inshorts.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeevan on 9/14/17.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsFeedViewHolder> {
    Context context;
    List<NewsFeed> newsFeeds;
    BookmarkEventListener bookmarkEventListener;

    public NewsFeedAdapter(Context context) {
        this.context = context;
        this.newsFeeds = new ArrayList<>();
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
    public void onBindViewHolder(NewsFeedViewHolder holder, int position) {
        final NewsFeed news = newsFeeds.get(position);
        holder.txtTitle.setText(news.getTitle());
        holder.txtPublisher.setText(news.getPublisher());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(news.getTimeStamp());
        holder.txtTimeStamp.setText(Util.getDateDisplayString(cal));
        holder.imgBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bookmarkEventListener.bookmark(news);
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

    class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.li_home_news)
        CardView parent;
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.txt_publisher)
        TextView txtPublisher;
        @BindView(R.id.txt_timestamp)
        TextView txtTimeStamp;
        @BindView(R.id.img_bookmark)
        ImageView imgBookmark;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
