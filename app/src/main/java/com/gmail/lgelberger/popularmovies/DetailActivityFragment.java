package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 * <p>
 * This Displays the movie details including a poster and other text details
 * The
 * <p>
 * Going to implement a Cursor Loader to provide a cursor (from the database)
 * The query URI will be provided by an intent from Main Activity Fragment
 * The cursor loader will monitor changes in data
 * <p>
 * Will not be using a CursorAdapter as it is only for List/grid views.
 * I will just be displaying one db row worth of data.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Cursor Loader  ID
    private static final int MOVIE_DETAIL_LOADER = 0;

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


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }



    // From http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize the Loader with a LoaderManager
        //Arguments - Loader ID, Bundle, Class that implements callback method
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }


    //returns a cursor loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent(); //get intent that is passed to DetailActivity
        if (intent == null || intent.getData() == null) { //if nothing in intent OR if no URI in intent data
            // (i.e. not created from an intent) - (like a 2 pane layout) where the fragment is created directly
            // , do not return a cursor loader
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(), //context
                intent.getData(),  // Query URI
                MOVIE_COLUMNS, //projection
                null,
                null,
                null //sort order - just one movie, so no need to sort
        );
    }


    //do all UI updates here!
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieDetailCursor) {
        //   Log.v(LOG_TAG, "In onLoadFinished");

        if (!movieDetailCursor.moveToFirst()) {
            return;
        } //if no data in cursor do nothing

        //getting information from intent once the activity is created (after activity and fragment are created)
        //  ZZZOLDMovieDataProvider movieDetails = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_details_intent_key));

        Context detailContext = getActivity();// getContext();

        //get data from Cursor
        String movieURL = movieDetailCursor.getString(COL_MOVIE_POSTER_URL);
        View posterThumbnailView = getActivity().findViewById(R.id.imageview_poster_thumbnail);

        //  Picasso.with(detailContext).load(movieURL).into((ImageView) posterThumbnailView);
        //Allow picasso to deal with errors - some databases will time out
        Picasso.with(detailContext)
                .load(movieURL)
                //.placeholder(R.drawable.placeholder) //put a placeholder in place of image while it is loading
                .placeholder(R.drawable.placeholder_error_vertical) //put a placeholder in place of image while it is loading
                .error(R.drawable.placeholder_error_vertical) //put a picture if there is an error retrieving file
                .into((ImageView) posterThumbnailView);


        ((TextView) getActivity().findViewById(R.id.textview_title)).setText(movieDetailCursor.getString(COL_MOVIE_TITLE));
        ((TextView) getActivity().findViewById(R.id.textview_plot_synopsis)).setText(movieDetailCursor.getString(COL_PLOT_SYNOPSIS));
        ((TextView) getActivity().findViewById(R.id.textview_user_rating)).setText(movieDetailCursor.getString(COL_VOTE_AVERAGE));
        ((TextView) getActivity().findViewById(R.id.textview_release_date)).setText(movieDetailCursor.getString(COL_RELEASE_DATE));
    }

    //nothing to do here. No Cursor Adapter to swap cursor
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
