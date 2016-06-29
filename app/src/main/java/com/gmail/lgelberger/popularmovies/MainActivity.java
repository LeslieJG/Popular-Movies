package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.gmail.lgelberger.popularmovies.service.PopularMoviesService;
import com.gmail.lgelberger.popularmovies.service.ReviewAndTrailerUpdateService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Main Activity
 * implements onMovieSelectedListener which is defined in the MainActivityFragment as
 * a callback for the fragment to pass the detail Movie ContentProvider URI of the movie that was clicked
 * That way , the MainActivity can pass that URI off to the detail fragment via an intent (in 1-pane mode)
 * or as a fragment argument (in 2 pane mode)
 *
 *
 */
public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMovieSelectedListener {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";// Create a Tag to identify the Detail Fragment
    private static final String LOG_TAG = MainActivity.class.getSimpleName(); //for debugging

    private Boolean mTwoPane; //used to indicate if we are using a two pane main layout (i.e. if it is a tablet)

    private SharedPreferences sharedPref; //declaring shared pref here
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener; //listening for changes to pref here, to be able

    private String movieSortOrder; //to hold a reference to current sort order preference - retireved from Preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //need to put this here to load the default preferences for app.
        //3 arguments   (context, xml file for settings, reload defaults more than once)
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false); //trying to set default values for all of app

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this); //initializing sharedPref
        //get default shared pref doesn't require a file name - it goes for the default file name
        prefListener = new MyPreferenceChangeListener();
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener); //registering the listener to allow for API calls when sort order changes

        /* Don't think I need snackbar - delete later if needed LJG
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        //update sort order from shared Pref and do API call to get movie data from database if needed
        if (savedInstanceState == null) { //if app is being run for the first time this session
            String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key); //get the sort order from preferences
          //  String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");
            movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

            updateDatabaseFromApiIfNeeded(this, movieSortOrder); //update the database with new API call if needed
          //  Log.v(LOG_TAG, "savedInstanceState is NULL - Doing API call!!!! SHould I really be doing this?");
        } else {
          //  Log.v(LOG_TAG, "savedInstanceState is Not null - No API call");
        }


        //See if our layout says this is a 2-Pane layout. If yes, set mTwoPane to true, and inflate the second pane fragment
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) { //if first time running app
               // Log.v(LOG_TAG, " in OnCreate - savedInstanceState == null - MAKING A NEW DETAIL FRAGMENT");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {//for debugging
              //  Log.v(LOG_TAG, " in OnCreate - savedInstanceState is NOT null - NOT making a new fragment - just leaving it alone");
            }

        } else { //In one pane layout - don't make a detail_fragment
            mTwoPane = false;
        }
    }


    /**
     * My Own OnSharedPreferenceChangeListener
     * Put on it's own for easier debugging
     * updates Movie Images if movie sort order is changed in settings
     */
    private class MyPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            //   if (isAdded()) { //just makes sure that fragment is attached to an Activity
            if (key.equals(getString(R.string.movie_sort_order_key))) { //should only do API call if sort order is NOT Favourites
                movieSortOrder = prefs.getString(key, "");

                //Log.v(LOG_TAG, "in MainActivity PrefChangeListener - sort order is now " + movieSortOrder);
                //update database if sort order is  "popular" or "top rated" and NOT Favourites
                updateDatabaseFromApiIfNeeded(getApplicationContext(), movieSortOrder);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class)); //add this line to launch settings
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Call back from MainActivityFragment with detail Movie Uri when Movie Grid has been clicked
     * Need to launch the Detail Fragment with this information.
     * Will pass the information to Detail Movie Fragment
     * Either with intent (if one pane view)
     * Or pass it directly to the fragment (if two pane view)
     *
     * @param movieDetailDbUri ContentProvider query URI for one movie to get details
     */
    @Override
    public void OnMovieSelected(Uri movieDetailDbUri) {
        //Movie Selected from grid ensure that the detail fragment has the information it needs

        // update database with this movie's Trailers and Reviews - so they will be there to display
        updateOneMovieReviewsAndTrailersFromApi(this, movieDetailDbUri);  //ONLY IF NOT FAVOURITES SORT ORDER!!!!! LJG ZZZ

        //do different things for phone or tablet
        if (mTwoPane == false) { //Just one pane, start DetailActivity and send the Query Uri with an intent
            Intent intentDetailActivity = new Intent(this, DetailActivity.class); //new intent with Detail Activity as recipient
            intentDetailActivity.setData(movieDetailDbUri);//setData puts a URI into the Intent - to be required by whomever received the intent
            startActivity(intentDetailActivity);
        }

        if (mTwoPane == true) { //then two panes - Update the detail Fragment with new data
            Bundle arguments = new Bundle(); //this will hold the MovieQuery URI
            arguments.putParcelable(DetailActivityFragment.MOVIE_DETAIL_URI, movieDetailDbUri); // put movieDetailQueryUri into arguments with key MOVIE_DETAIL_URI

            //  getSupportFragmentManager().beginTransaction().remove(oldFragment); //removes the old fragment
            DetailActivityFragment detailFragment = new DetailActivityFragment(); //make a new DetailActivityFragment
            detailFragment.setArguments(arguments); //add the arguments (movie query Uri) to the fragment
            // Now dynamically load fragment into DetailActivity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, DETAIL_FRAGMENT_TAG) //replace the old fragment with this new detailActivityFragment into container
                    .commit();
        }
    }


    ////////////////////////////////////////Private Helper Methods/////////////////////////////////


    /**
     * Used in onCreate and onSharedPreferenceListener
     * <p/>
     * to connect to network update the local database of movies
     * <p/>
     * Makes the API query URL from movieSortOrder
     * Checks to see if network connectivity exists
     * If yes, calls
     * <p/>
     */
    private void updateDatabaseFromApiIfNeeded(Context context, String movieSortOrder) {

        //Check to see if we need to do API call
       // if (movieSortOrder == (getString(R.string.movie_query_favourites))) { //if sort order is favourites
        if (movieSortOrder.equals(getString(R.string.movie_query_favourites))) { //if sort order is favourites
            //No need to do api call as favourites are stored locally
            //  Log.v(LOG_TAG, "In updateDatabaseFromApiIfNeeded, Sort order is " + movieSortOrder + " so NOT doing API call");
            return;
        }

        //  Log.v(LOG_TAG, "In updateDatabaseFromApiIfNeeded, Sort order is " + movieSortOrder);

        //check internet connectivity
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            URL movieQueryURL = makeMovieApiQueryURL(context, movieSortOrder); //make the API url
            String movieQueryURLAsString = movieQueryURL.toString();

            //Start the PopularMoviesService to update entre MovieEntry Database from API
            Intent intent = new Intent(context, PopularMoviesService.class);  //make explicit intent for my service
            intent.putExtra(PopularMoviesService.MOVIE_API_QUERY_EXTRA_KEY, //put extra with key MOVIE_API_QUERY_EXTRA_KEY
                    movieQueryURLAsString); //put in the movieQueryURL -
            context.startService(intent);
          //  Log.v(LOG_TAG, "in updateDatabaseFromApiIfNeeded, starting PopularMoviesService");

            //Log.v(LOG_TAG, "starting Sort Order API call");
        } else { //no internet connection
            Toast.makeText(context, "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Private Helper Method
     * Makes URL to access API to get movie info
     * <p/>
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
        //Log.v(LOG_TAG, "In makeMovieApiQueryURL, The Query url is " + url);
        return url;
    }


    /**
     * Used by MainActivityONLY
     * <p/>
     * Gets the information needed to start the ReviewAndTrailerUpdateService
     *
     * @param context          Application context for accessing Strings
     * @param movieDetailDbUri The database Uri for one movie
     */
    private static void updateOneMovieReviewsAndTrailersFromApi(Context context, Uri movieDetailDbUri) {

        //only update Reviews and Movies if the movieDetailDbUri is from MovieEntry table (which is the API called table)
        //and NOT the favouriteEntry Table URI
        //if it is the Favourite entry, then do nothing - do NOT procede any further
        List<String> uriPathSegments = movieDetailDbUri.getPathSegments();
        if (uriPathSegments.contains(MovieContract.PATH_FAVOURITE)){ //don't need to update favourites
           //Log.v(LOG_TAG, "in updateOneMovieReviewsAndTrailersFromApi - NOT doing api call as movieDetailDbUri is "+ movieDetailDbUri.toString());
            return; //don't do API call

        }


        //For making good use of database Projections specify the columns we need
        final String[] MOVIE_COLUMNS = {MovieContract.MovieEntry.COLUMN_API_MOVIE_ID};
        // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these must change.
        final int COL_API_MOVIE_ID = 0;

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