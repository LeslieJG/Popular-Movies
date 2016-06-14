package com.gmail.lgelberger.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.lgelberger.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leslie on 2016-05-16.
 * <p/>
 * This is the NEW FetchMoviesFromApiTask
 * Writes incoming API calls to database
 * <p/>
 * made the networking stuff an AsyncTask for now to get it off main thread
 * <p/>
 * Params, the type of the parameters sent to the task upon execution.
 * Progress, the type of the progress units published during the background computation.
 * Result, the type of the result of the background computation.
 * <p/>
 * ALSO - make sure that AsyncTask is cancel AsyncTask instance properly in onDestroy
 * so that if the fragment is rebuilding, you destroy AsyncTask and rebuild it once
 * Fragment has rebuilt so that resources can be accessed e.g. R.string.id
 * as per
 * http://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to-activity
 * <p/>
 * <p/>
 * <p/>
 * Param String will be the URL to call the movie db on internet
 * <p/>
 * This class modelled after the "Sunshine" AsyncTask
 */
public class FetchMoviesFromApiTask extends AsyncTask<URL, Void, String> {
    private final String LOG_TAG = FetchMoviesFromApiTask.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name

    private final Context mContext;

    //to decide which values to update in MovieEntry database
    //and also do decided which JSON is expected to be returned
    private int apiCallNeeded = 0; //initial variable set to not one of the below values
    private static final int updateEntireMovieEntryTable = 1;
    private static final int updateMovieReviews = 2;
    private static final int updateMovieTrailers = 3;

    //for searching URL's
    //if the API movie query URL's change, these static variables need to change to reflect the current API urls needed
    private static final String popularMovies = "popular";
    private static final String topRatedMovies = "top_rated";
    private static final String movieReviews = "reviews";
    private static final String movieVideos = "videos";

    private String movieApiId; //should hold the API Id of movie. Needed for inserting Movie Reviews and Trailers
    private String selection;  //for querying and updating the local database about specific movie using API id

    /**
     * We will use this one with CursorLoader versions
     *
     * @param context application context for context.getString(R.id etc) - to access the apps String resources
     */
    FetchMoviesFromApiTask(Context context) {
        this.mContext = context;
    }


