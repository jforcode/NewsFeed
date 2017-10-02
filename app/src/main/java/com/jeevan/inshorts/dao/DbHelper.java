package com.jeevan.inshorts.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeevan on 9/18/17.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "NEWS_FEED_APP";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NewsFeedTable.CREATE_QUERY);
        db.execSQL(BookmarksTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NewsFeedTable.DELETE_QUERY);
        db.execSQL(BookmarksTable.DELETE_QUERY);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(NewsFeedTable.DELETE_QUERY);
        db.execSQL(BookmarksTable.DELETE_QUERY);
        onCreate(db);
    }
}
