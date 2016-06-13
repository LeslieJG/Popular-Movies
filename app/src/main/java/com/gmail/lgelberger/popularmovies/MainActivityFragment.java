package com.gmail.lgelberger.popularmovies;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gmail.lgelberger.popularmovies.data.MovieContract;

/**
 * Fragment containing the gridview of Movies displayed from the local database
 * <p/>
 * Implementing a Cursor Loader to provide a cursor (from the database)
 * Using a CursorAdapter  for grid views.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    MovieCursorAdapter movieAdapter; // declare my custom CursorAdapter

    OnMovieSelectedListener movieListener; //refers to the containing activity of this fragment.
    // We will pass the selected movie Uri through this listener to the containing activity to deal with

    int mGridItemSelected = GridView.INVALID_POSITION; //to hold current position to place grid at proper position after rotation
    private final String SELECTED_KEY = "selected_position"; //key for storing mGridItemSelected into savedInstanceState
    GridView gridView = null; //making a reference to the GridView

    int mGridItemFirstVisiblePosition = GridView.INVALID_POSITION; //trying to maintain scroll state after rotation when no selection
    private final String FIRST_VISIBLE_POSITION_KEY = "first_visisble_position";

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging

    //Step 1/3 of making Cursor Loader - Create Loader ID
    private static final int MOVIE_LOADER = 0;

    /////////////////////Database projection constants///////////////
    //For making good use of database Projections specify the columns we need
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
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3,
            MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR,
            MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1,
            MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2,
            MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3,
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_API_MOVIE_ID = 2;
    static final int COL_MOVIE_POSTER_URL = 3;
    static final int COL_MOVIE_POSTER_IMAGE = 4;
    static final int COL_ORIGINAL_TITLE = 5;
    static final int COL_PLOT_SYNOPSIS = 6;
    static final int COL_VOTE_AVERAGE = 7;
    static final int COL_RELEASE_DATE = 8;
    static final int COL_MOVIE_REVIEW_1 = 9;
    static final int COL_MOVIE_REVIEW_1_AUTHOR = 10;
    static final int COL_MOVIE_REVIEW_2 = 11;
    static final int COL_MOVIE_REVIEW_2_AUTHOR = 12;
    static final int COL_MOVIE_REVIEW_3 = 13;
    static final int COL_MOVIE_REVIEW_3_AUTHOR =14;
    static final int COL_MOVIE_VIDEO_1 = 15;
    static final int COL_MOVIE_VIDEO_2 = 16;
    static final int COL_MOVIE_VIDEO_3 = 17;
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

        Log.v(LOG_TAG, "In onCreateView of MainFragment");

        movieAdapter = new MovieCursorAdapter(getActivity(), null, 0); //make a new MovieAdapter (cursor adapter)
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies); // Get a reference to the gridView,
        // and attach this adapter to it.

        gridView.setAdapter(movieAdapter); ////set adapter to GridView


        /*
        Listens for grid clicks
        It calls containing activity (which implements the OnMovieSelectedListener interface)
        and passes it the URI so that the Activity can then pass the information
        onto the detail fragment itself
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int gridItemClicked, long gridItemRowId) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor selectedMoviecursor = (Cursor) adapterView.getItemAtPosition(gridItemClicked);

                if (selectedMoviecursor != null) {
                    Long detailMovieDatabaseID = selectedMoviecursor.getLong(COL_MOVIE_ID); //get the database _ID of the movie clicked
                    Uri detailMovieUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(detailMovieDatabaseID); //make the detail query URI

                    movieListener.OnMovieSelected(detailMovieUri);//pass the URI to containing activity
                    // which will then pass it on to the detail activity or fragment depending on the layout
                }

                mGridItemSelected = gridItemClicked; //set out local member variable with the item clicked id to recover after rotation
                Log.v(LOG_TAG, "Selected Grid Item Number: " + mGridItemSelected);
            }
        });


        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mGridItemSelected = savedInstanceState.getInt(SELECTED_KEY);
            Log.v(LOG_TAG, "Retreived that last GridItem clicked was " + mGridItemSelected);
        }

        //currently not using this for display after rotation - can delete later on
        if (savedInstanceState != null && savedInstanceState.containsKey(FIRST_VISIBLE_POSITION_KEY)) {
            //restore FirstVisisblePosition
            mGridItemFirstVisiblePosition = savedInstanceState.getInt(FIRST_VISIBLE_POSITION_KEY);
            Log.v(LOG_TAG, "Retrieved mGridItemFirstVisiblePosition - it is " + mGridItemFirstVisiblePosition);
        }

        return rootView;
    }

    /*
    need to store grid position that is selected so can recover to that location in the grid after rotation
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "in onSaveInstanceState");

        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mGridItemSelected != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mGridItemSelected);
        }

        if (mGridItemFirstVisiblePosition != GridView.INVALID_POSITION) {
            outState.putInt(FIRST_VISIBLE_POSITION_KEY, mGridItemFirstVisiblePosition);
        }

        super.onSaveInstanceState(outState);
    }

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

        //scroll list back to current selection if gridView exists and mGridItemSelected has a valid position
        if (mGridItemSelected != GridView.INVALID_POSITION && gridView != null) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.clearFocus();
            gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridView.requestFocusFromTouch();
                    gridView.setSelection(mGridItemSelected);
                    gridView.requestFocus();
                }
            });
        }

        //I've left these here in case I want to use them. They work as well
        // gridView.smoothScrollToPositionFromTop(mGridItemSelected, 0); //this DOES work

         /*   gridView.post(new Runnable() {
                @Override
                public void run() {
                  //  gridView.smoothScrollToPosition(mGridItemFirstVisiblePosition); //works
                    //above works

                    //this combination below works works
                   // gridView.setItemChecked(mGridItemSelected, true);
                   // gridView.smoothScrollToPosition(mGridItemSelected);
                }
            });*/
    }


    //Clean up when loader destroyed. Don't have Cursor Adapter pointing at any data
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }


}








