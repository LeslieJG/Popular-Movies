package com.gmail.lgelberger.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.gmail.lgelberger.popularmovies.ApiUtility;
import com.gmail.lgelberger.popularmovies.R;
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
 * Created by Leslie on 2016-06-16.
 *
 *
 * Intent Service doesn't use main thread for task
 * This fetches data from API and loads it into the local database if needed.
 * Service is a context! So can use Context context = this;
 *
 * This service is an Android component and MUST be registered in AndroidManifest
 *  <service android:name=".service.PopularMoviesService"/> as a child of the <application> element
 * Over ride Constructor to pass in a Thread name to superclass constructor
 * OverRide onHandleIntent
 *
 *Start the Service with explicit intent using StartService method
 * For more info see:
 * https://developer.android.com/guide/components/services.html
 * https://developer.android.com/guide/topics/manifest/service-element.html
 * https://www.udacity.com/course/viewer#!/c-ud853-nd/l-1614738811/e-1664298683/m-1664298684
 *
 */
public class PopularMoviesService extends IntentService{
    private final String LOG_TAG = PopularMoviesService.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name
    public static final String MOVIE_API_QUERY_EXTRA_KEY = "movie_extra"; //key to retrieve movie query URL from intent
    // - make public static final so that the activity starting intent can use this as extra key for intent.

    Context mContext = this; //explicitly state context - allows for easier porting from AsyncTask to Service

    //to decide which values to update in MovieEntry database
    //and also do decided which JSON is expected to be returned
    private int apiCallNeeded = 0; //initial variable set to not one of the below values
    private static final int updateEntireMovieEntryTable = 1;
    private static final int updateMovieReviews = 2;
    private static final int updateMovieTrailers = 3;
    private static final int updateNothingWrongURLType = 0;

    //for searching URL's
    //if the API movie query URL's change, these static variables need to change to reflect the current API urls needed
    //LJG ZZZ god damn it these are defined in STRINGS.xml use those!!!!!!!!! - crappy code here!!!!!
    private static final String popularMovies = "popular";
    private static final String topRatedMovies = "top_rated";
    private static final String movieReviews = "reviews";
    private static final String movieVideos = "videos";

    private String movieApiId; //should hold the API Id of movie. Needed for inserting Movie Reviews and Trailers
    //private String selection;  //for querying and updating the local database about specific movie using API id

    String jsonFromApi; //just adding this to hold JSON string returned from API call - perhaps it will be changed on refactoring

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * //@param name Used to name the worker thread, important only for debugging.
     */
    public PopularMoviesService() { //removed from brackets --> String name
        super("PopularMoviesService"); //pass the name of the worker thread - This must be done when implementing IntentService
        // to the superclass Service - I've named it the same as this class to make debugging easier

       // super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) { //Do all your off Thread stuff here - It is the only other thing (other than making constructo)
        //that is needed for IntentService

        //get the API Query URL needed from Intent
        ///LJG ZZZ Ensure I really need the URL passed in, and not a URI or String?

      //  URL url =  intent.getParcelableExtra(MOVIE_API_QUERY_EXTRA_KEY); //perhaps just pass extra as URI or string?


        String urlAsString = intent.getStringExtra(MOVIE_API_QUERY_EXTRA_KEY);//get the incoming URL as string
        URL url = null;
        try {
           url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //N.B. Url is the actual API query URL that is passed in
        //I need to figure out what is required - general API call or looking for reviews/trailers for specific movie
        // if reviews/trailers I use the API movie ID given in the incomming URL to look that movie up in my local
        //database and see if I already have the reviews/trailers from it - if not, then do API call to get them


        //check to make sure URL passed in, if no URL, no need to do API call
        if (url == null){
            Log.e(LOG_TAG, "No URL passed in");
            return; //nothing to do - just exit
        }
        Log.v(LOG_TAG, "URL for API call is " + url.toString()); //for figureing out types of calls needed - delete later on

        //url should be one of the following types
        //   http://api.themoviedb.org/3/movie/popular?api_key=[My API Key]
        //   http://api.themoviedb.org/3/movie/top_rated?api_key=[My API Key]
        //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=[My API Key]
        //  http://api.themoviedb.org/3/movie/293660/videos?api_key=[My API Key]


        //Check which kind of URL comes in to decide what to do next
     //   String urlAsString = url.toString();
     //   Uri urlAsUri = Uri.parse(urlAsString);



       if (dataAlreadyInDb(url)){
           //if data already there, no need to make API call or load database - We are done
           return;
       } //otherwise do API call and load into database

        jsonFromApi = doApiCall(url); //do API call and get JSON
        if (jsonFromApi == null) { //do nothing - nothing returned from API call - no database loading needed
            return;
        }



        apiCallNeeded = getApiCallNeeded(url);
        String selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiId; //just select the individual movie (if updating reviews or trailers)

        try {
            switch (apiCallNeeded) { //find out which type of API call was made
                case updateEntireMovieEntryTable:
                    int rowsDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null); //delete entire database
                    Log.v(LOG_TAG, " - Database deleted after API call"); //for debugging tablet rotated twice

                    ContentValues[] myTempContentValues = getMovieDataFromJson(jsonFromApi); //this is my ContentValues[] with all the movie data in it
                    //bulk insert all new stuff
                    int numberOfRowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                            myTempContentValues);
                    break;
                case updateMovieReviews:
                    // (Uri uri, ContentValues values, String selection, String[] selectionArgs) {
                    ContentValues myTempReviewValues = getReviewsFromJson(jsonFromApi);  //get content values from JSON
                    int numberOfRowsUpdatedReviews = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                            myTempReviewValues,
                            selection,
                            null); //update the database with Reviews
                    break;
                case updateMovieTrailers:
                    ContentValues myTempVideoValues = getTrailersFromJson(jsonFromApi);  //get content values from JSON
                    int numberOfRowsUpdatedTrailers = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                            myTempVideoValues,
                            selection,
                            null); //update the database with Reviews
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


