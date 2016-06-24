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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leslie on 2016-06-16.
 * <p/>
 * <p/>
 * Intent Service doesn't use main thread for task
 * This fetches data from API and loads it into the local database if needed.
 * Service is a context! So can use Context context = this;
 * <p/>
 * This service is an Android component and MUST be registered in AndroidManifest
 * <service android:name=".service.PopularMoviesService"/> as a child of the <application> element
 * Over ride Constructor to pass in a Thread name to superclass constructor
 * OverRide onHandleIntent
 * <p/>
 * <p/>
 * Start the Service with explicit intent using StartService method
 * For more info see:
 * https://developer.android.com/guide/components/services.html
 * https://developer.android.com/guide/topics/manifest/service-element.html
 * https://www.udacity.com/course/viewer#!/c-ud853-nd/l-1614738811/e-1664298683/m-1664298684
 * <p/>
 * <p/>
 * <p/>
 * Get ALL Reviews and Trailers when database updated
 * Will ONlY be called with API Query url
 * http://api.themoviedb.org/3/movie/top_rated?api_key=[My API Key]
 * http://api.themoviedb.org/3/movie/popular?api_key=[My API Key]
 * <p/>
 * WIll need to call API and load data into database.
 * Then it will need to loop through each movie and get it's own list of API movie ID's needed for all 20 movies
 * THen for EACH movie, it will have to make a new intent call (to get a
 */
public class PopularMoviesService extends IntentService {
    private final String LOG_TAG = PopularMoviesService.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name
    public static final String MOVIE_API_QUERY_EXTRA_KEY = "movie_extra"; //key to retrieve movie query URL from intent
    // - make public static final so that the activity starting intent can use this as extra key for intent.

    Context mContext = this; //explicitly state context - allows for easier porting from AsyncTask to Service
    String jsonFromApi; //just adding this to hold JSON string returned from API call - perhaps it will be changed on refactoring

    /////////////////////Database projection constants///////////////
    //For making good use of database Projections specify the columns we need
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_API_MOVIE_ID
    };

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_API_MOVIE_ID = 1;
    /////////////////////////////////////////////////////////


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * //@param name Used to name the worker thread, important only for debugging.
     */
    public PopularMoviesService() { //removed from brackets --> String name
        super("PopularMoviesService"); //pass the name of the worker thread - This must be done when implementing IntentService
        // to the superclass Service - I've named it the same as this class to make debugging easier
        // super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) { //Do all your off Thread stuff here -
        // It is the only other thing (other than making constructor) that is needed for IntentService

        String urlAsString = intent.getStringExtra(MOVIE_API_QUERY_EXTRA_KEY);//get the incoming URL as string
        URL url = null;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //check to make sure URL passed in, if no URL, no need to do API call
        if (url == null) {
            Log.e(LOG_TAG, "No URL passed in");
            return; //nothing to do - just exit
        }
        Log.v(LOG_TAG, "URL for API call is " + url.toString()); //for figureing out types of calls needed - delete later on
        //url should be one of the following types
        //   http://api.themoviedb.org/3/movie/popular?api_key=[My API Key]
        //   http://api.themoviedb.org/3/movie/top_rated?api_key=[My API Key]

        // do API call and load into database*/
        jsonFromApi = ApiUtility.fetchJsonFromApi(url); //do API call and get JSON
        if (jsonFromApi == null) { //do nothing - nothing returned from API call - no database loading needed
            return;
        }

        int rowsDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null); //delete entire database
        Log.v(LOG_TAG, " - Database deleted after API call"); //for debugging tablet rotated twice

        try {
            ContentValues[] myTempContentValues = getMovieDataFromJson(jsonFromApi);
            //bulk insert all new stuff
            int numberOfRowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                    myTempContentValues);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return; //all done nothing more to do in service
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
