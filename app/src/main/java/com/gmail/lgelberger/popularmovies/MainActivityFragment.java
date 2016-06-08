package com.gmail.lgelberger.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.data.MovieContract;

import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 *
 * Going to implement a Cursor Loader to provide a cursor (from the database)
 * Will  be using a CursorAdapter  for grid views.
 *
 *
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //  MovieAdapter movieAdapter;//declare custom MovieAdapter - old way without cursor adapter
    MovieCursorAdapter movieAdapter; // declare my custom CursorAdapter

    SharedPreferences sharedPref; //declaring shared pref here
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    OnMovieSelectedListener movieListener; //refers to the containing activity of this fragment.
    // We will pass the selected movie Uri through this listener to the containing activity to deal with

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging

    //Step 1/3 of making Cursor Loader - Create Loader ID
    private static final int MOVIE_LOADER = 0;

    /////////////////////Database projection constants///////////////
    //For making good use of database Projections
    //specify the columns we need
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_API_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_API_MOVIE_ID = 2;
    static final int COL_MOVIE_POSTER_URL = 3;
    static final int COL_MOVIE_POSTER_IMAGE = 4;
    static final int COL_ORIGINAL_TITLE = 5;
    static final int COL_PLOT_SYNOPSIS = 6;
    static final int COL_VOTE_AVERAGE = 7;
    static final int COL_RELEASE_DATE = 8;
    static final int COL_MOVIE_REVIEW = 9;
    static final int COL_MOVIE_VIDEO = 10;
    /////////////////////////////////////////////////////////

    /*
    GridView
How to auto-fit properly - also add the following to the xml file for GridView
To learn more, you can also try to set it equals to auto_fit. By doing so, your app can have the ability to judge the number of columns it should show based on the screen size (or different orientation). Try it! :)
and width to wrap content

http://stackoverflow.com/questions/6912922/android-how-does-gridview-auto-fit-find-the-number-of-columns/7874011#7874011

The solution is to measure your column size before setting the GridView's column width. Here is a quick way to measure Views offscreen:
(where cell is the specific grid cell in the GridView to measure
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


    /*
    Interface that All activities hosting this fragment must implement
    i.e Container Activity must implement this interface

    Modelled on https://developer.android.com/guide/components/fragments.html#CommunicatingWithActivity
     */
    public interface OnMovieSelectedListener {
        public void OnMovieSelected(Uri movieUri);
    }

    /*
    When this fragment is attached to the activity, ensure that it implements onMovieSelectedListener interface
    and assign my local version to be the activity    See:
    https://developer.android.com/guide/components/fragments.html#CommunicatingWithActivity

    May have to change this to onAttach(Context context)
    and check if context is an activity I'm not sure if this will work on older versions of Android though
    see    http://stackoverflow.com/questions/32083053/android-fragment-onattach-deprecated
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            movieListener = (OnMovieSelectedListener) activity; //casts the attached Activity into a movieListener
        } catch (ClassCastException e) { //ensure that the attached Activity implements the proper callback interface
            throw new ClassCastException(activity.toString() + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.preferences, false); //trying to set default values for all of app
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);  //inflate the fragment view

        //make a new MovieAdapter (cursor adapter)
        movieAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        // Get a reference to the gridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        //set adapter to GridView
        gridView.setAdapter(movieAdapter); //my custom adapter


        /*
        Listens for grid clicks
        Now it calls containing activity (which implements the OnMovieSelectedListener interface
        and calls the conatining activity and passes it the URI so that the Activity can then pass the information
        onto the detail fragment itself

        Old way -  to make a specific intent with the detailMovieUri in it
        and add that to the intent with intent.setData(detailURI)
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int gridItemClicked, long gridItemRowId) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor selectedMoviecursor = (Cursor) adapterView.getItemAtPosition(gridItemClicked);

                if (selectedMoviecursor != null) {
                    Long detailMovieDatabaseID =selectedMoviecursor.getLong(COL_MOVIE_ID); //get the database _ID of the movie clicked
                    Uri detailMovieUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(detailMovieDatabaseID); //make the detail query URI

                    movieListener.OnMovieSelected(detailMovieUri);//instead pass the URI to containing activity
                    // which will then pass it on to the detail activity or fragment depending on the layout

                }
            }
        });



      //  sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext()); //initializing sharedPref with the defaults
       // prefListener = new MyPreferenceChangeListener();
       // sharedPref.registerOnSharedPreferenceChangeListener(prefListener); //registering the listener

      //////////////this should be done in Main Activity
      //  updateDatabaseFromAPI(); //update the entire Grid from internet - when Fragment created
//would deleting this cause issues? SHould the listener crap be put on when activity is created?


        return rootView;
    }



    //put this in Main Activity?

    /**
     * My Own OnSharedPreferenceChangeListener
     * Put on it's own for easier debugging
     * updates Movie Images if movie sort order is changed
     */
   /* private class MyPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (isAdded()) { //just makes sure that fragment is attached to an Activity
                if (key.equals(getString(R.string.movie_sort_order_key))) {

                    //will have to call API Update and then when done - re-attach load the movies from grid

//// perhaps this should also be passed to Main Activity?
                    updateDatabaseFromAPI(); //update the entire Grid from internet when sort order preference is changed
                }
            }
        }
    }
*/


    //////////////////////////////Initialize Loader with Loader Manager /////////////////////////////////
    //////////////////////////////Step 3/3 to create a Cursor Loader //////////////////////
    // Must be in onCreate in Activity or onActivityCreated in Fragment
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);  //Loader ID, optional Bundle, Object that implements the loader callbacks
        super.onActivityCreated(savedInstanceState);
    }


    ////////////////////////////////////Loader Call back methods needed to implement CursorLoader //////////////////
    /////////////////Step 2/3 to create a CursorLoader ////////////////////////////////////////////

    //Returns a cursor Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by _ID - the order they were put into database.
        String sortOrder = MovieContract.MovieEntry._ID + " ASC";

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI, //get entire table back
                MOVIE_COLUMNS, //the projection - very important to have this!
                null,
                null,
                sortOrder);
    }

    //update UI once data is ready
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        //add any other UI updates here that are needed once data is ready
    }

    //Clean up when loader destroyed. Don't have Cursor Adapter pointing at any data
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }






    /**
     * to connect to network and load images into
     * Database
     * (not the gridview)
     * the gridView
     *
     * This should be put into Api Utilities - can be self contained
     */
    private void updateMovieGridImages() {

        final String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key); //to be able to look at sort order preference
        String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

       // URL movieQueryURL = makeMovieQueryURL(movieSortOrder); //old - delete when not needed
        URL movieQueryURL = ApiUtility.makeMovieApiQueryURL(getContext(),movieSortOrder);

        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date

            //LJG ZZZ transferring to new separate class
            // FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask();
            //   movieTask.execute(movieQueryURL);

            //old method with movieAdapter passed in
            // FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask(getActivity(), movieAdapter); //pass in context and movieAdapter
//newer version - no movie adapter will used cursor loader
            FetchMoviesFromApiTask movieTask = new FetchMoviesFromApiTask(getActivity()); //pass in context
            movieTask.execute(movieQueryURL);
        }
    }





    /////////////////////// This will get moved to API utilities
    /////delete this version soon!!!!!
    /**
     * Makes URL to access API to get movie info
     * <p/>
     * movie should now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @return URL for themoviedb.org
     */
    /*private URL makeMovieQueryURL(String sortOrder) {
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
        Log.v(LOG_TAG + "POP MakeMovieQueryURL", "The Query url is "+ url);
        return url;
    }*/
}











