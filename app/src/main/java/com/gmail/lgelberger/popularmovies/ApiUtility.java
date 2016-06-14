package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Leslie on 2016-06-08.
 * <p>
 * Utility Methods for API calls
 * Most methods will be static
 */
public class ApiUtility {
    private static String LOG_TAG = ApiUtility.class.getSimpleName(); //for debugging


    /**
     * Makes URL to access API to get movie info
     * <p>
     * movie should now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @param context   Context of Application for String id references
     * @param sortOrder Sort Order required to be constructed into API call
     * @return URL for themoviedb.org
     */
    private static URL makeMovieApiQueryURL(Context context, String sortOrder) {
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(context.getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(context.getString(R.string.movie_query_movie))
                .appendPath(sortOrder)
                .appendQueryParameter(context.getString(R.string.movie_query_key_api_key), context.getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG + "POP MakeMovieQueryURL", "The Query url is " + url);
        return url;
    }


//   http://api.themoviedb.org/3/movie/popular?api_key=[My API Key]
    //   http://api.themoviedb.org/3/movie/top_rated?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    // OR the other types
    //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    //
    //  http://api.themoviedb.org/3/movie/293660/videos?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    //



    //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    public static URL makeReviewsAPIQueryURL(Context context, String movieID){
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(context.getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(context.getString(R.string.movie_query_movie))
                .appendPath(movieID)
                .appendPath(context.getString(R.string.movie_query_reviews))
                .appendQueryParameter(context.getString(R.string.movie_query_key_api_key), context.getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG + "MakeReviewsAPIQueryURL", "The Query url is " + url);

        return url;
    }


    //  http://api.themoviedb.org/3/movie/293660/videos?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    public static URL makeTrailersAPIQueryURL(Context context, String movieID){
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(context.getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(context.getString(R.string.movie_query_movie))
                .appendPath(movieID)
                .appendPath(context.getString(R.string.movie_query_traliers))
                .appendQueryParameter(context.getString(R.string.movie_query_key_api_key), context.getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG + "MakeTrailersAPIQueryURL", "The Query url is " + url);

        return url;
    }



    /**
     * to connect to network update the local database of movies
     *
     * Makes the API query URL from movieSortOrder
     * Checks to see if network connectivity exists
     * If yes, calls
     */
    public static void updateDatabaseFromAPI(Context context, String movieSortOrder) {
        URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrder); //make the API url

        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) { //if not network connection
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date
            FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask(context); //pass in context
            movieTask.execute(movieQueryURL);
        }
    }


    //Need to get Movie ID from this
  //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=123456
    //
    //  http://api.themoviedb.org/3/movie/293660/videos?api_key=123456
    public static String getApiMovieIdFromUri (Uri uri){
        //pathSegments should have 3/movie/293660/videos or 3/movie/293660/reviews
        List<String> pathSegments = uri.getPathSegments();
        String movieID = pathSegments.get(2); //get the third (zero indexed) path segment
        return movieID;
    }





}
