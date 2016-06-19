package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 *
 * Implementing Multiple Button Click listener as per
 * http://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
 *
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    // Cursor Loader  ID
    private static final int MOVIE_DETAIL_LOADER = 0;

    static final String LOG_TAG = "DETAIL_ACT_FRAGEMENT";
    static final String MOVIE_DETAIL_URI = "MOVIE_DETAIL_URI"; // Movie Detail URI key (for getting arguments from fragment)
    private Uri movieQueryUri; // will hold the Uri for the cursorLoader query

   // private static final int BUTTON_1_ID = 1;

        //////Adding the views
    ///hopefully this will make them retain state
    //LJG don't think I need this
    private ImageView mPosterView;
    private TextView mMovieTitleView;
    private TextView mPlotSynopsisView;
    private TextView mVoteAverageView;
    private TextView mReleaseDateView;

    private TextView mReviewAuthor_1;
    private TextView mReview_1;
    private TextView mReviewAuthor_2;
    private TextView mReview_2;
    private TextView mReviewAuthor_3;
    private TextView mReview_3;

    private TextView mTrailersTitle;
    final static int TRAILERS_TITLE_ID = 500;

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
            //ApiUtility.updateDatabaseFromApi(getContext(), apiMovieID); //updates Reviews and Trailers into detail view if needed
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false); // the rootview of the Fragement

        //assign all the views
        mPosterView = (ImageView) rootView.findViewById(R.id.imageview_poster_thumbnail);
        mMovieTitleView = ((TextView) rootView.findViewById(R.id.textview_title));
        mPlotSynopsisView = ((TextView) rootView.findViewById(R.id.textview_plot_synopsis));
        mVoteAverageView = ((TextView) rootView.findViewById(R.id.textview_user_rating));
        mReleaseDateView = ((TextView) rootView.findViewById(R.id.textview_release_date));

        //adding the Reviews
        mReviewAuthor_1 = (TextView) rootView.findViewById(R.id.textview_movie_review_1_author);
        mReview_1 = (TextView) rootView.findViewById(R.id.textview_movie_review_1_content);
        mReviewAuthor_2 = (TextView) rootView.findViewById(R.id.textview_movie_review_2_author);
        mReview_2 = (TextView) rootView.findViewById(R.id.textview_movie_review_2_content);
        mReviewAuthor_3 = (TextView) rootView.findViewById(R.id.textview_movie_review_3_author);
        mReview_3 = (TextView) rootView.findViewById(R.id.textview_movie_review_3_content);

        mButtonContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_video_button_container);

        LinearLayout.LayoutParams textViewTitleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT); //allows clicked button to change width without affected the other buttons

        mTrailersTitle = new TextView(getContext());
        mTrailersTitle.setLayoutParams(textViewTitleParams);
        mTrailersTitle.setTypeface(null, Typeface.BOLD);
        mTrailersTitle.setId(TRAILERS_TITLE_ID);
        mTrailersTitle.setText("Trailers");

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



       /* mButtonVideo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonVideo1.setText("Clicked Button");
            }
        });
*/




     //   mButtonContainer.addView(mButtonVideo1);

       // final Button mTrailerButton_1 = (Button) rootView.findViewById(R.id.button_trailer);
       /* Button.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        Button button = new Button(this);
        button.setLayoutParams(params);*/


       /* final Button mTrailerButton_1 =new Button(getContext(), );
        mTrailerButton_1.setOnClickListener(new View.OnClickListener() { //https://developer.android.com/guide/topics/ui/controls/button.html
            public void onClick(View v) {
                // Do something in response to button click
            mTrailerButton_1.setText("ButtonPressed"); //this works!
                if (mTrailerButton_1 != null) { //if there really is a trailer
                    Intent youTubeIntent =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey1));
                    startActivity(youTubeIntent);
                }
            }
        });
*/

