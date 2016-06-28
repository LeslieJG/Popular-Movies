package com.gmail.lgelberger.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;

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
 * <p/>
 * Implementing Multiple Button Click listener as per
 * http://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    // Cursor Loader  ID
    private static final int MOVIE_DETAIL_LOADER = 0;

    static final String LOG_TAG = "DETAIL_ACT_FRAGEMENT";
    static final String MOVIE_DETAIL_URI = "MOVIE_DETAIL_URI"; // Movie Detail URI key (for getting arguments from fragment)
    private Uri movieQueryUri; // will hold the Uri for the cursorLoader query

    private Cursor mCursor;

    //////Adding the views
    // General Movie information views
    private ImageView mPosterView;
    private TextView mMovieTitleView;
    private TextView mPlotSynopsisView;
    private TextView mVoteAverageView;
    private TextView mReleaseDateView;

    private Button mAddToFavouritesButton;
    final static int BUTTON_ADD_TO_REVIEWS = 200;

    //Review views
    LinearLayout mReviewsContainer;
    private TextView mReviewAuthor_1;
    private TextView mReview_1;
    private TextView mReviewAuthor_2;
    private TextView mReview_2;
    private TextView mReviewAuthor_3;
    private TextView mReview_3;
    private static final int REVIEW_1_CONTENT_ID = 900;
    private static final int REVIEW_2_CONTENT_ID = 901;
    private static final int REVIEW_3_CONTENT_ID = 902;

    //Reviews Layout Containers
    private LinearLayout mReviewLayout1;
    private LinearLayout mReviewLayout2;
    private LinearLayout mReviewLayout3;
    private static final int REVIEW_1_CONTAINER_ID = 700;
    private static final int REVIEW_2_CONTAINER_ID = 701;
    private static final int REVIEW_3_CONTAINER_ID = 702;

    //Trailer views
    //holding reference to the Video Button Container - to inlfate buttons as needed
    LinearLayout mButtonContainer;
    Button mButtonVideo1;
    Button mButtonVideo2;
    Button mButtonVideo3;
    final static int BUTTON_1_ID = 400;
    final static int BUTTON_2_ID = 401;
    final static int BUTTON_3_ID = 402;

    //For YouTube movie Id keys - to play trailers
    private String mTrailerYoutubeKey1;
    private String mTrailerYoutubeKey2;
    private String mTrailerYoutubeKey3;

    //Titles
    private TextView mTrailersTitle;
    final static int TRAILERS_TITLE_ID = 500;
    private TextView mReviewsTitle;
    final static int REVIEWS_TITLE_ID = 501;

    String[] projection; //to store the appropriate content provider projection, either MOVIE_COLUMNS or FAVOURITE_COLUMNS

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

    //identical to above just with FavouriteEntry column names
    //prehaps make this a part of a projection map at some point?
    private static final String[] FAVOURITE_COLUMNS = {
            MovieContract.FavouriteEntry._ID,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_TITLE,
            MovieContract.FavouriteEntry.COLUMN_API_MOVIE_ID,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_POSTER_URL,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_POSTER,
            MovieContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.FavouriteEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.FavouriteEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_1,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_2,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_3,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_1,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_2,
            MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_3,
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS or FAVOURITE COLUMNS changes, these must change.
    //I'm using the same Indices for both projection string arrays above
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

    //constructor
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.v(LOG_TAG, " OnCreateView called"); //debugging double rotation

        //get the MovieQuery Uri from the fragment itself (it should have been created with one)
        Bundle arguments = getArguments(); //get arguments from when fragment created
        if (arguments != null) { //if there are some arguments
            movieQueryUri = arguments.getParcelable(DetailActivityFragment.MOVIE_DETAIL_URI); //get the movieQuery URI passed in
            //the movieQueryUri of the form
            // content://com.gmail.lgelberger.popularmovies/movie/182  or
            // content://com.gmail.lgelberger.popularmovies/favourite/182
            //and is the Uri to our local content provider to look up movie details in the local database

            //decide which content provider URI we are using and set the projections accordingly
            List<String> uriPathSegments = movieQueryUri.getPathSegments();
            if (uriPathSegments.contains(MovieContract.PATH_FAVOURITE)) {
                projection = FAVOURITE_COLUMNS;
            } else if (uriPathSegments.contains(MovieContract.PATH_MOVIE)) {
                projection = MOVIE_COLUMNS;
            } else {
                Log.e(LOG_TAG, "invalid Detail URI passed in as argument");
            }
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false); // the rootview of the Fragment

        //assign all the general detail views
        mPosterView = (ImageView) rootView.findViewById(R.id.imageview_poster_thumbnail);
        mMovieTitleView = ((TextView) rootView.findViewById(R.id.textview_title));
        mPlotSynopsisView = ((TextView) rootView.findViewById(R.id.textview_plot_synopsis));
        mVoteAverageView = ((TextView) rootView.findViewById(R.id.textview_user_rating));
        mReleaseDateView = ((TextView) rootView.findViewById(R.id.textview_release_date));

        mAddToFavouritesButton = ((Button) rootView.findViewById(R.id.favourites_button));
        mAddToFavouritesButton.setOnClickListener(this);
        mAddToFavouritesButton.setId(BUTTON_ADD_TO_REVIEWS);

        //Trailer and Reviews Containers
        mButtonContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_video_button_container);
        mReviewsContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_reviews_container);

        //Trailers Title
        mTrailersTitle = new TextView(getContext());
        LinearLayout.LayoutParams textViewTitleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons
        mTrailersTitle.setLayoutParams(textViewTitleParams);
        mTrailersTitle.setTypeface(null, Typeface.BOLD);
        mTrailersTitle.setId(TRAILERS_TITLE_ID);
        mTrailersTitle.setText("Trailers");

        //Reviews Title
        mReviewsTitle = new TextView(getContext());
        mReviewsTitle.setLayoutParams(textViewTitleParams);
        mReviewsTitle.setTypeface(null, Typeface.BOLD);
        mReviewsTitle.setId(REVIEWS_TITLE_ID);
        mReviewsTitle.setText("Reviews");

        //Define the buttons
        mButtonVideo1 = new Button(getContext());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons
        mButtonVideo1.setLayoutParams(buttonParams);
        mButtonVideo1.setText("Play Trailer #1");
        mButtonVideo1.setId(BUTTON_1_ID);
        mButtonVideo1.setOnClickListener(this);  //this fragment implements onClickListener

        mButtonVideo2 = new Button(getContext());
        mButtonVideo2.setLayoutParams(buttonParams);
        mButtonVideo2.setText("Play Trailer #2");
        mButtonVideo2.setId(BUTTON_2_ID);
        mButtonVideo2.setOnClickListener(this);  //this fragment implements onClickListener

        mButtonVideo3 = new Button(getContext());
        mButtonVideo3.setLayoutParams(buttonParams);
        mButtonVideo3.setText("Play Trailer #3");
        mButtonVideo3.setId(BUTTON_3_ID);
        mButtonVideo3.setOnClickListener(this);  //this fragment implements onClickListener

        //Reviews
        mReviewAuthor_1 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_1 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReviewAuthor_2 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_2 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReviewAuthor_3 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_3 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_1.setId(REVIEW_1_CONTENT_ID);
        mReview_2.setId(REVIEW_2_CONTENT_ID);
        mReview_3.setId(REVIEW_3_CONTENT_ID);

        //Individual Reviews Containers.
        mReviewLayout1 = new LinearLayout(getContext());
        mReviewLayout2 = new LinearLayout(getContext());
        mReviewLayout3 = new LinearLayout(getContext());

        //first one is width, height
        LinearLayout.LayoutParams reviewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons
        mReviewLayout1.setLayoutParams(reviewParams);
        mReviewLayout1.setId(REVIEW_1_CONTAINER_ID);
        mReviewLayout1.setOrientation(LinearLayout.HORIZONTAL);

        mReviewLayout2.setLayoutParams(reviewParams);
        mReviewLayout2.setId(REVIEW_2_CONTAINER_ID);
        mReviewLayout2.setOrientation(LinearLayout.HORIZONTAL);

        mReviewLayout3.setLayoutParams(reviewParams);
        mReviewLayout3.setId(REVIEW_3_CONTAINER_ID);
        mReviewLayout3.setOrientation(LinearLayout.HORIZONTAL);

        // return inflater.inflate(R.layout.fragment_detail, container, false); //old
        return rootView;
    }


    // From http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Log.v(LOG_TAG, "In onActivityCreated - movieQueryUri is currently " + movieQueryUri);

        //Initialize the Loader with a LoaderManager  - Arguments - Loader ID, Bundle, Class that implements callback method
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }


    //returns a cursor loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.v(LOG_TAG, " onCreateLoader, movieQueryUri = " + movieQueryUri);

        //only return cursor if the query URI was passed in - if no URI passed in, do nothing
        // Bundle arguments = getArguments(); //get arguments from when fragment created
        if (movieQueryUri == null) { //if there are no arguments -->>>>>There ARE argument, just the Query is not set yet?
            //Log.v(LOG_TAG, "onCreateLoader, movieQueryUri is NULL!!!!!!! -No Cursor Loader Created"); //debugging
            return null;
        } else { //movieQueryUri is not null
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            //Log.v(LOG_TAG, "onCreateLoader, movieQueryUri is not Null - Creating New Cursor Loader");

            return new CursorLoader(
                    getActivity(), //context
                    movieQueryUri, //Uri retrieved from fragment argument
                    projection, //projection depended on type of URI above
                    null,
                    null,
                    null //sort order - just one movie, so no need to sort
            );
        }
    }


    //do all UI updates here!
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieDetailCursor) {
        //Log.v(LOG_TAG, "In onLoadFinished - Method Called");

        if (movieDetailCursor != null && movieDetailCursor.moveToFirst()) { //if  is not empty and exists
            //Log.v(LOG_TAG, "In OnLoadFinished - updating UI from Cursor");

            // get a copy of the cursor for making Content Values later on
            mCursor = movieDetailCursor;

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

            //get the Movie Reviews
            mReviewAuthor_1.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_1_AUTHOR));
            mReview_1.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_1));
            mReviewAuthor_2.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_2_AUTHOR));
            mReview_2.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_2));
            mReviewAuthor_3.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_3_AUTHOR));
            mReview_3.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_3));

            //get the YouTubeKeys for each video
            mTrailerYoutubeKey1 = movieDetailCursor.getString(COL_MOVIE_VIDEO_1);
            mTrailerYoutubeKey2 = movieDetailCursor.getString(COL_MOVIE_VIDEO_2);
            mTrailerYoutubeKey3 = movieDetailCursor.getString(COL_MOVIE_VIDEO_3);

            //TRAILERS
            //Add the Trailers Title if there are trailers and it hasn't been added yet
            if (mTrailerYoutubeKey1 != null && mTrailersTitle != mButtonContainer.findViewById(TRAILERS_TITLE_ID)) {
                //   mButtonContainer.addView(horiztonalThickLine());
                mButtonContainer.addView(mTrailersTitle); //only add trailers title if there are trailers and it has not been added before
            }

            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey1 != null && mButtonVideo1 != mButtonContainer.findViewById(BUTTON_1_ID)) {
                mButtonContainer.addView(mButtonVideo1);
            }
            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey2 != null && mButtonVideo2 != mButtonContainer.findViewById(BUTTON_2_ID)) {
                mButtonContainer.addView(mButtonVideo2);
            }

            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey3 != null && mButtonVideo3 != mButtonContainer.findViewById(BUTTON_3_ID)) {
                mButtonContainer.addView(mButtonVideo3);
            }

            //Review Strings for testing if any reviews available
            String reviewString1 = movieDetailCursor.getString(COL_MOVIE_REVIEW_1);
            String reviewString2 = movieDetailCursor.getString(COL_MOVIE_REVIEW_2);
            String reviewString3 = movieDetailCursor.getString(COL_MOVIE_REVIEW_3);

            //REVIEWS
            //Only add Header title if there are reviews, and if title hasn't already been added
            if (reviewString1 != null && mReviewsTitle != mReviewsContainer.findViewById(REVIEWS_TITLE_ID)) {
                mReviewsContainer.addView(horiztonalLine());
                mReviewsContainer.addView(mReviewsTitle);
            }

            //only load 1st review if there is a review, and if the review content hasn't been loaded into container yet
            if (reviewString1 != null && mReview_1 != mReviewsContainer.findViewById(REVIEW_1_CONTENT_ID)) {
                mReviewLayout1.addView(author());
                mReviewLayout1.addView(mReviewAuthor_1);

                mReviewsContainer.addView(mReviewLayout1); //added the Review Author with title
                mReviewsContainer.addView(mReview_1); //added the actual review
            }

            //only load 2nd review if there is a review, and if the review content hasn't been loaded into container yet
            if (reviewString2 != null && mReview_2 != mReviewsContainer.findViewById(REVIEW_2_CONTENT_ID)) {
                mReviewsContainer.addView(horiztonalLine());
                mReviewLayout2.addView(author());
                mReviewLayout2.addView(mReviewAuthor_2);

                mReviewsContainer.addView(mReviewLayout2); //added the Review Author with title
                mReviewsContainer.addView(mReview_2); //added the actual review
            }

            //only load 3rd review if there is a review, and if the review content hasn't been loaded into container yet
            if (reviewString3 != null && mReview_3 != mReviewsContainer.findViewById(REVIEW_3_CONTENT_ID)) {
                mReviewsContainer.addView(horiztonalLine());
                mReviewLayout3.addView(author());
                mReviewLayout3.addView(mReviewAuthor_3);

                mReviewsContainer.addView(mReviewLayout3); //added the Review Author with title
                mReviewsContainer.addView(mReview_3); //added the actual review
            }
        }
    }

    //nothing to do here. No Cursor Adapter to swap cursor
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " onLoaderReset");
    }


    //helper method to build YouTube Uri for a given movie
    private Uri buildYouTubeUri(String youTubeMovieIdKey) {

        //example http://www.youtube.com/watch?v=cxLG2wtE7TM

        Uri youTubeUri = Uri.parse(getContext().getString(R.string.video_query_url_base)).buildUpon() //
                .appendPath(getContext().getString(R.string.video_query_url_watch)) //watch
                .appendQueryParameter(getContext().getString(R.string.video_query_key), youTubeMovieIdKey)
                .build();
        return youTubeUri;
    }

    /*
    Needed to over ride as Fragment implements onClickListener
    Allows one Listener for many buttons
    As described in :
    http://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case BUTTON_1_ID:
                //  mButtonVideo1.setText("Clicked Button 1");
                Intent youTubeIntent1 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey1));
                startActivity(youTubeIntent1);
                break;

            case BUTTON_2_ID:
                // mButtonVideo2.setText("Clicked Button 2");
                Intent youTubeIntent2 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey2));
                startActivity(youTubeIntent2);
                break;

            case BUTTON_3_ID:
                // mButtonVideo3.setText("Clicked Button 3");
                Intent youTubeIntent3 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey3));
                startActivity(youTubeIntent3);
                break;

            case BUTTON_ADD_TO_REVIEWS:
                // Toast.makeText(getContext(), "Pressed Add to reviews Button", Toast.LENGTH_LONG).show();

                //for now check to make sure that sort order is not favourites
                if (!isFavouriteDetailMovieDisplayed(movieQueryUri)) { //if the movie is not from favourites
                    //add it to favourites database

                    ContentValues movieTotalContentValues = new ContentValues(); //for holding content values taken from cursor for detail display
                    if (mCursor.moveToFirst()) {   //moving cursor to first - and confirming it has data
                        // Log.v(LOG_TAG, "converting cursor to content values soon ---- cursor is valid");
                    } else {
                        // Log.v(LOG_TAG, "cursor is not valid, can't be converted to content values");
                        return; //no cursor, no data - do nothing
                    }

                    DatabaseUtils.cursorRowToContentValues(mCursor, movieTotalContentValues);//put the contents of the cursor into content Values

                    //strip out the _ID tag so that the _ID tag will be created by favourites database
                    movieTotalContentValues.remove(MovieContract.FavouriteEntry._ID);

                    //then insert the row into favourites database table to store it in favourites
                    if (movieTotalContentValues != null) { //if there are content values to store
                        getContext().getContentResolver().insert(MovieContract.FavouriteEntry.CONTENT_URI,
                                movieTotalContentValues); //insert into favourite table

                        // Log.v(LOG_TAG, "Review Button Pressed and added to Favourites");
                    }
                }
                break;
            default: //just in case I want to add some default behaviour later
                break;
        }

    }


    ////////////////////////////////Private Helper Methods/////////////////////////////////////


    /*
    Helper method to convert dp to pixels. Needed for programmatically setting layout heights in pixels

    Inspiration from: http://stackoverflow.com/questions/5959870/programatically-set-height-on-layoutparams-as-density-independent-pixels
    and
    http://stackoverflow.com/questions/7793436/give-padding-with-setpadding-with-dip-unit-not-px-unit
     */
    private int getPixelsFromDip(int dip) {
        int pixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                getResources().getDisplayMetrics()
        );
        // Toast.makeText(getContext(), "Converting " + dip + " dp to " + pixels +" pixels", Toast.LENGTH_LONG).show();
        return pixels;
    }


    //For returning a Horizontal Line - 1dp
    private View horiztonalLine() {
        View horizontalLine = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getPixelsFromDip(1));
        params.setMargins(0, getPixelsFromDip(16), 0, getPixelsFromDip(16)); //left, top, right, bottom
        horizontalLine.setLayoutParams(params);
        horizontalLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorDivider));

        return horizontalLine;
    }


    //For returning a textview with Author in it
    private TextView author() {
        TextView authorTextView = new TextView(getContext());
        authorTextView.setText("Author:    ");
        return authorTextView;
    }


    //Temporary helper method
    //for determening if the incoming DetailActivityFragment incoming URI is to display from the favourites database table
    private boolean isFavouriteDetailMovieDisplayed(Uri movieQueryUriPassedIn) {
        List<String> uriPathSegments = movieQueryUriPassedIn.getPathSegments();
        if (uriPathSegments.contains(MovieContract.PATH_FAVOURITE)) { //don't need to update favourites
            return true;
        }
        return false;  //otherwise return false
    }
}

