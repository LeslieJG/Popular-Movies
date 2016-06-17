package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * This Displays the movie details including a poster and other text details
 * <p/>
 * Implements a Cursor Loader to provide a cursor (from the database)
 * <p/>
 * Fragment gets the movieQueryUri from the arguments the fragment is created with.
 * Fragement is created in either MainActivity or DetailActivity depending on 2-Pane of 1-Pane view
 * <p/>
 * Will not be using a CursorAdapter as it is only for List/grid views.
 * I will just be displaying one db row worth of data.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Cursor Loader  ID
    private static final int MOVIE_DETAIL_LOADER = 0;

    static final String LOG_TAG = "DETAIL_ACT_FRAGEMENT";
    static final String MOVIE_DETAIL_URI = "MOVIE_DETAIL_URI"; // Movie Detail URI key (for getting arguments from fragment)
    private Uri movieQueryUri; // will hold the Uri for the cursorLoader query

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
    static final int COL_MOVIE_REVIEW_3_AUTHOR = 14;
    static final int COL_MOVIE_VIDEO_1 = 15;
    static final int COL_MOVIE_VIDEO_2 = 16;
    static final int COL_MOVIE_VIDEO_3 = 17;
    /////////////////////////////////////////////////////////


    //////Adding the views
    ///hopefully this will make them retain state
    //LJG don't think I need this
    private ImageView mPosterView;
    private TextView mMovieTitleView;
    private TextView mPlotSynopsisView;
    private TextView mVoteAverageView;
    private TextView mReleaseDateView;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, " OnCreateView called"); //debugging double rotation

        //get the MovieQuery Uri from the fragment itself (it should have been created with one)
        Bundle arguments = getArguments(); //get arguments from when fragement created
        if (arguments != null) { //if there are some arguments
            movieQueryUri = arguments.getParcelable(DetailActivityFragment.MOVIE_DETAIL_URI); //get the movieQuery URI passed in
            //the movieQueryUri of the form content://com.gmail.lgelberger.popularmovies/movie/182
            //and is the Uri to our local content provider to look up movie details in the local database
            //need to make this an API call, so we need to use this URI to look up in the database what the API call ID is!


            //call the Database/API to get the movie reviews/trailers loaded in as well.???  - oR should I load ALL the reviews
            //at the same time? (big cookie model?)
            //for Now just load them here

            //getting URI
            //content://com.gmail.lgelberger.popularmovies/movie/182
            //but thought it would be ...
            ////  http://api.themoviedb.org/3/movie/293660/reviews?api_key=[My API Key]
            //  http://api.themoviedb.org/3/movie/293660/videos?api_key=[My API Key]
            //which is what I need to send to PopularMoviesService


            //The movieQueryUri passed in is ALWAYS going to be content://com.gmail.lgelberger.popularmovies/movie/182.
            // in an AsyncTask, just get cursor from Query, then find the API id from cursor,
            // then onPostExecute - call the update database

            //make a new Async Task for database lookup to find movieAPIidFromDatabase





           // String apiMovieID = ApiUtility.getApiMovieIdFromUri(movieQueryUri);

            //update Reviews and Trailers if needed
            //ApiUtility.updateDatabaseFromApiIfNeeded(getContext(), apiMovieID); //updates Reviews and Trailers into detail view if needed
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false); // the rootview of the Fragement

        //assign all the views
        mPosterView = (ImageView) rootView.findViewById(R.id.imageview_poster_thumbnail);
        mMovieTitleView = ((TextView) rootView.findViewById(R.id.textview_title));
        mPlotSynopsisView = ((TextView) rootView.findViewById(R.id.textview_plot_synopsis));
        mVoteAverageView = ((TextView) rootView.findViewById(R.id.textview_user_rating));
        mReleaseDateView = ((TextView) rootView.findViewById(R.id.textview_release_date));

        if (arguments != null) { //for debugging
            Log.v(LOG_TAG, "In OnCreateView - arguments not null");
        } else {
            Log.v(LOG_TAG, "In OnCreateView - arguments is null");
        }

        if (movieQueryUri != null) //for debugging
        {
            Log.v(LOG_TAG, "In OnCreateView - movieQueryUri not null, it is " + movieQueryUri);
        } else {
            Log.v(LOG_TAG, "In OnCreateView - movieQueryUri is null ");
        }

        // return inflater.inflate(R.layout.fragment_detail, container, false); //old
        return rootView;
    }


    // From http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //perhaps keep a copy

        Log.v(LOG_TAG, "In onActivityCreated - movieQueryUri is currently " + movieQueryUri);

        //Initialize the Loader with a LoaderManager  - Arguments - Loader ID, Bundle, Class that implements callback method
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }


    //returns a cursor loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, " onCreateLoader, movieQueryUri = " + movieQueryUri);

        //only return cursor if the query URI was passed in - if no URI passed in, do nothing
        // Bundle arguments = getArguments(); //get arguments from when fragment created
        if (movieQueryUri == null) { //if there are no arguments -->>>>>There ARE argument, just the Query is not set yet?
            Log.v(LOG_TAG, "onCreateLoader, movieQueryUri is NULL!!!!!!! -No Cursor Loader Created"); //debugging
            return null;
        } else { //movieQueryUri is not null
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            Log.v(LOG_TAG, "onCreateLoader, movieQueryUri is not Null - Creating New Cursor Loader");

            return new CursorLoader(
                    getActivity(), //context
                    // intent.getData(),  // Query URI
                    movieQueryUri, //Uri retrieved from fragment argument
                    MOVIE_COLUMNS, //projection
                    null,
                    null,
                    null //sort order - just one movie, so no need to sort
            );
        }
    }


    //do all UI updates here!
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieDetailCursor) {
        Log.v(LOG_TAG, "In onLoadFinished - Method Called");

        if (movieDetailCursor == null) {
            Log.v(LOG_TAG, "onload finished - Movie Cursor is NULL");
        } else {
            Log.v(LOG_TAG, "onload finished - Movie Cursor is not Null");
        }

        if (movieDetailCursor.moveToFirst()) {
            Log.v(LOG_TAG, "onload finished - Movie cursor has values (not Empty)");
        } else {
            Log.v(LOG_TAG, "onload finished - Movie cursor is empty");
        }

        //DO it the other way - it data then load views
        if (movieDetailCursor != null && movieDetailCursor.moveToFirst()) { //if  is not empty and exists
            //  if(movieDetailCursor != null ){ //if  is not empty and exists
            Log.v(LOG_TAG, "In OnLoadFinished - updating UI from Cursor");

            ///////////////////////////////////////////////////////////////////
            //Update UI
            Context detailContext = getActivity();// getContext();

            //get data from Cursor
            String movieURL = movieDetailCursor.getString(COL_MOVIE_POSTER_URL);

            //  Picasso.with(detailContext).load(movieURL).into((ImageView) posterThumbnailView);
            //Allow picasso to deal with errors - some databases will time out
            Picasso.with(detailContext)
                    .load(movieURL)
                    //.placeholder(R.drawable.placeholder) //put a placeholder in place of image while it is loading
                    .placeholder(R.drawable.placeholder_error_vertical) //put a placeholder in place of image while it is loading
                    .error(R.drawable.placeholder_error_vertical) //put a picture if there is an error retrieving file
                    .into((ImageView) mPosterView);

            mMovieTitleView.setText(movieDetailCursor.getString(COL_MOVIE_TITLE));
            mPlotSynopsisView.setText(movieDetailCursor.getString(COL_PLOT_SYNOPSIS));
            mVoteAverageView.setText(movieDetailCursor.getString(COL_VOTE_AVERAGE));
            mReleaseDateView.setText(movieDetailCursor.getString(COL_RELEASE_DATE));
        }
    }

    //nothing to do here. No Cursor Adapter to swap cursor
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " onLoaderReset");
    }









    /**
     * Takes in the database URI
     *
     *
     */
    private class getMovieApiIDTask extends AsyncTask<Uri, Void, String> {






        @Override
        protected String doInBackground(Uri... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }


    }
}

