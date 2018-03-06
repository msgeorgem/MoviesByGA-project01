package com.example.android.moviesbyg;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Marcin on 2018-02-10.
 */

class QueryUtils {
    public static final String TEST_MDB_MOVIE_PATH = "https://api.themoviedb.org/3/movie/321612/videos?api_key=1157007d8e3f7d5e0af6d7e4165e2730";
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String MDB_POSTER_PATH = "http://image.tmdb.org/t/p/w185";
    private static final String MDB_MOVIE_PATH1 = "https://api.themoviedb.org/3/movie/";
    /**
     * Tag for the log messages
     */
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    private static final String MDB_MOVIE_PATH2 = "/videos?api_key=" + api_key;
    private static final String MDB_REVIEWS_PATH2 = "/reviews?api_key=" + api_key;
    private static final String API_KEY = "api_key";


    private static final String ERROR_MESSAGE = "Problem parsing the movie JSON results";
    private static final String MDB_RESULTS = "results";
    private static final String MDB_TITLE = "title";
    private static final String MDB_DATE = "release_date";
    private static final String MDB_POPULARITY = "popularity";
    private static final String MDB_VOTE = "vote_average";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_POSTER = "poster_path";
    private static final String MDB_ID = "id";
    private static final String MDB_BACKDROP_PATH = "backdrop_path";
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }



    /**
     * Query the USGS dataset and return an {@link ArrayList <SingleMovie>} object to represent a single news.
     */
    public static ArrayList<SingleMovie> fetchMoviesData(String requestUrl) {
        Log.i(LOG_TAG,"fetchSingleNewsData");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<SingleMovie> singleMovie = extractMovies(jsonResponse);

        // Return the {@link Event}
        return singleMovie;


    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link SingleMovie} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<SingleMovie> extractMovies(String singleMovieJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(singleMovieJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding singleMovie to
        ArrayList<SingleMovie> singleMovie = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(singleMovieJSON);
            JSONArray jsonResultsArray = root.getJSONArray(MDB_RESULTS);

            for (int i = 0; i < jsonResultsArray.length(); i++) {
                JSONObject movie = jsonResultsArray.getJSONObject(i);

                String movieTitle = movie.getString(MDB_TITLE);
                String movieDate = movie.getString(MDB_DATE);
                String movieVote = movie.getString(MDB_VOTE);
                String movieOverview = movie.getString(MDB_OVERVIEW);

                String movieId = movie.getString(MDB_ID);
                String movieIdPath = MDB_MOVIE_PATH1 + movieId + MDB_MOVIE_PATH2;
                String reviewIdPath = MDB_MOVIE_PATH1 + movieId + MDB_REVIEWS_PATH2;

                String moviePoster = movie.getString(MDB_POSTER);
                String posterPath = MDB_POSTER_PATH+moviePoster;

                singleMovie.add(new SingleMovie(movieTitle, movieOverview, movieDate, movieVote, posterPath, movieIdPath, reviewIdPath, movieId));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of news
        return singleMovie;
    }

}
