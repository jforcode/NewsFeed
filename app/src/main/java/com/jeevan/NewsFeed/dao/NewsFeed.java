package com.jeevan.NewsFeed.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 Created by jeevan on 9/14/17.

 _id: id for local database
 ID : the numeric ID of the article
 TITLE : the headline of the article
 URL : the URL of the article
 PUBLISHER : the publisher of the article
 CATEGORY : the category of the news item; one of: , b : business , t : science and technology , e: entertainment , m : health
 HOSTNAME : hostname where the article was posted
 TIMESTAMP : approximate timestamp of the article's publication, given in Unix time (seconds since midnight on Jan 1, 1970)

 */

public class NewsFeed implements Parcelable {
    private long _id;
    @SerializedName("ID")
    private long id;
    @SerializedName("TITLE")
    private String title;
    @SerializedName("URL")
    private String url;
    @SerializedName("PUBLISHER")
    private String publisher;
    @SerializedName("CATEGORY")
    private String category;
    @SerializedName("HOSTNAME")
    private String hostName;
    @SerializedName("TIMESTAMP")
    private long timeStamp;
    private boolean bookmarked;

    public NewsFeed() {

    }

    protected NewsFeed(Parcel in) {
        _id = in.readLong();
        id = in.readLong();
        title = in.readString();
        url = in.readString();
        publisher = in.readString();
        category = in.readString();
        hostName = in.readString();
        timeStamp = in.readLong();
        bookmarked = in.readByte() != 0;
    }

    public static final Creator<NewsFeed> CREATOR = new Creator<NewsFeed>() {
        @Override
        public NewsFeed createFromParcel(Parcel in) {
            return new NewsFeed(in);
        }

        @Override
        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(publisher);
        dest.writeString(category);
        dest.writeString(hostName);
        dest.writeLong(timeStamp);
        dest.writeByte((byte) (bookmarked ? 1 : 0));
    }
}
