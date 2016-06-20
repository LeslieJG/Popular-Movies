package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * This Displays the movie details including a poster and other text details
 * <p>
 * Implements a Cursor Loader to provide a cursor (from the database)
 * <p>
 * Fragment gets the movieQueryUri from the arguments the fragment is created with.
 * Fragement is created in either MainActivity or DetailActivity depending on 2-Pane of 1-Pane view
 * <p>
 * Will not be using a CursorAdapter as it is only for List/grid views.
 * I will just be displaying one db row worth of data.
 * <p>
 * Implementing Multiple Button Click listener as per
 * http://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    // Cursor Loader  ID
    private static final int MOVIE_DETAIL_LOADER = 0;

    static final String LOG_TAG = "DETAIL_ACT_FRAGEMENT";
    static final String MOVIE_DETAIL_URI = "MOVIE_DETAIL_URI"; // Movie Detail URI key (for getting arguments from fragment)
    private Uri movieQueryUri; // will hold the Uri for the cursorLoader query

    // private static final int BUTTON_1_ID = 1;

    //////Adding the views
    // General Movie information views
    private ImageView mPosterView;
    private TextView mMovieTitleView;
    private TextView mPlotSynopsisView;
    private TextView mVoteAverageView;
    private TextView mReleaseDateView;

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
    private TextView mReviewsAuthorTitle;
    final static int REVIEWS_AUTHOR_TITLE_ID = 502;

    //Horizontal Dividers
    private View mHorizontalDivider1;
    private View mHorizontalDivider2;
    private View mHorizontalDivider3;
    final static int HORIZONTAL_DIVIDER_1_ID = 600;
    final static int HORIZONTAL_DIVIDER_2_ID = 601;
    final static int HORIZONTAL_DIVIDER_3_ID = 602;

    //Blank text line - so I don't have to add a line to my textviews
    private TextView newLine;

    Resources mresources;

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


    //Testing Review Views
    /*LinearLayout mTestReviewView1;
    LinearLayout mTestReviewView2;
    LinearLayout mTestReviewView3;

    static final int    M_TEST_REVIEW_VIEW_1_ID = 800;
    static final int    M_TEST_REVIEW_VIEW_2_ID = 801;
    static final int    M_TEST_REVIEW_VIEW_3_ID = 802;
*/
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
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false); // the rootview of the Fragement

        mresources = getResources();


        //assign all the views
        mPosterView = (ImageView) rootView.findViewById(R.id.imageview_poster_thumbnail);
        mMovieTitleView = ((TextView) rootView.findViewById(R.id.textview_title));
        mPlotSynopsisView = ((TextView) rootView.findViewById(R.id.textview_plot_synopsis));
        mVoteAverageView = ((TextView) rootView.findViewById(R.id.textview_user_rating));
        mReleaseDateView = ((TextView) rootView.findViewById(R.id.textview_release_date));

        //Trailer and Reviews Containers
        mButtonContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_video_button_container);
        mReviewsContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_reviews_container);

        //adding the Reviews
        mReviewAuthor_1 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_1 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReviewAuthor_2 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_2 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReviewAuthor_3 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_3 = new TextView(getContext(), null, R.style.MovieReviewTextTheme);
        mReview_1.setId(REVIEW_1_CONTENT_ID);
        mReview_2.setId(REVIEW_2_CONTENT_ID);
        mReview_3.setId(REVIEW_3_CONTENT_ID);

      /*
        mReviewAuthor_1 = (TextView) rootView.findViewById(R.id.textview_movie_review_1_author);
        mReview_1 = (TextView) rootView.findViewById(R.id.textview_movie_review_1_content);
        mReviewAuthor_2 = (TextView) rootView.findViewById(R.id.textview_movie_review_2_author);
        mReview_2 = (TextView) rootView.findViewById(R.id.textview_movie_review_2_content);
        mReviewAuthor_3 = (TextView) rootView.findViewById(R.id.textview_movie_review_3_author);
        mReview_3 = (TextView) rootView.findViewById(R.id.textview_movie_review_3_content);


*/


        LinearLayout.LayoutParams textViewTitleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons

        //Trailers Title
        mTrailersTitle = new TextView(getContext());
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

        /*mReviewsAuthorTitle = new TextView(getContext());
        mReviewsAuthorTitle.setText("Author:   ");
*/
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons

        //Define the buttons
        mButtonVideo1 = new Button(getContext());
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

        //Setup the horizontal divider
        /*mHorizontalDivider1 = new View(getContext(), null, R.style.HorizontalDividerStyle);
        mHorizontalDivider1.setId(HORIZONTAL_DIVIDER_1_ID);

        mHorizontalDivider2 = new View(getContext(), null, R.style.HorizontalDividerStyle);
        mHorizontalDivider2.setId(HORIZONTAL_DIVIDER_2_ID);

        mHorizontalDivider3 = new View(getContext(), null, R.style.HorizontalDividerStyle);
        mHorizontalDivider3.setId(HORIZONTAL_DIVIDER_3_ID);*/

       // mHorizontalDivider1 = inflater.inflate(R.layout.horizontal_divider, null);

       /* LayoutInflater testInflator = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        mHorizontalDivider1 = testInflator.inflate(R.layout.horizontal_divider, null);*/

       /* mHorizontalDivider1 = new View (getContext());
        mHorizontalDivider1.*/



        /*View rootView = inflater.inflate(R.layout.fragment_detail, container, false); // the rootview of the Fragement
        View view = inflater.inflate(R.layout.new_layout,null);
*/
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


        newLine = new TextView(getContext());
        newLine.setText("\n"); //should be a new line



        //LJG ZZZ My test
        /*mTestReviewView1 =  rootView.findViewById(R.id.single_review_layout);
        //mTestReviewView1.setId(M_TEST_REVIEW_VIEW_1_ID);*/


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
/*
        if (movieDetailCursor == null) {
            Log.v(LOG_TAG, "onload finished - Movie Cursor is NULL");
        } else {
            Log.v(LOG_TAG, "onload finished - Movie Cursor is not Null");
        }

        if (movieDetailCursor.moveToFirst()) {
            Log.v(LOG_TAG, "onload finished - Movie cursor has values (not Empty)");
        } else {
            Log.v(LOG_TAG, "onload finished - Movie cursor is empty");
        }*/

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


            //inflate Buttons and Reviews only if they exists

            //get the Movie Reviews
            mReviewAuthor_1.setText("Author:    " + movieDetailCursor.getString(COL_MOVIE_REVIEW_1_AUTHOR ) + "\n");
            mReview_1.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_1));
            mReviewAuthor_2.setText("Author:    " + movieDetailCursor.getString(COL_MOVIE_REVIEW_2_AUTHOR ) + "\n");
            mReview_2.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_2));
            mReviewAuthor_3.setText("Author:    " + movieDetailCursor.getString(COL_MOVIE_REVIEW_3_AUTHOR ) + "\n");
            mReview_3.setText(movieDetailCursor.getString(COL_MOVIE_REVIEW_3));


            //get the YouTubeKeys for each video
            mTrailerYoutubeKey1 = movieDetailCursor.getString(COL_MOVIE_VIDEO_1);
            mTrailerYoutubeKey2 = movieDetailCursor.getString(COL_MOVIE_VIDEO_2);
            mTrailerYoutubeKey3 = movieDetailCursor.getString(COL_MOVIE_VIDEO_3);

            //try inflating buttons here

            //TRAILERS
            //Add the Trailers Title if there are trailers and it hasn't been added yet
            if (mTrailerYoutubeKey1 != null && mTrailersTitle != mButtonContainer.findViewById(TRAILERS_TITLE_ID)) {
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


            //REVIEWS
            //Only add Header title if there are reviews, and if title hasn't already been added
            if (mReview_1 != null && mReviewsTitle != mReviewsContainer.findViewById(REVIEWS_TITLE_ID)) {
                mReviewsContainer.addView(mReviewsTitle);
            }

            //only load 1st review if there is a review, and if the review content hasn't been loaded yet
            if (mReview_1 != null && mReview_1 != mReviewsContainer.findViewById(REVIEW_1_CONTENT_ID)) {
                mReviewsContainer.addView(mReviewAuthor_1); //added the Review Author with title
                mReviewsContainer.addView(mReview_1); //added the actual review
            }


            //only load 2nd review if there is a review, and if the review content hasn't been loaded yet
            if (mReview_2 != null && mReview_2 != mReviewsContainer.findViewById(REVIEW_2_CONTENT_ID)) {
                mReviewsContainer.addView(horiztonalLine());
                mReviewsContainer.addView(mReviewAuthor_2); //added the Review Author with title
                mReviewsContainer.addView(mReview_2); //added the actual review
            }

//only load 3rd review if there is a review, and if the review content hasn't been loaded yet
            if (mReview_3 != null && mReview_3 != mReviewsContainer.findViewById(REVIEW_3_CONTENT_ID)) {
                mReviewsContainer.addView(horiztonalLine());
                mReviewsContainer.addView(mReviewAuthor_3); //added the Review Author with title
                mReviewsContainer.addView(mReview_3); //added the actual review
            }




/*

            //only load 2ndt review if there is a review, and if the review content hasn't been loaded yet
            if (mReview_2 != null && mReview_2 != mReviewsContainer.findViewById(REVIEW_2_CONTENT_ID)) {
                mReviewLayout2.addView(mHorizontalDivider1);
                mReviewLayout2.addView(mReviewsAuthorTitle); //adding "Author:  "
                mReviewLayout2.addView(mReviewAuthor_2); //adding the actual author
                mReviewLayout2.addView(newLine);

                mReviewsContainer.addView(mReviewLayout2); //added the Review Author with title
                mReviewsContainer.addView(mReview_2); //added the actual review
            }

*/


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
                // do your code
                //  mButtonVideo1.setText("Clicked Button 1");
                Intent youTubeIntent1 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey1));
                startActivity(youTubeIntent1);
                break;

            case BUTTON_2_ID:
                // do your code
                // mButtonVideo2.setText("Clicked Button 2");
                Intent youTubeIntent2 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey2));
                startActivity(youTubeIntent2);
                break;

            case BUTTON_3_ID:
                // do your code
                //   mButtonVideo3.setText("Clicked Button 3");
                Intent youTubeIntent3 = new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey3));
                startActivity(youTubeIntent3);
                break;

            default:
                break;
        }

    }


    ////http://stackoverflow.com/questions/5959870/programatically-set-height-on-layoutparams-as-density-independent-pixels
    //or
    //http://stackoverflow.com/questions/7793436/give-padding-with-setpadding-with-dip-unit-not-px-unit
    //int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1 , getResources().getDisplayMetrics());
    private int getPixelsFromDip (int dip){
        //http://stackoverflow.com/questions/5959870/programatically-set-height-on-layoutparams-as-density-independent-pixels
      //  int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1 , getResources().getDisplayMetrics());

        int pixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                getResources().getDisplayMetrics()
        );

        Toast.makeText(getContext(), "COnverting " + dip + " dp to " + pixels +" pixels", Toast.LENGTH_LONG).show();
        return pixels;
    }


    //For returning a Horizontal Line
    private View horiztonalLine (){
        View horizontalLine = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getPixelsFromDip(1));
        params.setMargins(0, getPixelsFromDip(16), 0, getPixelsFromDip(16)); //left, top, right, bottom
        horizontalLine.setLayoutParams(params);
        horizontalLine.setBackgroundColor(ContextCompat.getColor( getContext(),R.color.colorDivider));


        return  horizontalLine;
    }

    /**
     * Takes in the database URI
     * <p>
     * NOT being used right now
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

