package com.example.android.moviesbyg;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SingleMovie>> {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_OVERVIEW = "EXTRA_OVERVIEW";
    public static final String EXTRA_RELEASE_DATE = "EXTRA_RELEASE_DATE";
    public static final String EXTRA_VOTE = "EXTRA_VOTE";
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_POSTER = "EXTRA_POSTER";
    private static final String LOG_TAG = MoviesActivity.class.getName();
    private static final String BUNDLE_RECYCLER_LAYOUT = "MoviesActivity.moviesRecyclerView.activity_movies";
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIES_LOADER_ID = 1;

    /* API_KEY gained from themoviedb.org */
    private static final String api_key = BuildConfig.API_KEY;
    private static final String API_KEY = "api_key";
    private final ArrayList<SingleMovie> movieGrid = new ArrayList<>();

    /**
     * Adapter for the list of movies
     */
    private MoviesAdapter.OnItemClickListener mListener;
    private MoviesAdapter mAdapter = new MoviesAdapter(movieGrid, mListener);
    private Parcelable state;
    private RecyclerView moviesRecyclerView;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Log.i(LOG_TAG, "initLoader");

        // Find a reference to the {@link ListView} in the layout
        moviesRecyclerView = findViewById(R.id.list_item);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mListener = new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SingleMovie item) {

                String currentMovieTitleString = item.getTitle();
                String currentMovieOverviewString = item.getOverview();
                String currentMovieReleaseDateString = item.getmReleaseDate();
                String currentMovieVotingString = item.getVoting();
                String currentMoviePosterString = item.getPoster();
                String currentMovieID = item.getMovieID();

                Intent intent1 = new Intent(getApplicationContext(), DetailActivity.class);

                intent1.putExtra(EXTRA_TITLE, currentMovieTitleString);
                intent1.putExtra(EXTRA_OVERVIEW, currentMovieOverviewString);
                intent1.putExtra(EXTRA_RELEASE_DATE, currentMovieReleaseDateString);
                intent1.putExtra(EXTRA_VOTE, currentMovieVotingString);
                intent1.putExtra(EXTRA_POSTER, currentMoviePosterString);
                intent1.putExtra(EXTRA_ID, currentMovieID);

                startActivity(intent1);
            }
        };

        mAdapter = new MoviesAdapter(movieGrid, mListener);


        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        moviesRecyclerView.setAdapter(mAdapter);
        moviesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        moviesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        moviesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEmptyStateTextView = findViewById(R.id.empty_view);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIES_LOADER_ID, null, MoviesActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Set empty state text to display "No internet connection"
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<ArrayList<SingleMovie>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String urlOrder = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_label)
        );

        Uri baseUri = Uri.parse(urlOrder);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(API_KEY, api_key);
        Log.i(LOG_TAG, uriBuilder.toString());
        return new MoviesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<SingleMovie>> loader, ArrayList<SingleMovie> movies) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "onLoadFinished");
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        moviesRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new MoviesAdapter(movieGrid, mListener);

        // If there is a valid list of {@link Movies}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
            mAdapter = new MoviesAdapter(movies, mListener);
            moviesRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SingleMovie>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset");
        mAdapter = new MoviesAdapter(movieGrid, mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save RecyclerView state
        state = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onStop() {
        super.onStop();
        // save RecyclerView state
        state = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (state != null) {
            moviesRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_m) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            moviesRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, moviesRecyclerView.getLayoutManager().onSaveInstanceState());
    }
}
