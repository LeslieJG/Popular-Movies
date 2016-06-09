package com.gmail.lgelberger.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMovieSelectedListener {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";// Create a Tag to identify the Detail Fragment
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Boolean mTwoPane; //used to indicate if we are using a two pane main layout (i.e. if it is a tablet)

    //adding stuff needed for initial API call here
    SharedPreferences sharedPref; //declaring shared pref here
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener; //listening for changes to pref here, to be able
    //to do new API calls if needed

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
        //get default shared pref doesn't require a file name - it goes for the defulat file name
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


        //if app hasn't been running before - Do a new API call to update the local database
        if (savedInstanceState == null) { //if app is being run for the first time this session
            //get the sort order from preferences
            String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key);
            String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

            ApiUtility.updateDatabaseFromAPI(this, movieSortOrder); //update the database with new API call
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
                Log.v(LOG_TAG, " in OnCreate - savedInstanceState == null - MAKING A NEW DETAIL FRAGMENT");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {//for dubgging
                Log.v(LOG_TAG, " in OnCreate - savedInstanceState is NOT null - NOT making a new fragment - just leaving it alone");
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
            if (key.equals(getString(R.string.movie_sort_order_key))) {
                String movieSortOrder = prefs.getString(key, "");
                ApiUtility.updateDatabaseFromAPI(getApplicationContext(), movieSortOrder);
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

    /*
    Call back from MainActivityFragment with detail Movie Uri when Movie Grid has been clicked
    Need to launch the Detail Fragement with this information.
    Will pass the information to Detail Movie Fragment
    Either with intent (if one pane view)
    Or pass it directly to the fragment (if two pane view)
     */
    @Override
    public void OnMovieSelected(Uri movieUri) {
        if (mTwoPane == false) { //Just one pane, start DetailActivity and send the Query Uri with an intent
            Intent intentDetailActivity = new Intent(this, DetailActivity.class); //new intent with Detail Activity as recipient
            intentDetailActivity.setData(movieUri);//setData puts a URI into the Intent - to be required by whomever received the intent
            startActivity(intentDetailActivity);
        }

        if (mTwoPane == true) { //then two panes - Update the detail Fragment with new data
            Bundle arguments = new Bundle(); //this will hold the MovieQuery URI
            arguments.putParcelable(DetailActivityFragment.MOVIE_DETAIL_URI, movieUri); // put movieDetailQueryUri into arguments with key MOVIE_DETAIL_URI

            //  getSupportFragmentManager().beginTransaction().remove(oldFragment); //removes the old fragment
            DetailActivityFragment detailFragment = new DetailActivityFragment(); //make a new DetailActivityFragment
            detailFragment.setArguments(arguments); //add the arguments (movie query Uri) to the fragment
            // Now dynamically load fragment into DetailActivity
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, DETAIL_FRAGMENT_TAG) //replace the old fragment with this new detailActivityFragment into container
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}

