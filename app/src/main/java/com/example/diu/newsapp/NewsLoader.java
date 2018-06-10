package com.example.diu.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();
    /** Query URL */
    private String mUrl;
    public NewsLoader(Context context,String mUrl) {
        super(context);
        this.mUrl=mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(LOG_TAG,"onStartLoading");
    }

    @Override
    public List<News> loadInBackground() {
        if(mUrl==null){
            return null;
        }
        List<News> news=QueryUtils.fetchNewsData(mUrl);
        Log.v(LOG_TAG,"loadInBackground");
        return news;
    }
}
