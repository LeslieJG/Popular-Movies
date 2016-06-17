package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.service.PopularMoviesService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    //keep a lot of the constants for deciding about API here
    public static final int REVIEWS = 1;
    public static final int TRAILERS = 2;


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
   // public static URL makeReviewsAPIQueryURL(String movieID){
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



    //private helper method to check internet connectivity before starting PopularMoviesService
    private static boolean isConnectedToInternet(Context context){
        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }



    /**
     * to connect to network update the local database of movies
     *
     * Makes the API query URL from movieSortOrder
     * Checks to see if network connectivity exists
     * If yes, calls
     *
     * This will have to be rewritten to allow for Reviews and Trailers to be fetched from API if needed
     */
    public static void updateDatabaseFromApiIfNeeded(Context context, String movieSortOrderOrMovieApiID) {

        //figure out whether looking at sort order or API movie ID
        //to do this check to see if it is a number, if yes, then it is API movie ID - get Revies and trailers
        Boolean isConnected = isConnectedToInternet(context);

        if (isConnected){
            //check type whether input String is movieSortOrder or MovieAPI ID
            if (TextUtils.isDigitsOnly(movieSortOrderOrMovieApiID)) {  //return digits only - IE it is an API movie ID




                URL reviewsApiQueryUrl = makeReviewsAPIQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, reviewsApiQueryUrl);// start downloading the reviews for the desired movie
                Log.v(LOG_TAG, "starting Reviews API call");

                URL trailersApiQueryUrl = makeTrailersAPIQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, trailersApiQueryUrl);// start downloading the trailers for the desired movie
                Log.v(LOG_TAG, "starting Trailers API call");




            } else { //it is a sort order URI - update the entire database
                URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, movieQueryURL);
                Log.v(LOG_TAG, "starting Sort Order API call");
            }
        }else { //no internet connection
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
        }
    }

    //Starts the PopularMoviesService to update the Database from API if needed
    private static void startPopularMoviesService(Context context, URL ApiQueryUrl) {
        String movieQueryURLAsString = ApiQueryUrl.toString();

        Intent intent = new Intent(context, PopularMoviesService.class);  //make explicit intent for my service
        intent.putExtra(PopularMoviesService.MOVIE_API_QUERY_EXTRA_KEY, //put extra with key MOVIE_API_QUERY_EXTRA_KEY
               // movieQueryURL); //put in the movieQueryURL - THIS WILL CHANGE SOON!!!!!!!!
                movieQueryURLAsString); //put in the movieQueryURL - THIS WILL CHANGE SOON!!!!!!!!
        context.startService(intent);
    }


    //Need to get Movie ID from this
  //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=123456
    //
    //  http://api.themoviedb.org/3/movie/293660/videos?api_key=123456
    public static String getApiMovieIdFromUri (Uri uri){
        //pathSegments should have 3/movie/293660/videos or 3/movie/293660/reviews
       Log.v(LOG_TAG, "in getApiMovieIdFromUri. The Uri passed in is "+ uri);

        List<String> pathSegments = uri.getPathSegments();
        String movieID = pathSegments.get(2); //get the third (zero indexed) path segment

        Log.v(LOG_TAG, "in getApiMovieIdFromUri. The movieID (or third path segment is"+ movieID);

        //should check that the String can be converted to a number, because if wrong API url passed in,
        //the string COULD be the sort order (popular or top_rated)

         if (TextUtils.isDigitsOnly(movieID)) {  //return movie ID if it is digits only
             return movieID;
         }
      //  Log.v(LOG_TAG, "getApiMovieIdFromUri was passed the wrong uri. Unable to extract APiMovie ID. Uri passed in was " + uri);
        return null; //if it is not digits we have the wrong uri passed in
    }

/*

    public static int getTypeOfApiQuery(URL url){

    }
*/



    /*
    Trying to put doAPI Call here - It should ALWAYS be called from within a separate Service or AsyncTask
    i.e. OFF the main thread
     */
    public static String doApiCall(URL apiQueryUrl) {
        Log.v(LOG_TAG, "doApiCall - doing it now");
        //If we get to here then we need to make the API call to get data (i.e the data is not already in database)
        String movieJsonStr = null; // Will contain the raw JSON response as a string.

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to themoviedb.org, and open the connection
            urlConnection = (HttpURLConnection) apiQueryUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            //return the movie JSON info as String
            return movieJsonStr; // successful, done here (except for finally block)

        } catch (MalformedURLException e) {
            //  Toast.makeText(mContext, "Got invalid data from server", Toast.LENGTH_LONG).show();
            e.printStackTrace(); //the URL was malformed
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally { //disconnect the URL connection and close reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        // This will only happen if there was an error getting or parsing the movie data. or API delivers nothing
        return null;
    }




}
