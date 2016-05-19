package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    MovieAdapter movieAdapter;//declare custom MovieAdapter

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging

    SharedPreferences sharedPref; //declaring shared pref here
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;



    /*
    Girdview
How to auto-fit properly - also add the following to the xml file for gridview
To learn more, you can also try to set it equals to auto_fit. By doing so, your app can have the ability to judge the number of columns it should show based on the screen size (or different orientation). Try it! :)
and width to wrap content

http://stackoverflow.com/questions/6912922/android-how-does-gridview-auto-fit-find-the-number-of-columns/7874011#7874011

The solution is to measure your column size before setting the GridView's column width. Here is a quick way to measure Views offscreen:


(where cell is the specific grid cell in the gridview to measure

public int measureCellWidth( Context context, View cell )
{

    // We need a fake parent
    FrameLayout buffer = new FrameLayout( context );
    android.widget.AbsListView.LayoutParams layoutParams = new  android.widget.AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    buffer.addView( cell, layoutParams);

    cell.forceLayout();
    cell.measure(1000, 1000);

    int width = cell.getMeasuredWidth();

    buffer.removeAllViews();

    return width;
}
And then you just set the GridView's column width:

gridView.setColumnWidth( width );


You can use setColumnWidth() right after you use setAdapter() on your GridView. –
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.preferences, false); //trying to set default values for all of app
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);  //inflate the fragment view

        movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies_layout);  //initialize custom gridView adapter
        // now bind the adapter to the actual gridView so it knows which view it is populating
        // Get a reference to the gridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        //set adapter to gridview
        gridView.setAdapter(movieAdapter); //my custom adapter

        //adding click listener for grid
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int gridItemClicked, long grItemClicked) {

                MovieDataProvider selectedMovieFromGrid = (MovieDataProvider) movieAdapter.getItem(gridItemClicked); //getting movie detail straight from the movieAdapter

                Intent intentDetailActivity = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                intentDetailActivity.putExtra(getString(R.string.movie_details_intent_key), selectedMovieFromGrid);
                startActivity(intentDetailActivity);
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext()); //initializing sharedPref with the defaults
        prefListener = new MyPreferenceChangeListener();
       /* prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() { //making a OnSharedPreferencesChanged LIstener
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                //  if (key == getString(R.string.movie_sort_order_key)) {
                if (key.equals("movie_sort_order_key")) {
                    updateMovieGridImages(); //update the entire Grid from internet when sort order preference is changed
                }
            }
        };*/
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener); //registering the listener

        updateMovieGridImages(); //update the entire Grid from internet - when Fragment created

        return rootView;
    }

    /**
     * My Own OnSharedPreferenceChangeListener
     * Put on it's own for easier debgugging
     */
    private class MyPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (isAdded()) { //just makes sure that fragment is attached to an Actvity
                if (key.equals(getString(R.string.movie_sort_order_key))) {
                    updateMovieGridImages(); //update the entire Grid from internet when sort order preference is changed
                }
            }
        }
    }


    /**
     * to connect to network and load images into the gridView
     */
    private void updateMovieGridImages() {
        final String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key); //to be able to look at sort order preference
        String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

        URL movieQueryURL = makeMovieQueryURL(movieSortOrder);

        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date
            //LJG ZZZ tranfering to new separate class
            // FetchMovieTask movieTask = new FetchMovieTask();
            //   movieTask.execute(movieQueryURL);
            FetchMovieTask movieTask = new FetchMovieTask(getActivity(), movieAdapter); //pass in context and movieAdapter
            movieTask.execute(movieQueryURL);
        }
    }

    /**
     * Makes URL to access API to get movie info
     * <p/>
     * movie shoud now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @return URL for themoviedb.org
     */
    private URL makeMovieQueryURL(String sortOrder) {
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(getString(R.string.movie_query_movie))
                .appendPath(sortOrder)
                .appendQueryParameter(getString(R.string.movie_query_key_api_key), getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}











