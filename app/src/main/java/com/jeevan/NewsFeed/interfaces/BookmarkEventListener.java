package com.jeevan.NewsFeed.interfaces;

import com.jeevan.NewsFeed.dao.NewsFeed;

/**
 * Created by jeevan on 9/14/17.
 */

public interface BookmarkEventListener {
    void bookmark(NewsFeed feed);
}
