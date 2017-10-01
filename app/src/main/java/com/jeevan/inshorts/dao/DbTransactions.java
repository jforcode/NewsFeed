package com.jeevan.inshorts.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeevan on 9/18/17.
 */

public class DbTransactions {
    private SQLiteDatabase db;
    private static DbTransactions dbInstance;

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

    private NewsFeed getNewsFeedFromCursor(Cursor newsCursor) {
        NewsFeed newsFeed = new NewsFeed();
        newsFeed.set_id(newsCursor.getLong(newsCursor.getColumnIndex(NewsFeedTable._ID)));
        newsFeed.setId(newsCursor.getLong(newsCursor.getColumnIndex(NewsFeedTable.KEY_ID)));
        newsFeed.setTitle(newsCursor.getString(newsCursor.getColumnIndex(NewsFeedTable.KEY_TITLE)));
        newsFeed.setUrl(newsCursor.getString(newsCursor.getColumnIndex(NewsFeedTable.KEY_URL)));
        newsFeed.setPublisher(newsCursor.getString(newsCursor.getColumnIndex(NewsFeedTable.KEY_PUBLISHER)));
        newsFeed.setCategory(newsCursor.getString(newsCursor.getColumnIndex(NewsFeedTable.KEY_CATEGORY)));
        newsFeed.setHostName(newsCursor.getString(newsCursor.getColumnIndex(NewsFeedTable.KEY_HOSTNAME)));
        newsFeed.setTimeStamp(newsCursor.getLong(newsCursor.getColumnIndex(NewsFeedTable.KEY_TIMESTAMP)));
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
    public List<NewsFeed> getNewsFeed(int recordSize, int currPage) {
        List<NewsFeed> feed = new ArrayList<>();
        int pageStart = (currPage - 1)*recordSize + 1;
        String limit = pageStart + ", " + recordSize;
        Cursor feedCursor = db.query(NewsFeedTable.TABLE_NAME, null, null, null, null, null, null, limit);
        while (feedCursor.moveToNext()) {
            feed.add(getNewsFeedFromCursor(feedCursor));
        }
        return feed;
    }



    public void saveNewsFeed(List<NewsFeed> feed) {
        for (NewsFeed newsFeed : feed) {
            db.insert(NewsFeedTable.TABLE_NAME, null, getNewsFeedContentValues(newsFeed));
        }
    }
}
