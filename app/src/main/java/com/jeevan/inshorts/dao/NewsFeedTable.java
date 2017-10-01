package com.jeevan.inshorts.dao;

/**
 * Created by jeevan on 9/18/17.
 */

public class NewsFeedTable {
    public static final String TABLE_NAME = "NEWS_FEED";
    public static final String _ID = "_ID";
    public static final String KEY_ID = "ID";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_URL = "URL";
    public static final String KEY_PUBLISHER = "PUBLISHER";
    public static final String KEY_CATEGORY = "CATEGORY";
    public static final String KEY_HOSTNAME = "HOSTNAME";
    public static final String KEY_TIMESTAMP = "TIMESTAMP";

    public static final String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // local primary key
            KEY_ID + " INTEGER NOT NULL, " + // id field of the online data
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_URL + " TEXT NOT NULL, " +
            KEY_PUBLISHER + " TEXT NOT NULL, " +
            KEY_CATEGORY + " TEXT NOT NULL, " +
            KEY_HOSTNAME + " TEXT NOT NULL, " +
            KEY_TIMESTAMP + " INTEGER NOT NULL" +
            ")";

    public static final String DELETE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
