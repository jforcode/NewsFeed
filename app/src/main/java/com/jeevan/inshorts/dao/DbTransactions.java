package com.jeevan.inshorts.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jeevan on 9/18/17.
 */

public class DbTransactions {
    private SQLiteDatabase db;
    private static DbTransactions dbInstance;
    private static final String TAG = "DBTransactions";

    private DbTransactions(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static DbTransactions getDbInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DbTransactions(context.getApplicationContext());
        }
        return dbInstance;
    }

    private NewsFeed getNewsFeedFromCursor(Cursor feedCursor) {
        NewsFeed newsFeed = new NewsFeed();
        newsFeed.set_id(feedCursor.getLong(feedCursor.getColumnIndex(NewsFeedTable._ID)));
        newsFeed.setId(feedCursor.getLong(feedCursor.getColumnIndex(NewsFeedTable.KEY_ID)));
        newsFeed.setTitle(feedCursor.getString(feedCursor.getColumnIndex(NewsFeedTable.KEY_TITLE)));
        newsFeed.setUrl(feedCursor.getString(feedCursor.getColumnIndex(NewsFeedTable.KEY_URL)));
        newsFeed.setPublisher(feedCursor.getString(feedCursor.getColumnIndex(NewsFeedTable.KEY_PUBLISHER)));
        newsFeed.setCategory(feedCursor.getString(feedCursor.getColumnIndex(NewsFeedTable.KEY_CATEGORY)));
        newsFeed.setHostName(feedCursor.getString(feedCursor.getColumnIndex(NewsFeedTable.KEY_HOSTNAME)));
        newsFeed.setTimeStamp(feedCursor.getLong(feedCursor.getColumnIndex(NewsFeedTable.KEY_TIMESTAMP)));
        return newsFeed;
    }

    private ContentValues getNewsFeedContentValues(NewsFeed newsFeed) {
        ContentValues values = new ContentValues();
        values.put(NewsFeedTable.KEY_ID, newsFeed.getId());
        values.put(NewsFeedTable.KEY_TITLE, newsFeed.getTitle());
        values.put(NewsFeedTable.KEY_URL, newsFeed.getUrl());
        values.put(NewsFeedTable.KEY_PUBLISHER, newsFeed.getPublisher());
        values.put(NewsFeedTable.KEY_CATEGORY, newsFeed.getCategory());
        values.put(NewsFeedTable.KEY_HOSTNAME, newsFeed.getHostName());
        values.put(NewsFeedTable.KEY_TIMESTAMP, newsFeed.getTimeStamp());
        return values;
    }

    // get the page at index currPage, where each page has size = recordSize
    // sort on timestamp as latest first
    // if category is provided
    public List<NewsFeed> getNewsFeed(int recordSize, int currPage, String sortBy, String category) {
        List<NewsFeed> newsFeeds = new ArrayList<>();
        int pageStart = (currPage - 1)*recordSize + 1;
        StringBuilder query = new StringBuilder();
        String[] args = {};
        query.append("SELECT NF.*, BK.").append(BookmarksTable.KEY_TITLE).append(" AS BOOKMARKED")
                .append(" FROM ").append(NewsFeedTable.TABLE_NAME).append(" NF ")
                .append(" LEFT OUTER JOIN ").append(BookmarksTable.TABLE_NAME).append(" BK ")
                .append(" ON NF.").append(NewsFeedTable.KEY_TITLE).append(" = BK.").append(BookmarksTable.KEY_TITLE);

        if (category != null && !category.isEmpty()) {
            query.append(" WHERE NF.").append(NewsFeedTable.KEY_CATEGORY).append(" IN (").append(category).append(")");
        }
        query.append(" ORDER BY ").append(sortBy);
        if (sortBy.equals(NewsFeedTable.KEY_TIMESTAMP)) query.append(" DESC ");
        query.append(" LIMIT ").append(pageStart).append(",").append(recordSize);
        Log.d(TAG, query.toString());
        Cursor feedCursor = db.rawQuery(query.toString(), args);
        while (feedCursor.moveToNext()) {
            NewsFeed feed = getNewsFeedFromCursor(feedCursor);
            feed.setBookmarked(feedCursor.getString(feedCursor.getColumnIndex("BOOKMARKED")) != null);
            newsFeeds.add(feed);
        }
        return newsFeeds;
    }

    public void saveNewsFeed(List<NewsFeed> feed) {
        for (NewsFeed newsFeed : feed) {
            db.insert(NewsFeedTable.TABLE_NAME, null, getNewsFeedContentValues(newsFeed));
        }
    }

    public void bookmark(NewsFeed feed) {
        if (feed.isBookmarked()) {
            ContentValues values = new ContentValues();
            values.put(BookmarksTable.KEY_TITLE, feed.getTitle());
            values.put(BookmarksTable.KEY_TIMESTAMP, Calendar.getInstance().getTimeInMillis());
            Log.d(TAG, feed.getTitle() + " " + Calendar.getInstance().getTimeInMillis());
            db.insert(BookmarksTable.TABLE_NAME, null, values);
        } else {
            String where = BookmarksTable.KEY_TITLE + " = ?";
            String[] args = {feed.getTitle()};
            db.delete(BookmarksTable.TABLE_NAME, where, args);
        }
    }

    public List<NewsFeed> getBookmarkedNewsFeed() {
        StringBuilder query = new StringBuilder("SELECT DISTINCT NF.* FROM ").append(NewsFeedTable.TABLE_NAME).append(" NF ")
                .append(" INNER JOIN ").append(BookmarksTable.TABLE_NAME).append(" BK ")
                .append(" ON NF.").append(NewsFeedTable.KEY_TITLE).append(" = BK.").append(BookmarksTable.KEY_TITLE)
                .append(" ORDER BY BK.").append(BookmarksTable.KEY_TIMESTAMP).append(" DESC");
        Cursor bookmarkCursor = db.rawQuery(query.toString(), null);
        List<NewsFeed> bookmarks = new ArrayList<>();
        while (bookmarkCursor.moveToNext()) {
            NewsFeed newsFeed = getNewsFeedFromCursor(bookmarkCursor);
            newsFeed.setBookmarked(true);
            bookmarks.add(newsFeed);
        }
        return bookmarks;
    }

    public void clearNewsFeed() {
        db.execSQL(NewsFeedTable.CLEAR_QUERY);
    }
}
