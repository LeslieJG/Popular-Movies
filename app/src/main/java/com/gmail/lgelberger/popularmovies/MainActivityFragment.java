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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> mMovieAdapterForGridTextOnly; //need this as global variable within class so all subclasses can access it
    MovieAdapter movieAdapter;//declare custom MovieAdapter

    //perhaps make this an arraylist or just list
   // MovieDataProvider[] movieData; //will contain array of all the movie data needed - writen in AsyncTask onPostExecute() - for now;

    List<MovieDataProvider> movieData = new ArrayList<MovieDataProvider>();


    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging


    /**
     * constructor
     */
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //try actually creating stuff in the fragment before the fragment returns the inflated view
        final String MOVIE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key); //to be able to look at sort order preference


        // Create some dummy data for the Grid View
        // Here's a sample movie list
        String[] data = {
                "MOvie 1",
                "Movie 2",
                "Movie 3",
                "Movie 4",
                "Movie 5",
                "Movie 6",
                "Movie 7",
                "Movie 8",
                "Movie 9",
                "Movie 10",
                "Movie 11"
        };
        final List<String> movieData = new ArrayList<String>(Arrays.asList(data));
        // Toast.makeText(getActivity(), "MainActivity Fragment has dummy data", Toast.LENGTH_LONG).show();  //for debugging

        //let me do the same with pics
        int[] dummyPics = {R.drawable.test_movie_poster_1,
                R.drawable.test_movie_poster_2,
                R.drawable.test_movie_poster_3,
                R.drawable.test_movie_poster_4,
                R.drawable.test_movie_poster_1,
                R.drawable.test_movie_poster_2,
                R.drawable.test_movie_poster_3,
                R.drawable.test_movie_poster_4,
                R.drawable.test_movie_poster_1,
                R.drawable.test_movie_poster_2,
                R.drawable.test_movie_poster_3};

        List dummyPicList = new ArrayList(Arrays.asList(dummyPics));


        //infalte the fragment view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        /*ImageView myTestImageView = (ImageView) rootView.findViewById(R.id.test_imageview);
        myTestImageView.setImageResource(R.drawable.test_movie_poster);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185//gokfO8RVKhfn8jNMyUBaaMgLjP8.jpg").into(myTestImageView);
*/


        //create/ initialize an adapter that will populate each grid item
       /* mMovieAdapterForGridTextOnly = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.grid_item_movies_layout, // The name of the layout ID File.
                R.id.grid_item_movies_textview, // The ID of the textview to populate.
                movieData); //the ArrayList of data*/


        //try my custom adapter here
        movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies_layout);  //this one works well without picaso

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


                //launch the detail activity with explicit intent

    /////////////////IMPORTANT/////////////
                /////////////// ADD movieDataProvider (correct position in array) to intent to detail fragment can interpret it
                //  Intent detailActivityStarter = new Intent()

                Intent intentDetailActivity = new Intent(new Intent(getContext(), DetailActivity.class));



              //intentDetailActivity.putExtra("movie_details_key", movieData.get(gridItemClicked));

                startActivity(intentDetailActivity);




                Toast.makeText(getActivity(), "Item clicked is number " + gridItemClicked +" and the contents of the item are "
                        + movieAdapter.getItem((int) gridItemClicked), Toast.LENGTH_LONG).show();  //this works and gets the item number
            }
        });


        //*******************WORKING HERE ******************
        //make the movie query url based on Shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String movieSortOrder = sharedPref.getString(MOVIE_SORT_ORDER_KEY, "");

        /*Log.v(LOG_TAG, "In Main activity fragment, The movie sort order is " + movieSortOrder);

        if (movieSortOrder == null){
            Log.v(LOG_TAG, "IMPORTANT, sort order is NULL!!!!!!");
        }*/


        //URL movieQueryURL = makeMovieQueryURL();
        URL movieQueryURL = makeMovieQueryURL(movieSortOrder);
        Log.v(LOG_TAG, "The movie URL is " + movieQueryURL);
        Toast.makeText(getActivity(), "The movie URL is " + movieQueryURL, Toast.LENGTH_LONG).show();

//**********************END WORKING HERE ********************************
// it works but doesn't refresh when settings have been changed.
        //get it to refresh for setting change


        //check for internet connectivity first
        //check to see if device is connected to network
        //code snippet from http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "No Internet Connection. Connect to internet and try again", Toast.LENGTH_LONG).show();
            //no internet connection so no need to continue - must find a way of running this code when there is internet!!!!!!
        } else { // if there is internet, get the movie date
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(movieQueryURL);
        }

        // return inflater.inflate(R.layout.fragment_main, container, false); - old original default code --> delete
        return rootView;
    }

    /**
     * Makes URL to access API to get movie info
     * <p>
     * movie shoud now look like this
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @return URL for themoviedb.org
     */
    private URL makeMovieQueryURL(String sortOrder) {
        URL url = null; //url to be built


       /* Uri builtUri = Uri.parse(getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(getString(R.string.movie_query_movie))
                .appendPath(getString(R.string.movie_query_popular))
                .appendQueryParameter(getString(R.string.movie_query_key_api_key), getString(R.string.api_key))
                .build();*/

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
     * Makes URL to access API to get movie info
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
     * <p>
     * Should check for network connectivity before making network calls - Have not
     * implememnted this yet!!!!
     * <p>
     * <p>
     * Params, the type of the parameters sent to the task upon execution.
     * Progress, the type of the progress units published during the background computation.
     * Result, the type of the result of the background computation.
     * <p>
     * <p>
     * Param String will be the URL to call the moviedb
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
                //MovieDataProvider[] movieData = getMovieDataFromJson(result);
                movieData = getMovieDataFromJson(result); //originally declared at beginning of MainActivityFragment. Now being initialized
                movieAdapter.clear(); //clear all the old movie data out

               //as array
                /*for (int i = 0; i < movieData.length; i++) { //add the movieData to the Adapter
                    movieAdapter.add(movieData[i]); //load the movieData into adapter
                }*/

                // movie data as list
                for (int i = 0; i < movieData.size(); i++) { //add the movieData to the Adapter
                    movieAdapter.add(movieData.get(i)); //load the movieData into adapter
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

            //load the info needed into movieDataProviderArray
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieDetails = movieArray.getJSONObject(i);// Get the JSON object representing the movie

                URL moviePosterURL = makePosterURL(movieDetails.getString(TMDB_POSTER_PATH)); //get movie URL

                //TRYING not to use arrays
                movieDataProviderArrayFromJSON[i] = new MovieDataProvider();
                movieDataProviderArrayFromJSON[i].setMovieTitle(movieDetails.getString(TMDB_TITLE));
                movieDataProviderArrayFromJSON[i].setMoviePosterUrl(String.valueOf(moviePosterURL));

                //new stuff here
                movieDataProviderArrayFromJSON[i].setOriginalTitle(movieDetails.getString(TMDB_ORIGINAL_TITLE));
                movieDataProviderArrayFromJSON[i].setOverview(TMDB_OVERVIEW);
                movieDataProviderArrayFromJSON[i].setVoteAverage(TMDB_VOTE_AVERAGE);
                movieDataProviderArrayFromJSON[i].setReleaseDate(TMDB_RELEASE_DATE);

            }

            return Arrays.asList(movieDataProviderArrayFromJSON); //convert the movie data provider array into a list
        }
    }
}
