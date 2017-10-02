package com.jeevan.inshorts.dao;

/**
 * Created by jeevan on 10/1/17.
 */

public class BookmarksTable {
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_TIMESTAMP = "TIMESTAMP";
    public static final String TABLE_NAME = "BOOKMARKS";
    public static final String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_TITLE + " PRIMARY KEY, "
            + KEY_TIMESTAMP + " INTEGER NOT NULL "
            + ")";

    public static final String DELETE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;

}
