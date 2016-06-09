package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

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
}
