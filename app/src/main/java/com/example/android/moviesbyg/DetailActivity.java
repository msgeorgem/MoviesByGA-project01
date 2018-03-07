package com.example.android.moviesbyg;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.moviesbyg.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

/**
 * Created by Marcin on 2018-02-10.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static String MDB_CURRENT_MOVIE_ID;
    private final String MDB_SHARE_HASHTAG = "IMDB Source";
    private String mMovieSummary;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(MoviesActivity.EXTRA_TITLE);
        mDetailBinding.part2.title.setText(title);

        String releaseDate = getIntent().getStringExtra(MoviesActivity.EXTRA_RELEASE_DATE);
        mDetailBinding.part2.releaseDate.setText(releaseDate);

        String vote = getIntent().getStringExtra(MoviesActivity.EXTRA_VOTE);
        mDetailBinding.part2.rating.setText(vote);

        String overview = getIntent().getStringExtra(MoviesActivity.EXTRA_OVERVIEW);
        mDetailBinding.part3.overview.setText(overview);

        String poster = getIntent().getStringExtra(MoviesActivity.EXTRA_POSTER);
        context = mDetailBinding.part1.poster.getContext();
        Picasso.with(context).load(poster).into(mDetailBinding.part1.poster);


        MDB_CURRENT_MOVIE_ID = getIntent().getStringExtra(MoviesActivity.EXTRA_ID);

        mMovieSummary = title + "" + releaseDate + "" + overview;
        Log.i(LOG_TAG, "initClipsLoader");


        ImageView imageView = findViewById(R.id.poster1);
        imageView.setColorFilter(0x7f000000, PorterDuff.Mode.DARKEN);


        Glide.with(context.getApplicationContext())
                .load(poster)   //passing your url to load image.
                .override(400, 400)  //just set override like this
                .fitCenter()
                .into(imageView);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(DetailActivity.this);
            return true;
        }
        /* Share menu item clicked */
        if (id == R.id.action_share_d) {
            Intent shareIntent = createShareMovieIntent();
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mMovieSummary + MDB_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}