    /*
    Check if data refered to by apiQueryUrl is already in the database
    returns true if data already in database
    false if data not in database
     */
    private boolean dataAlreadyInDb (URL apiQueryUrl) {
        int apiCallNeededJustThisMethod = getApiCallNeeded(apiQueryUrl); //figure out which API call was passed in

        //Check which kind of URL comes in to decide what to do next
        String urlAsString = apiQueryUrl.toString();
        Uri urlAsUri = Uri.parse(urlAsString);

        String movieApiIdFromJustThisMethod = ApiUtility.getApiMovieIdFromUri(urlAsUri); //get the movie ID if Reviews or Trailers URL

        String selection = MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " = " + movieApiIdFromJustThisMethod;
        String[] projection = new String[1]; //will have only one column in my projection

            switch (apiCallNeededJustThisMethod) { //need to get apiCallNeeded somewhere else for better isolation of methods
                case updateEntireMovieEntryTable:
                    //for now do nothing - I will always delete the database and reload it
                    //in the future we can do a database check
                    return false; //data is not already in database - we're done with this method.
                    //break;
                case updateMovieReviews:
                    projection[0] = MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1;
                    //projection of just First Review - Just need to see if there are reviews there already
                    break;
                case updateMovieTrailers:
                    projection[0] = MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1;
                    //projection of just First Trailer - Just need to see if there are reviews there already
                    break;
                default:
                    //something went wrong - should never reach here
                    break;
            }


        // Queries the MovieEntry Table and returns results for just the one movie
        Cursor mCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, //look through entire database
                projection, // The columns to return for each row - should Just be the first review or trailer
                selection, //selection criteria - just look for
                null,                     // Selection args
                null);                        // The sort order for the returned rows


        try {
            //check to see if MovieReviews exits already for this movie
            if (mCursor.moveToFirst() == true) { //already have Movie Review in the table. No need to load
                Log.v(LOG_TAG, "There are already Reviews Loaded - no need to make an API call");
                return true;  //return true - data already in database- no need to do any loading of Reviews
            } else {
                Log.v(LOG_TAG, "There are NO reviews Loaded - Make the API call");
                return false; //data Not yet in database API call needed
            }

        } finally {
            mCursor.close(); //just making sure to close cursor before allowing the above return null or allowing it to continue
        }
    }



    private String doApiCall(URL apiQueryUrl) {
        //If we get to here then we need to make the API call to get data (i.e the data is not already in database)
        String movieJsonStr = null; // Will contain the raw JSON response as a string.

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to themoviedb.org, and open the connection
            urlConnection = (HttpURLConnection) apiQueryUrl.openConnection();
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
        // This will only happen if there was an error getting or parsing the movie data. or API delivers nothing
        return null;
    }






    //
    /*
    takes incoming URL and decides which API URL it is
    Returns one of the three static final constants
    //decide where to store the static final constants in this class or in ApiUtilitiy?

     */
    private int getApiCallNeeded(URL url) {
         int apiCalltype;

        //Check which kind of URL comes in to decide what to do next
        String urlAsString = url.toString();
        Uri urlAsUri = Uri.parse(urlAsString);

        if (urlAsString.toLowerCase().contains(popularMovies) || urlAsString.toLowerCase().contains(topRatedMovies)) {
            //do the required action for popular movies or top rated - i.e. a normal API query
            apiCalltype = updateEntireMovieEntryTable;
        }  else if (urlAsString.toLowerCase().contains(movieReviews)) { //do the requred action to load just the Movie Reviews in
        apiCalltype = updateMovieReviews;}
        else if (urlAsString.toLowerCase().contains(movieVideos)) { //do the required action to load just the Movie Trailers in
            apiCalltype = updateMovieTrailers;}
        else {
            //something went very wrong. Error off
            Log.e(LOG_TAG, "Something Screwed up with deciding whether to update MovieEntryTable or Review or Trailers");
            return 0;
        }

        return apiCalltype;
    }




/**
 * Take the complete string representing the entire initial movie call to themoviedb.org
 * and pull out the data needed for the grid view
 * <p>
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