/*
        final Button mTrailerButton_2 = (Button) rootView.findViewById(R.id.button_trailer);
        mTrailerButton_2.setOnClickListener(new View.OnClickListener() { //https://developer.android.com/guide/topics/ui/controls/button.html
            public void onClick(View v) {
                // Do something in response to button click
                mTrailerButton_1.setText("ButtonPressed"); //this works!
                if (mTrailerButton_2 != null) { //if there really is a trailer
                    Intent youTubeIntent =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey2));
                    startActivity(youTubeIntent);
                }
            }
        });

        final Button mTrailerButton_3 = (Button) rootView.findViewById(R.id.button_trailer);
        mTrailerButton_3.setOnClickListener(new View.OnClickListener() { //https://developer.android.com/guide/topics/ui/controls/button.html
            public void onClick(View v) {
                // Do something in response to button click
                mTrailerButton_1.setText("ButtonPressed"); //this works!
                if (mTrailerButton_3 != null) { //if there really is a trailer
                    Intent youTubeIntent =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey3));
                    startActivity(youTubeIntent);
                }
            }
        });

*/












        //  mButtonContainer.addView(mTestButton);


       /* if (arguments != null) { //for debugging
            Log.v(LOG_TAG, "In OnCreateView - arguments not null");
        } else {
            Log.v(LOG_TAG, "In OnCreateView - arguments is null");
        }

        if (movieQueryUri != null) //for debugging
        {
            Log.v(LOG_TAG, "In OnCreateView - movieQueryUri not null, it is " + movieQueryUri);
        } else {
            Log.v(LOG_TAG, "In OnCreateView - movieQueryUri is null ");
        }*/

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


            //inflate Buttons and Reviews only if they exists

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

            //try inflating buttons here

           /* if (mButtonVideo1 == mButtonContainer.findViewById(BUTTON_1_ID)) {
                mButtonContainer.removeView(mButtonVideo1);
                Log.v(LOG_TAG, "Button in parent veiw");
            }*/


            //mButtonContainer.addView(mTrailersTitle);
            if (mTrailerYoutubeKey1 != null && mTrailersTitle !=  mButtonContainer.findViewById(TRAILERS_TITLE_ID)){
                mButtonContainer.addView(mTrailersTitle); //only add trailers title if there are trailers and it has not been added before
            }

            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey1 != null  && mButtonVideo1 != mButtonContainer.findViewById(BUTTON_1_ID)) {
                mButtonContainer.addView(mButtonVideo1);
            }
            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey2 != null  && mButtonVideo2 != mButtonContainer.findViewById(BUTTON_2_ID)) {
                mButtonContainer.addView(mButtonVideo2);
            }

            //only add button if YouTube key exists and it is not already loaded into button container
            if (mTrailerYoutubeKey3 != null  && mButtonVideo3 != mButtonContainer.findViewById(BUTTON_3_ID)) {
                mButtonContainer.addView(mButtonVideo3);
            }


         /*   if (mTrailerYoutubeKey2 != null) {
                mButtonContainer.addView(mButtonVideo2);
            }

            if (mTrailerYoutubeKey3 != null) {
                mButtonContainer.addView(mButtonVideo3);
            }*/

            //add some random test button to the button conatiner
          /*  Button mTestButton1 = new Button(getContext());
            mTestButton1.setText("TestButton1");

            Button mTestButton2 = new Button(getContext());
            mTestButton2.setText("TestButton2");

            //get a reference to button container
            //LinearLayout mButtonContainer = (LinearLayout) rootView.findViewById(R.id.linear_layout_video_button_container);
            mButtonContainer.addView(mTestButton1);
            mButtonContainer.addView(mTestButton2);

*/

        }
    }

    //nothing to do here. No Cursor Adapter to swap cursor
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, " onLoaderReset");
    }



    //helper method to build YouTube Uri for a given movie
    private Uri buildYouTubeUri(String youTubeMovieIdKey){

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
                Intent youTubeIntent1 =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey1));
                startActivity(youTubeIntent1);
                break;

            case BUTTON_2_ID:
                // do your code
               // mButtonVideo2.setText("Clicked Button 2");
                Intent youTubeIntent2 =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey2));
                startActivity(youTubeIntent2);
                break;

            case BUTTON_3_ID:
                // do your code
             //   mButtonVideo3.setText("Clicked Button 3");
                Intent youTubeIntent3 =new Intent(Intent.ACTION_VIEW, buildYouTubeUri(mTrailerYoutubeKey3));
                startActivity(youTubeIntent3);
                break;

            default:
                break;
        }

    }


    /**
     * Takes in the database URI
     *
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

