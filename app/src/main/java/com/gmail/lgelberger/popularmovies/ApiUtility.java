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
 *
 * Just trying to put some Utility methods here for API calls
 * Most methods will be static
 *
 *
 */
public class ApiUtility {
    private static String LOG_TAG =  ApiUtility.class.getSimpleName(); //for debugging



    /**
     *Makes URL to access API to get movie info
     * <p/>
     * movie should now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @param context Context of Application for String id references
     * @param sortOrder Sort Order required to be constructed into API call
     * @return URL for themoviedb.org
     */
    public static URL makeMovieApiQueryURL(Context context, String sortOrder) {
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
        Log.v(LOG_TAG + "POP MakeMovieQueryURL", "The Query url is "+ url);
        return url;
    }


    /**
     * to connect to network and load images into the gridView
     *
     * This should be put into Api Utilities - can be self contained
     */
    public static void updateDatabaseFromAPI(Context context, String movieSortOrder) {
       // SharedPreferences sharedPref; //declaring shared pref here - I HOPE THIS IS THE SAME AS MAIN ACTIVITY!!!!


        final String MOVIE_SORT_ORDER_KEY = context.getString(R.string.movie_sort_order_key); //to be able to look at sort order preference
      //I really hope this gets the shared preff from context
        //SharedPreferences sharedPref = context.getSharedPreferences("",0 ); //trying blank file name, 0 is the private mode (default)

        //Context mContext = getApplicationContext();
        //mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);


      //  String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");
        Log.v(LOG_TAG, "Movie sort order key is " + movieSortOrder + " It is retireved from the shared pref correctly");
        URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrder);

        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date

            //LJG ZZZ transferring to new separate class
            // FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask();
            //   movieTask.execute(movieQueryURL);

            //old method with movieAdapter passed in
            // FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask(getActivity(), movieAdapter); //pass in context and movieAdapter
//newer version - no movie adapter will used cursor loader
            FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask(context); //pass in context
            movieTask.execute(movieQueryURL);
        }
    }








}
