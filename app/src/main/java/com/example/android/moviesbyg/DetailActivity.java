package com.example.android.moviesbyg;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
 * Created by Marcin on 2017-09-10.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static String MDB_CURRENT_MOVIE_ID;
    private final String MDB_SHARE_HASHTAG = "IMDB Source";
    private String mMovieSummary;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(MoviesAdapter.EXTRA_TITLE);
        mDetailBinding.part2.title.setText(title);

        String releaseDate = getIntent().getStringExtra(MoviesAdapter.EXTRA_RELEASE_DATE);
        mDetailBinding.part2.releaseDate.setText(releaseDate);

        String vote = getIntent().getStringExtra(MoviesAdapter.EXTRA_VOTE);
        mDetailBinding.part2.rating.setText(vote);

        String overview = getIntent().getStringExtra(MoviesAdapter.EXTRA_OVERVIEW);
        mDetailBinding.part3.overview.setText(overview);

        String poster = getIntent().getStringExtra(MoviesAdapter.EXTRA_POSTER);
        context = mDetailBinding.part1.poster.getContext();
        Picasso.with(context).load(poster).into(mDetailBinding.part1.poster);


        MDB_CURRENT_MOVIE_ID = getIntent().getStringExtra(MoviesAdapter.EXTRA_ID);

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
//for the details?! It's super simple. match_parent to both over an image view. So,
// you'll need to have a frame or relative layout to put it over the back.
// Then, scaleType = centerCrop. To make it "darker" add any view on top with an alpha channel dark: #80000000 for example

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

    public class BlurBuilder {
        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 7.5f;

        public Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            }
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic.setRadius(BLUR_RADIUS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic.setInput(tmpIn);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                theIntrinsic.forEach(tmpOut);
            }
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }
}