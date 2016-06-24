package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.gmail.lgelberger.popularmovies.service.PopularMoviesService;
import com.gmail.lgelberger.popularmovies.service.ReviewAndTrailerUpdateService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    //keep a lot of the constants for deciding about API here
    public static final int REVIEWS = 1;
    public static final int TRAILERS = 2;

    /////////////////////Database projection constants///////////////
    //For making good use of database Projections specify the columns we need
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_API_MOVIE_ID
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these must change.
    static final int COL_API_MOVIE_ID = 0;
    /////////////////////////////////////////////////////////


////////////////////////Only Method Used by Two Classes//////////////////////////
    /*
  USed by ReviewAndTrailerUpdateService and PopularMoviesService

  Trying to put doAPI Call here - It should ALWAYS be called from within a separate Service or AsyncTask
  i.e. OFF the main thread
   */

    /**
     *  To connect to network and fetch JSON results from API
     *
     * @param apiQueryUrl URL needed for Sepecific API call
     * @return JSON result of API call
     */
    public static String fetchJsonFromApi(URL apiQueryUrl) {
        Log.v(LOG_TAG, "fetchJsonFromApi - doing it now");
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










//////////////////////////Used by Review and Trailer Update Service ONLY ////////////////////////////////////////


    /**
     * Examples of URL built
     * http://api.themoviedb.org/3/movie/293660/videos?api_key=[API Key]
     * http://api.themoviedb.org/3/movie/293660/reviews?api_key=[API Key]
     *
     * @param context Needed for accessing App Strings
     * @param ApiMovieID API Id of movie
     * @param makeReviewURL true - make review URL, false - make trailer URL
     * @return URL for Review or Trailer API call based on API MovieID passed in
     */
    public static URL makeReviewsAndTrailerAPIQueryURL(Context context, String ApiMovieID, Boolean makeReviewURL) {

        //boolean statement ? true result : false result;
        String reviewOrTrailer = makeReviewURL == true ?
                context.getString(R.string.movie_query_reviews) : //if true then  reviews URL string
                context.getString(R.string.movie_query_traliers);//if false then  trailers URL string

        URL url = null; //url to be built

        Uri builtUri = Uri.parse(context.getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(context.getString(R.string.movie_query_movie))
                .appendPath(ApiMovieID)
                .appendPath(reviewOrTrailer)
                .appendQueryParameter(context.getString(R.string.movie_query_key_api_key), context.getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "In makeReviewsAndTrailerAPIQueryURL, The Query url is " + url);

        return url;
    }



/////////////////////////////Used by Main Activity ONLY //////////////////////////////////////////////////////////


    /**
     * Used Twice by MainActivityOnly !!!!
     * Used in onCreate and onSharedPreferenceListener
     *
     * to connect to network update the local database of movies
     * <p>
     * Makes the API query URL from movieSortOrder
     * Checks to see if network connectivity exists
     * If yes, calls
     * <p>
     * This will have to be rewritten to allow for Reviews and Trailers to be fetched from API if needed
     *
     */
    public static void updateDatabaseFromApi(Context context, String movieSortOrderOrMovieApiID) {

        //figure out whether looking at sort order or API movie ID
        //to do this check to see if it is a number, if yes, then it is API movie ID - get Revies and trailers
        Boolean isConnected = isConnectedToInternet(context);


        if (isConnected) {
            URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
            startPopularMoviesService(context, movieQueryURL);
            Log.v(LOG_TAG, "starting Sort Order API call");

        } else { //no internet connection
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
        }

        //check type whether input String is movieSortOrder or MovieAPI ID
            /*if (TextUtils.isDigitsOnly(movieSortOrderOrMovieApiID)) {  //return digits only - IE it is an API movie ID




                URL reviewsApiQueryUrl = makeReviewsAPIQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, reviewsApiQueryUrl);// start downloading the reviews for the desired movie
                Log.v(LOG_TAG, "starting Reviews API call");

                URL trailersApiQueryUrl = makeTrailersAPIQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, trailersApiQueryUrl);// start downloading the trailers for the desired movie
                Log.v(LOG_TAG, "starting Trailers API call");

*/

/*
            } else { //it is a sort order URI - update the entire database
                URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrderOrMovieApiID); //make the API url
                startPopularMoviesService(context, movieQueryURL);
                Log.v(LOG_TAG, "starting Sort Order API call");
            }
        }else { //no internet connection
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
        }*/
    }


    //private helper method to check internet connectivity before starting PopularMoviesService
    private static boolean isConnectedToInternet(Context context) {
        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    /**
     * Private Helper Method
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


    //Starts the PopularMoviesService to update the Database from API if needed
    private static void startPopularMoviesService(Context context, URL ApiQueryUrl) {
        String movieQueryURLAsString = ApiQueryUrl.toString();

        Intent intent = new Intent(context, PopularMoviesService.class);  //make explicit intent for my service
        intent.putExtra(PopularMoviesService.MOVIE_API_QUERY_EXTRA_KEY, //put extra with key MOVIE_API_QUERY_EXTRA_KEY
                // movieQueryURL); //put in the movieQueryURL - THIS WILL CHANGE SOON!!!!!!!!
                movieQueryURLAsString); //put in the movieQueryURL - THIS WILL CHANGE SOON!!!!!!!!
        context.startService(intent);
    }






    /**
     * Used by MainActivityONLY
     *
     * Gets the information needed to start the ReviewAndTrailerUpdateService
     *
     * @param context          Application context for accessing Strings
     * @param movieDetailDbUri The database Uri for one movie
     */
    public static void updateOneMovieReviewsAndTrailersFromApi(Context context, Uri movieDetailDbUri) {
        //Do a database lookup to get the API Id
        //get cursor for entire database
        Cursor oneMovieCursor = context.getContentResolver().query(movieDetailDbUri, //get just one row back
                MOVIE_COLUMNS, //the projection - just the API movie ID in projections
                null, //selection - select the entire database
                null, //selection Args
                null); //sort order (doesn't matter since we're going to go through them all anyway  - order doesn't matter to us

        try {
            String movieDatabaseId = MovieContract.MovieEntry.getIdFromUri(movieDetailDbUri);//get the movieDatabaseId

            if (oneMovieCursor.moveToFirst()) { //move cursor to first  - if no cursor, don't do API call

                String movieApiId = oneMovieCursor.getString(COL_API_MOVIE_ID); //get the API id of movie

                //now start the ReviewandTrailerUpdateService for the movie returned from cursor
                Intent intent = new Intent(context, ReviewAndTrailerUpdateService.class);  //make explicit intent for service
                intent.putExtra(ReviewAndTrailerUpdateService.REVIEW_TRAILER_DB_ID_EXTRA, movieDatabaseId); //put the _ID of local database in
                intent.putExtra(ReviewAndTrailerUpdateService.REVIEW_TRAILER_API_ID_EXTRA, movieApiId);
                context.startService(intent);
            } else {
                Log.v(LOG_TAG, "updateOneMovieReviewsAndTrailersFromApi - no valid cursor from database");
            }
        } finally {
            oneMovieCursor.close(); //close cursor at the end
        }
    }





}
