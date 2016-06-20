package com.gmail.lgelberger.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.gmail.lgelberger.popularmovies.ApiUtility;
import com.gmail.lgelberger.popularmovies.R;
import com.gmail.lgelberger.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Leslie on 2016-06-17.
 * <p/>
 * To update a Review AND Trailer for the given API ID and update the appropriate columns in database
 * Perhaps also get the _ID of the movie to update it (it sounds like a good idea!)
 * <p/>
 * I am assuming that the majority of the data is ALREADY in the database and I can just update entries
 * Make sure this is the case!!!!!
 *
 * In The Future it might be good to just do the API calls and not load the reviews
 * into the database, but instead using a ResultReciever call-back to the calling Fragment
 * and just load the data directly into the views from API call. This would allow for more than the 3 reviews
 * and 3 trailers currently in the database.
 *
 * <p/>
 *
 * params
 * Intent comes in with API Id to update
 * Intent comes in with local database _ID column value of movie to update
 *
 */
public class ReviewAndTrailerUpdateService extends IntentService {
    private final String LOG_TAG = ReviewAndTrailerUpdateService.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name
    public static final String REVIEW_TRAILER_API_ID_EXTRA = "movie_extra"; //key to retrieve API id from intent
    public static final String REVIEW_TRAILER_DB_ID_EXTRA = "movie_db_id_extra";//key to retrieve the _ID of the local database for this movie

    Context mContext = this; //explicitly state context - allows for easier porting from AsyncTask to Service
    String apiMovieID = null; //the movie ID that the API uses
    String databaseMovieID = null; //the _ID that the local database uses for the movie.

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * //@param name Used to name the worker thread, important only for debugging.
     */
    public ReviewAndTrailerUpdateService() {
        super("ReviewAndTrailerUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Get the API movie ID
        apiMovieID = intent.getStringExtra(REVIEW_TRAILER_API_ID_EXTRA);//get the incoming API movie ID
        databaseMovieID = intent.getStringExtra(REVIEW_TRAILER_DB_ID_EXTRA); //get the incoming movies _ID in local database

        //check to make sure we have valid data
        if (apiMovieID == null || databaseMovieID == null) {
            Log.v(LOG_TAG, "Invalid apiMovieID or databaseMovieID passed in");
            return; //
        }

        //perhaps should check to see if reviews/trailers are already in the database?
        //skip for now - add later

        //get URLs for API query
        URL reviewApiQueryUrl = ApiUtility.makeReviewsAPIQueryURL(mContext, apiMovieID);
        URL trailerApiQueryUrl = ApiUtility.makeTrailersAPIQueryURL(mContext, apiMovieID);

        //start with Reviews - arbitrary choice, but you have to start somewhere
        String reviewJsonFromApi = ApiUtility.doApiCall(reviewApiQueryUrl); //do API call and get JSON
        if (reviewJsonFromApi == null) { //do nothing - nothing returned from API call - no database loading needed
            Log.v(LOG_TAG, "Nothing returned from API call to Reviews");
           // return; //stop the service  --is this a good idea? - better way?
        } //otherwise valid incoming JSON

        String trailerJsonFromApi = ApiUtility.doApiCall(trailerApiQueryUrl);
        if (trailerJsonFromApi == null) {
            Log.v(LOG_TAG, "Nothing returned from API call to Trailers");
            // return; //stop the service
        }


        //now we have the JSON data we need
        //extract JSON data needed.

        //Must be declared outside Try/Catch block for access later on
        ContentValues reviewContentValues = null;
        ContentValues trailerContentValues = null;

        //Reviews JSON Extraction
        try {
            reviewContentValues = getReviewsFromJson(reviewJsonFromApi);
            trailerContentValues = getTrailersFromJson(trailerJsonFromApi);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //now we have content values
        //make one big content values to database update
        ContentValues movieTotalContentValues = reviewContentValues;
        movieTotalContentValues.putAll(trailerContentValues);// add the trailer content values

        //now add update the database with this information
        Uri updateUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(Long.parseLong(databaseMovieID));

        int numberOfRowsUpdatedReviews = mContext.getContentResolver().update(updateUri,
                movieTotalContentValues,
                null,
                null); //update the database with Reviews

        //we are done!
        return;
    }


    // This is tied specidifically to the JSON output from our API
    //If TheMovieDB.COM changes its JSON output, this method must change to reflect that.
    private ContentValues getTrailersFromJson(String movieTrailerJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_TRAILER = mContext.getString(R.string.movie_json_key_youtube_key);

        Log.v(LOG_TAG, "The Trailers JSON is "+ movieTrailerJsonStr);

        ContentValues movieTrailersCVFromJSON = new ContentValues(); // Make the Content Values that will be updating database rows
        //assume no Reviews - so load up with NoReviews for all
        // String noTrailers = "No Reviews Yet";
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, (String) null);
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2,(String) null);
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3,(String) null);


