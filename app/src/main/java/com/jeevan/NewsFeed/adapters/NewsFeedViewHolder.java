package com.jeevan.NewsFeed.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeevan.NewsFeed.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class NewsFeedViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.li_home_news)
    CardView parent;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_publisher)
    TextView txtPublisher;
    @BindView(R.id.img_bookmark)
    ImageView imgBookmark;
    @BindView(R.id.txt_category)
    TextView txtCategory;
    @BindView(R.id.txt_timestamp)
    TextView txtTimestamp;
    @BindView(R.id.txt_share)
    TextView txtShare;

    public NewsFeedViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}