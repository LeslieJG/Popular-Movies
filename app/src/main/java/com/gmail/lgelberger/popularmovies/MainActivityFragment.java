package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> mMovieAdapterForGridTextOnly; //need this as global variable within class so all subclasses can access it
    MovieAdapter movieAdapter;//declare custom MovieAdapter
    List<MovieDataProvider> movieData = Collections.synchronizedList(new ArrayList<MovieDataProvider>());  //threadsafe - to store all the movie data

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging

    SharedPreferences sharedPref; //declaring shared pref here
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.preferences, false); //trying to set default values for all of app
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);  //infalte the fragment view

        movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies_layout);  //initialize custom gridView adapter
        // now bind the adapter to the actual gridView so it knows which view it is populating
        // Get a reference to the gridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        //set adapter to gridview
        //gridView.setAdapter(mMovieAdapterForGridTextOnly); //just for textview
        gridView.setAdapter(movieAdapter); //my custom adapter

        //adding click listener for grid
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int gridItemClicked, long grItemClicked) {
                //  Toast.makeText(getActivity(), "Item clicked is number " + gridItemClicked , Toast.LENGTH_SHORT).show(); //for debugging

                MovieDataProvider selectedMovieFromGrid = new MovieDataProvider();
                selectedMovieFromGrid = movieData.get(gridItemClicked);
                //  Toast.makeText(getActivity(), "The Movie Selected Title is: " + selectedMovieFromGrid.getMovieTitle(), Toast.LENGTH_LONG).show(); //for debugging

                Intent intentDetailActivity = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                intentDetailActivity.putExtra(getString(R.string.movie_details_intent_key), selectedMovieFromGrid);
                startActivity(intentDetailActivity);
            }
        });


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext()); //initializing sharedPref with the defaults


        prefListener = new MyPreferenceChangeListener();
       /* prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() { //making a OnSharedPreferencesChanged LIstener
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                //  if (key == getString(R.string.movie_sort_order_key)) {
                if (key.equals("movie_sort_order_key")) {
                    updateMovieGridImages(); //update the entire Grid from internet when sort order preference is changed
                }
            }
        };*/
        sharedPref.registerOnSharedPreferenceChangeListener(prefListener); //registering the listener

        updateMovieGridImages(); //update the entire Grid from internet - when Fragment created

        return rootView;
    }

    /**
     * My Own OnSharedPreferenceChangeListener
     */
    private class MyPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (isAdded()) { //just makes sure that fragment is attached to an Actvity
                if (key.equals(getString(R.string.movie_sort_order_key))) {
                    updateMovieGridImages(); //update the entire Grid from internet when sort order preference is changed
                }
            }
        }
    }



    /**
     * sed to connect to network and load images into the gridView
     */
    private void updateMovieGridImages() {
        final String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key); //to be able to look at sort order preference
        String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

        URL movieQueryURL = makeMovieQueryURL(movieSortOrder);

        //check for internet connectivity first
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "No Internet Connection. Connect to internet and restart app", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(movieQueryURL);
        }
    }

    /**
     * Makes URL to access API to get movie info
     * <p/>
     * movie shoud now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @return URL for themoviedb.org
     */
    private URL makeMovieQueryURL(String sortOrder) {
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(getString(R.string.movie_query_movie))
                .appendPath(sortOrder)
                .appendQueryParameter(getString(R.string.movie_query_key_api_key), getString(R.string.api_key))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * Makes URL to access API to get movie poster
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

        Uri builtUri = Uri.parse(getString(R.string.poster_url_base)).buildUpon()
                .appendPath(getString(R.string.poster_url_poster_size))
                .appendEncodedPath(posterPath) //needed encoded path instead of path, as it wasn't creating properly
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * *made the networking stuff an AsyncTask for now to get it off main thread
     * <p/>
     * Should check for network connectivity before making network calls - Have not
     * implememnted this yet!!!!
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
     * Param String will be the URL to call the moviedb
     * <p/>
     * This class modelled after the "Sunshine" AsyncTask
     */
    public class FetchMovieTask extends AsyncTask<URL, Void, String> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName(); //used for logging - to keep the log tag the same as the class name

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
                movieData.clear();
                movieData.addAll(getMovieDataFromJson(result));

                movieAdapter.clear(); //clear all the old movie data out

                for (MovieDataProvider individualMovie : movieData) { //add the movieData to the Adapter
                    movieAdapter.add(individualMovie); //load the movieData into adapter
                }

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
            final String TMBD_RESULTS = getString(R.string.movie_json_key_results);
            final String TMDB_POSTER_PATH = getString(R.string.movie_json_key_poster_path);
            final String TMDB_TITLE = getString(R.string.movie_json_key_title);
            //new ones for detail activity here
            final String TMDB_ORIGINAL_TITLE = getString(R.string.movie_json_key_original_title);
            final String TMDB_OVERVIEW = getString(R.string.movie_json_key_overview);
            final String TMDB_VOTE_AVERAGE = getString(R.string.movie_json_key_vote_average);
            final String TMDB_RELEASE_DATE = getString(R.string.movie_json_key_release_date);

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
    }





}