        //check to see if anything came in as a JSON string
        if (movieTrailerJsonStr != null) {  //only load up the trailers if there is something to load
            JSONObject movieJSON = new JSONObject(movieTrailerJsonStr); //create JSON object from input string
            JSONArray movieTrailerArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of trailers

            int movieTrailerArraySize = movieTrailerArray.length();

            //I need to know if the size is 0 , 1, 2, more (if more then just do the 3rd one, ignore rest)
            //this is a cludge for now - change later to more elegant solution
            switch (movieTrailerArraySize) {
                default: //overwrite all the reviews - just overwrite third review right now
                    movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, movieTrailerArray.getJSONObject(2).getString(TMDB_TRAILER));
                    //no break so move to next one
                case 2: // overwrite the middle review
                    movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, movieTrailerArray.getJSONObject(1).getString(TMDB_TRAILER));
                    //no break so move to next one
                case 1: //overwrite just the first review
                    movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, movieTrailerArray.getJSONObject(0).getString(TMDB_TRAILER));
                case 0: //no reviews in array!!! Don't overwrite anything
                    break;
            }
        }





            //I will manually put in the content values without a FOR loop for now
      /*  JSONObject movieTrailer1 = movieTrailerArray.getJSONObject(0);
        JSONObject movieTrailer2 = movieTrailerArray.getJSONObject(1);
        JSONObject movieTrailer3 = movieTrailerArray.getJSONObject(2);

        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, movieTrailer1.getString(TMDB_TRAILER));
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, movieTrailer2.getString(TMDB_TRAILER));
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, movieTrailer3.getString(TMDB_TRAILER));
*/
        return movieTrailersCVFromJSON;
    }

    // This is tied specidifically to the JSON output from our API
    //If TheMovieDB.COM changes its JSON output, this method must change to reflect that.
    private ContentValues getReviewsFromJson(String movieReviewJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_AUTHOR = mContext.getString(R.string.movie_json_key_review_author);
        final String TMDB_CONTENT = mContext.getString(R.string.movie_json_key_review_content);

        Log.v(LOG_TAG, "The Reviews JSON is "+ movieReviewJsonStr);

        ContentValues movieReviewCVFromJSON = new ContentValues(); // Make the Content Values that will be updating database rows
       /* //assume no Reviews - so load up with NoReviews for all
        String noReviews = "No Reviews Yet";
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, noReviews);
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, noReviews);

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR,noReviews);
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, noReviews);

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR,noReviews);
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, noReviews);
*/
        //only load up the reviews if the incoming ReviewJSON string is valid
        if (movieReviewJsonStr != null){
            JSONObject movieJSON = new JSONObject(movieReviewJsonStr); //create JSON object from input string
            JSONArray movieReviewArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of reviews
            //NB Some arrays may be zero in size
            int movieReviewArraySize = movieReviewArray.length();

            //I need to know if the size is 0 , 1, 2, more (if more then just do the 3rd one, ignore rest)
            //this is a cludge for now - change later to more elegant solution
            switch (movieReviewArraySize) {
                default: //overwrite all the reviews - just overwrite third review right now
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR, movieReviewArray.getJSONObject(2).getString(TMDB_AUTHOR));
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, movieReviewArray.getJSONObject(2).getString(TMDB_CONTENT));
                    //no break so move to next one
                case 2: // overwrite the middle review
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR, movieReviewArray.getJSONObject(1).getString(TMDB_AUTHOR));
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, movieReviewArray.getJSONObject(1).getString(TMDB_CONTENT));
                    //no break so move to next one
                case 1: //overwrite just the first review
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, movieReviewArray.getJSONObject(0).getString(TMDB_AUTHOR));
                    movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, movieReviewArray.getJSONObject(0).getString(TMDB_CONTENT));
                case 0: //no reviews in array!!! Don't overwrite anything
                    break;
            }
        }




           /* case 0: //no reviews - make all Content Values say "no Reviews)
                break;
            case 1: //overwrite just the first review
                movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, movieReviewArray.getJSONObject(0).getString(TMDB_AUTHOR));
                movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, movieReviewArray.getJSONObject(0).getString(TMDB_CONTENT));
                break;
            case 2: //overwrite the second review AND the first review


                break;*/



        //below commented out - wasn't able to deal with different review sizes


        //I will manually put in the content values without a FOR loop for now
        /*JSONObject movieReview1 = movieReviewArray.getJSONObject(0);
        JSONObject movieReview2 = movieReviewArray.getJSONObject(1);
        JSONObject movieReview3 = movieReviewArray.getJSONObject(2);

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, movieReview1.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, movieReview1.getString(TMDB_CONTENT));

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR, movieReview2.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, movieReview2.getString(TMDB_CONTENT));

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR, movieReview3.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, movieReview3.getString(TMDB_CONTENT));

*/


        return movieReviewCVFromJSON;
    }
}