    /**
     * Get's the movie data from Internet and returns a String containing JSON data of all
     * the movies requested
     *
     * @param params URL of movie Query to access internet
     * @return movieJsonStr: a String containing the JSON data of all movies from internet
     */
    @Override
    protected String doInBackground(URL... params) {
        //check to see if URL is passed in
        if (params.length == 0) //no URL passed in
        {
            Log.e(LOG_TAG, "No URL passed in");
            return null;
        }

        URL url = params[0]; //get the URL from the input parameters
        Log.v(LOG_TAG, "URL for API call is " + url.toString()); //for figureing out types of calls needed - delete later on

        //url should be one of the following types
        //   http://api.themoviedb.org/3/movie/popular?api_key=[My API Key]
        //   http://api.themoviedb.org/3/movie/top_rated?api_key=[My API Key]
        //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=[My API Key]
        //  http://api.themoviedb.org/3/movie/293660/videos?api_key=[My API Key]


        //Check which kind of URL comes in to decide what to do next
        String urlAsString = url.toString();
        Uri urlAsUri = Uri.parse(urlAsString);

        if (urlAsString.toLowerCase().contains(popularMovies) || urlAsString.toLowerCase().contains(topRatedMovies)) {
            //do the required action for popular movies or top rated - i.e. a normal API query
            apiCallNeeded = updateEntireMovieEntryTable;
        } else if (urlAsString.toLowerCase().contains(movieReviews)) { //do the requred action to load just the Movie Reviews in
            apiCallNeeded = updateMovieReviews;
            //check database for Reviews at the correct movie ID- If one is there then return null here - don't need to do anything
            movieApiId = ApiUtility.getApiMovieIdFromUri(urlAsUri); //get the movie ID
            //get a cursor from Database using API id as query param
            //make the Query URI
            //Uri movieDetailQueryUri =  MovieContract.MovieEntry.buildMovieUriWithAppendedID(Long.valueOf(movieApiId)); //perhaps error check if Long.valueOf(movieApiId) returns an actual LONG and is not invalid?
            Uri dbQueryUri = MovieContract.MovieEntry.CONTENT_URI; //look through entire database
            String[] reviewMovieColumnProjection = {MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1}; //projection of just First Review - Just need to see if there are reviews there already
            selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiId;

            // Queries the MovieEntry Table and returns results for just the one movie
            // - ensure that it will return null if the movie exists but if the reviews aren't there yet!
            Cursor mCursor = mContext.getContentResolver().query(
                    //movieDetailQueryUri,   // The content URI of the words table
                    dbQueryUri,
                    reviewMovieColumnProjection, // The columns to return for each row - should Just be the first revie
                    // null,                    // Selection criteria
                    selection, //selection criteria
                    null,                     // Selection args
                    null);                        // The sort order for the returned rows

            //check to see if MovieReviews exits already for this movie
            if (mCursor.moveToFirst() == true) { //already have Movie Review in the table. No need to load
                Log.v(LOG_TAG, "There are already Reviews Loaded - no need to make an API call");
                return null;  //return null String - no need to do any loading of Reviews
            }
            Log.v(LOG_TAG, "There are NO reviews Loaded - Make the API call");

        } else if (urlAsString.toLowerCase().contains(movieVideos)) { //do the required action to load just the Movie Trailers in
            apiCallNeeded = updateMovieTrailers;

            movieApiId = ApiUtility.getApiMovieIdFromUri(urlAsUri); //get the movie API ID
            //get a cursor from Database using API id as query param

            Uri dbQueryUri = MovieContract.MovieEntry.CONTENT_URI; //look through entire database
            String[] trailerMovieColumnProjection = {MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1}; //projection of just First Trailer - Just need to see if there are reviews there already
            selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiId;

            // Queries the MovieEntry Table and returns results for just the one movie
            // - ensure that it will return null if the movie exists but if the reviews aren't there yet!
            Cursor mCursor = mContext.getContentResolver().query(
                    dbQueryUri,   //
                    trailerMovieColumnProjection,           // The columns to return for each row - should Just be the first trailer
                    selection,                    // Selection criteria
                    null,                     // Selection args
                    null);                        // The sort order for the returned rows

            //check to see if MovieReviews exits already for this movie
            if (mCursor.moveToFirst() == true) { //already have Movie Trailer in the table. No need to load
                Log.v(LOG_TAG, "There are already Trailers Loaded - no need to make an API call");
                return null;  //return null String - no need to do any loading of Reviews
            }
            Log.v(LOG_TAG, "There are NO Trailers Loaded - Make the API call");

        } else {
            //something went very wrong. Error off
            Log.e(LOG_TAG, "Something Screwed up with deciding whether to update MovieEntryTable or Review or Trailers");
        }


        //If we get to here then we need to make the API call to get data (i.e the data is not already in database)

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null; // Will contain the raw JSON response as a string.

        try {
            // Create the request to themoviedb.org, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            //return the movie JSON info as String
            return movieJsonStr; // successful, done here (except for finally block)

        } catch (MalformedURLException e) {
            //  Toast.makeText(mContext, "Got invalid data from server", Toast.LENGTH_LONG).show();
            e.printStackTrace(); //the URL was malformed
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally { //disconnect the URL connection and close reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        // This will only happen if there was an error getting or parsing the movie data.
        return null;
    }


    /**
     * This is where the JSON result from the movie query is processed into the database
     * (and later just read the results from the database)
     *
     * @param result is the JSON string from themoviedb.org
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //if null no API call made (or no results returned)
        if (result == null) { //do nothing
            //keep the check for null though - it is needed in case data is already in database and don't have to do API call
            return;
        }


        try {
            switch (apiCallNeeded) { //find out which type of API call was made
                case updateEntireMovieEntryTable:
                    //in future check to see if this is already in database, but for now just delete the database
                    int rowsDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null); //delete entire database
                    Log.v(LOG_TAG, " - Database deleted after API call"); //for debugging tablet rotated twice
                    //for debugging
                    // Toast.makeText(mContext, "Deleted " +  Integer.toString(rowsDeleted)   + " rws from database", Toast.LENGTH_LONG).show();

                    ContentValues[] myTempContentValues = getMovieDataFromJson(result); //this is my ContentValues[] with all the movie data in it
                    //bulk insert all new stuff
                    int numberOfRowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, myTempContentValues);
                    break;
                case updateMovieReviews:
                    // (Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                    Uri reviewUri = MovieContract.MovieEntry.CONTENT_URI;
                    ContentValues myTempReviewValues = getReviewsFromJson(result);  //get content values from JSON
                    selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiId;
                   // String selectionArgs[] = null;

                    //int numberOfRowsUpdated = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, myTempReviewValues, MovieContract.MovieEntry.COLUMN_API_MOVIE_ID)
                    int numberOfRowsUpdatedReviews = mContext.getContentResolver().update(reviewUri, myTempReviewValues, selection, null); //update the database with Reviews

                    //int insertedReviews= mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, myTempReviewValues);

                    break;
                case updateMovieTrailers:
                    ContentValues myTempTrailerValues = getTrailersFromJson(result);

                    Uri videoUri = MovieContract.MovieEntry.CONTENT_URI;
                    ContentValues myTempVideoValues = getTrailersFromJson(result);  //get content values from JSON
                    selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiId;
                   // String selectionArgs[] = null;

                    //int numberOfRowsUpdated = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, myTempReviewValues, MovieContract.MovieEntry.COLUMN_API_MOVIE_ID)
                    int numberOfRowsUpdatedTrailers = mContext.getContentResolver().update(videoUri, myTempVideoValues, selection, null); //update the database with Reviews
                    //insert into database

                    break;
                default:
                    break;
            }


            //or just make a toast? - for debugging
            /*Toast.makeText(mContext, Integer.toString(inserted)   +
                    " Out of " +
                    Integer.toString(myTempContentValues.length) +
                    " movies inserted into database.",
                    Toast.LENGTH_LONG).show();*/


            //confirm that the data is inserted correctly (throw an exception if inserted != myTempContentValues.getLength();
           /* if (inserted != myTempContentValues.length){
                //something went horribly wrong and data not in database
                throw new RuntimeException("FetMovieTask (AsyncTask) The database stored " + Integer.toString(inserted) +
                        " rows instead of the required " + Integer.toString(myTempContentValues.length)); */

            //////////////LJG ZZZ This thrown runtime exception may not be the best practise way to deal with this

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Take the complete string representing the entire initial movie call to themoviedb.org
     * and pull out the data needed for the grid view
     * <p/>
     * Put data directly into database
     * (for now, do not check it if is already in database
     * just wipe database first,
     * then add this stuff
     *
     * @param movieJsonStr the JSON of all the movie data from themoviedb.org
     * @return array of ZZZOLDMovieDataProvider objects to hold the info for each movie
     * @throws JSONException
     */
    //  private List<ZZZOLDMovieDataProvider> getMovieDataFromJson(String movieJsonStr) throws JSONException {
    private ContentValues[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_POSTER_PATH = mContext.getString(R.string.movie_json_key_poster_path);
        final String TMDB_TITLE = mContext.getString(R.string.movie_json_key_title);
        //new ones for detail activity here
        final String TMDB_ORIGINAL_TITLE = mContext.getString(R.string.movie_json_key_original_title);
        final String TMDB_OVERVIEW = mContext.getString(R.string.movie_json_key_overview);
        final String TMDB_VOTE_AVERAGE = mContext.getString(R.string.movie_json_key_vote_average);
        final String TMDB_RELEASE_DATE = mContext.getString(R.string.movie_json_key_release_date);
        //added for filling the full info into database
        final String TMDB_API_ID = mContext.getString(R.string.movie_json_key_api_movie_id);

        JSONObject movieJSON = new JSONObject(movieJsonStr); //create JSON object from input string
        JSONArray movieArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of movies

        //the new ContentValues that from the JSON that will be put into the database eventually
        ContentValues[] movieContentValueArrayFromJSON = new ContentValues[movieArray.length()];

        int movieArrayLength = movieArray.length();
        for (int i = 0; i < movieArrayLength; i++) { //load the info needed into movieDataProviderArray
            JSONObject movieDetails = movieArray.getJSONObject(i);// Get the JSON object representing *one* movie

            URL moviePosterURL = makePosterURL(movieDetails.getString(TMDB_POSTER_PATH)); //get movie URL

            //make a bunch of ContentValues for the database
            movieContentValueArrayFromJSON[i] = new ContentValues();
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieDetails.getString(TMDB_TITLE));
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, String.valueOf(moviePosterURL));
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieDetails.getString(TMDB_ORIGINAL_TITLE));
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, movieDetails.getString(TMDB_OVERVIEW));
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieDetails.getString(TMDB_VOTE_AVERAGE));
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieDetails.getString(TMDB_RELEASE_DATE));

            //also load all the other stuff I need into database
            movieContentValueArrayFromJSON[i].put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, movieDetails.getString(TMDB_API_ID));

            ///////////////////LJG ZZZ at a later time load the following 3 extra items into the database (their are columns existing for this)
            // need to load: MoviePoster.jpg     MovieReview     MovieVideo
            //load up the MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT , " + //I'm allowing this to be null if needed until I can
            //figure out how to store the damn poster image itself

            //load into database MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1 + " TEXT , " +  //I'm allowing this to be null if needed until I can
            //figure out how to store the movie reviews  (perhaps just a URL?)

            //load into database   MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1 + " TEXT  " +  //I'm allowing this to be null if needed until I can
        }

        return movieContentValueArrayFromJSON;
    }


    // This is tied specidifically to the JSON output from our API
    //If TheMovieDB.COM changes its JSON output, this method must change to reflect that.
    private ContentValues getTrailersFromJson(String movieTrailerJsonStr) throws JSONException {
    // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_TRAILER = mContext.getString(R.string.movie_json_key_youtube_key);

        JSONObject movieJSON = new JSONObject(movieTrailerJsonStr); //create JSON object from input string
        JSONArray movieTrailerArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of trailers

        ContentValues movieTrailersCVFromJSON = new ContentValues(); // Make the Content Values that will be updating database rows

        //I will manually put in the content values without a FOR loop for now
        JSONObject movieTrailer1 = movieTrailerArray.getJSONObject(0);
        JSONObject movieTrailer2 = movieTrailerArray.getJSONObject(1);
        JSONObject movieTrailer3 = movieTrailerArray.getJSONObject(2);

        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, movieTrailer1.getString(TMDB_TRAILER));
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, movieTrailer2.getString(TMDB_TRAILER));
        movieTrailersCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, movieTrailer3.getString(TMDB_TRAILER));

        return movieTrailersCVFromJSON;
    }

    // This is tied specidifically to the JSON output from our API
    //If TheMovieDB.COM changes its JSON output, this method must change to reflect that.
    private ContentValues getReviewsFromJson(String movieReviewJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_AUTHOR = mContext.getString(R.string.movie_json_key_review_author);
        final String TMDB_CONTENT = mContext.getString(R.string.movie_json_key_review_content);

        JSONObject movieJSON = new JSONObject(movieReviewJsonStr); //create JSON object from input string
        JSONArray movieReviewArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of reviews

        ContentValues movieReviewCVFromJSON = new ContentValues(); // Make the Content Values that will be updating database rows

        //I will manually put in the content values without a FOR loop for now
        JSONObject movieReview1 = movieReviewArray.getJSONObject(0);
        JSONObject movieReview2 = movieReviewArray.getJSONObject(1);
        JSONObject movieReview3 = movieReviewArray.getJSONObject(2);

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, movieReview1.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, movieReview1.getString(TMDB_CONTENT));

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR, movieReview2.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, movieReview2.getString(TMDB_CONTENT));

        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR, movieReview3.getString(TMDB_AUTHOR));
        movieReviewCVFromJSON.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, movieReview3.getString(TMDB_CONTENT));


        return movieReviewCVFromJSON;
    }


    /**
     * Helper method Makes URL to access API to get movie poster
     *
     * @return URL for themoviedb.org
     */
    private URL makePosterURL(String posterPath) {
        URL url = null; //url to be built

        // It’s constructed using 3 parts:
        // The base URL will look like: http://image.tmdb.org/t/p/.
        // Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
        //  And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
        //  Combining these three parts gives us a final url of  e.g. http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        Uri builtUri = Uri.parse(mContext.getString(R.string.poster_url_base)).buildUpon()
                .appendPath(mContext.getString(R.string.poster_url_poster_size))
                .appendEncodedPath(posterPath) //needed encoded path instead of path, as it wasn't creating properly
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
