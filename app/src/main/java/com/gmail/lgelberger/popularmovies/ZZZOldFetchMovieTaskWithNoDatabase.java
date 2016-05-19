package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Leslie on 2016-05-16.
 *
 * This is the OLD FetchMovieTask before using Loaders and Cursor Loader.
 * I am keeping it as an example as to how to write an asyncTask with direct API calls and
 * no Content Provider calls.
 * It is a good first step in creating an AsyncTask
 *
 * made the networking stuff an AsyncTask for now to get it off main thread
 * <p/>
 * <p/>
 * Params, the type of the parameters sent to the task upon execution.
 * Progress, the type of the progress units published during the background computation.
 * Result, the type of the result of the background computation.
 * <p/>
 * <p/>
 * ALSO - make sure that AsyncTask is cancel AsyncTask instance properly in onDestroy
 * so that if the fragment is rebuilding, you destroy AsyncTask and rebuild it once
 * Fragment has rebuilt so that resources can be accessed e.g. R.string.id
 * as per
 * http://stackoverflow.com/questions/10919240/fragment-myfragment-not-attached-to-activity
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * Param String will be the URL to call the moviedb on internet
 * <p/>
 * This class modelled after the "Sunshine" AsyncTask
 */
public class ZZZOldFetchMovieTaskWithNoDatabase extends AsyncTask<URL, Void, String> {
    private final String LOG_TAG = ZZZOldFetchMovieTaskWithNoDatabase.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name

    private MovieAdapter movieAdapter;
    private final Context mContext;


    /**
     *
     * @param context application context for context.getString(R.id etc) - to access the apps String resources
     * @param movieAdapter A reference to the apps GridAdpater to update data being displayed
     */
    ZZZOldFetchMovieTaskWithNoDatabase(Context context, MovieAdapter movieAdapter) {
        this.mContext = context;
        this.movieAdapter = movieAdapter;
    }

    /**
     *
     * @param params URL of movie Query to access internet
     * @return  movieJsonStr: a String containing the JSON data of all movies from internet
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
            e.printStackTrace(); //the URL was malformed
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
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
     * This is where the JSON result from the movie query is processed and put into the movieAdapter for display
     *
     * @param result is the JSON string from themoviedb.org
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //load the movie titles into the movieAdapter
        try {
            List<MovieDataProvider> movieData = new ArrayList<MovieDataProvider>(); //make a new list of all the movie data
            movieData.addAll(getMovieDataFromJson(result)); //add all the movie data from internet to list

            movieAdapter.clear(); //clear all the old movie data out
            //older way - add one movie at a time to the adapter
           /* for (MovieDataProvider individualMovie : movieData) { //add the movieData to the Adapter
                movieAdapter.add(individualMovie); //load the movieData into adapter
            }*/

            movieAdapter.addAll(movieData); //add all movies at once

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Take the complete string representing the entire initial movie call to themoviedb.org
     * and pull out the data needed for the grid view
     *
     * @param movieJsonStr the JSON of all the movie data from themoviedb.org
     * @return array of MovieDataProvider obects to hold the info for each movie
     * @throws JSONException
     */
    private List<MovieDataProvider> getMovieDataFromJson(String movieJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMBD_RESULTS = mContext.getString(R.string.movie_json_key_results);
        final String TMDB_POSTER_PATH = mContext.getString(R.string.movie_json_key_poster_path);
        final String TMDB_TITLE = mContext.getString(R.string.movie_json_key_title);
        //new ones for detail activity here
        final String TMDB_ORIGINAL_TITLE = mContext.getString(R.string.movie_json_key_original_title);
        final String TMDB_OVERVIEW = mContext.getString(R.string.movie_json_key_overview);
        final String TMDB_VOTE_AVERAGE = mContext.getString(R.string.movie_json_key_vote_average);
        final String TMDB_RELEASE_DATE = mContext.getString(R.string.movie_json_key_release_date);

        JSONObject movieJSON = new JSONObject(movieJsonStr); //create JSON object from input string
        JSONArray movieArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of movies

        MovieDataProvider[] movieDataProviderArrayFromJSON = new MovieDataProvider[movieArray.length()]; //array of Movie Data Providers

        int movieArrayLength = movieArray.length();


        for (int i = 0; i < movieArrayLength; i++) { //load the info needed into movieDataProviderArray
            JSONObject movieDetails = movieArray.getJSONObject(i);// Get the JSON object representing *one* movie

            URL moviePosterURL = makePosterURL(movieDetails.getString(TMDB_POSTER_PATH)); //get movie URL

            movieDataProviderArrayFromJSON[i] = new MovieDataProvider();
            movieDataProviderArrayFromJSON[i].setMovieTitle(movieDetails.getString(TMDB_TITLE));
            movieDataProviderArrayFromJSON[i].setMoviePosterUrl(String.valueOf(moviePosterURL));
            movieDataProviderArrayFromJSON[i].setOriginalTitle(movieDetails.getString(TMDB_ORIGINAL_TITLE));
            movieDataProviderArrayFromJSON[i].setOverview(movieDetails.getString(TMDB_OVERVIEW));
            movieDataProviderArrayFromJSON[i].setVoteAverage(movieDetails.getString(TMDB_VOTE_AVERAGE));
            movieDataProviderArrayFromJSON[i].setReleaseDate(movieDetails.getString(TMDB_RELEASE_DATE));
        }

        return Arrays.asList(movieDataProviderArrayFromJSON); //convert the movie data provider array into a list
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
