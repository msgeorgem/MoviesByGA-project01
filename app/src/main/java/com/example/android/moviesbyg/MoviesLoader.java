package com.example.android.moviesbyg;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-12.
 */

public class MoviesLoader extends AsyncTaskLoader<ArrayList<SingleMovie>> {

    /** Tag for log messages */
    private static final String LOG_TAG = MoviesLoader.class.getName();
    private ArrayList<SingleMovie> mData;
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link MoviesLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public MoviesLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"onStartLoading");
        if (mData != null) {
            Log.i(LOG_TAG, "Use cached data");
            // Use cached data
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<SingleMovie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG,"loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        ArrayList<SingleMovie> singleNews = QueryUtils.fetchMoviesData(mUrl);
        return singleNews;
    }

    @Override
    public void deliverResult(ArrayList<SingleMovie> data) {
        if (isReset()) {
            return;
        }
        // We’ll save the data for later retrieval
//        ArrayList<SingleMovie> oldData = mData;
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        // Only deliver result if the loader is started
        if (isStarted()) {
            super.deliverResult(data);
        }

    }
}
