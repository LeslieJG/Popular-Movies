package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

    //declare gridview here as opposed to OnCreateView?
    //declare custom MovieAdapter
    MovieAdapter movieAdapter;

    //used for logging - to keep the log tag the same as the class name
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //try actually creating stuff in the fragment before the fragment returns the inflated view

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
        List<String> movieData = new ArrayList<String>(Arrays.asList(data));
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

        //create/ initialize an adapter that will populate each grid item
        mMovieAdapterForGridTextOnly = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.grid_item_movies_layout, // The name of the layout ID File.
                R.id.grid_item_movies_textview, // The ID of the textview to populate.
                movieData); //the ArrayList of data


        //try my custom adapter here
        movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies_layout);

        //load data into my custom adapter
        int i = 0; //loop counter
        for (String title : data) { //loop through all the data
            MovieDataProvider movieDataProvider = new MovieDataProvider(dummyPics[i], title); //make a new dataprovider object
            // with the image resource and the title string for dummy data

            movieAdapter.add(movieDataProvider);// add each movie into the adapter's own list

            i++; //increment loop counter
        }


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
                //  Toast.makeText(getActivity(), "Item clicked is number " + grItemClicked + " and the contents of the item are "
                //         + mMovieAdapterForGridTextOnly.getItem((int) gridItemClicked), Toast.LENGTH_LONG).show();
                //this works and gets the item number

                Toast.makeText(getActivity(), "Item clicked is number " + grItemClicked + " and the contents of the item are "
                        + movieAdapter.getItem((int) gridItemClicked), Toast.LENGTH_LONG).show();
                //this works and gets the item number
            }
        });


// ZZZZZZ
        //for the real data make a list of MovieDataProvider Objects
        //for loop - iterate over all the incoming data
        //in for loop -
        //      MovieDateProvider = new MovieDataProvider(moviePoster resource[i], movieTitle[i])
        //

        //initialize Custom MovieAdapter that will populate each grid item
      /*  movieAdapter = new MovieAdapter(getActivity(), // The current context (this activity)
                R.layout.grid_item_movies_layout, // The name of the layout ID File.);
                movieData); //the ArrayList of data   --//error it needs an array of objects not a list - how to fix?*/

        //prabeesh's way of initializing
        //get Activity - he has it as getApplicationContext()

      /*  movieAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movies_layout);*/

        //for loop to add each of the MovieDataProviderObjects
        // for .......      movieAdapter.add(movieDataProvider)
        //or just make the moveDataProvider list and just add it as part of the initialization
        // i.e. just make sure the constructor can take care of it
        // or just addAll


        //now set the adapter to work with gridView
        //gridView.setAdapter(movieAdapter);


        ///ZZZZZZZZ


        //try the network code here to see if it works
        URL movieQueryURL = makeMovieQueryURL();
        Log.v(LOG_TAG, "The movie URL is " + movieQueryURL);
        Toast.makeText(getActivity(), "The movie URL is " + movieQueryURL, Toast.LENGTH_LONG).show();


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
     *
     * @return URL for themoviedb.org
     */
    private URL makeMovieQueryURL() {
        URL url = null; //url to be built

        Uri builtUri = Uri.parse(getString(R.string.movie_query_url_base)).buildUpon()
                .appendPath(getString(R.string.movie_query_discover))
                .appendPath(getString(R.string.movei_query_movie))
                .appendQueryParameter(getString(R.string.movie_query_key_sort_by), getString(R.string.movie_query_value_popularity))
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
        //  Combining these three parts gives us a final url of http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

        Uri builtUri = Uri.parse(getString(R.string.poster_url_base)).buildUpon()
                .appendPath(getString(R.string.poster_url_poster_size))
                .appendPath(posterPath)
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
     * Param String will be the URL to call the moviedb
     */
    public class FetchMovieTask extends AsyncTask<URL, Void, String> {
        //used for logging - to keep the log tag the same as the class name
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName(); //name of FetchWeatherTask class

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

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

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
                    // buffer.append(line + "\n");  //restore this line after deleting line below
                    // buffer.append(line + (System.getProperty("line.separator"))); //experimental delete when done
                    //buffer.append(line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                //Log.v(LOG_TAG, "movie URL is " + movieJsonStr );

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
         * @param result is the JSON string from themoviedb.org
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //for now, just use a verbose tag for debugging
           // Log.v(LOG_TAG, "The results from the network call is \n " + result); //for debugging

            //right now just load the movie titles into the movieAdapter
            try {
                String[] movieTitles = getMovieDataFromJson(result);

                for (int i = 0; i < movieTitles.length; i++) {
                    //load these titles into adapter
                    //   mMovieAdapterForGridTextOnly.add(movieTitles[i]); //confirm that this is how to add the data tothe adapter
                }
                //right now just put make the movie titles into an array list
                List<String> movieTitleArrayList = new ArrayList<String>(Arrays.asList(movieTitles));

                //now clear the gridview adapter
                mMovieAdapterForGridTextOnly.clear();
                mMovieAdapterForGridTextOnly.addAll(movieTitleArrayList);

                //need to put data into movie adapter (and NOT moveAdapterforGridTextONly.

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Take the complete string representing the entire initial movie call to themoviedb.org
         * and pull out the data we need for the grid view
         * <p/>
         * FOr now I'm just returning the String array of movie titles
         */
        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException { // throws JSONException
            // These are the names of the JSON objects that need to be extracted.
            final String TMBD_RESULTS = getString(R.string.movie_json_key_results);
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_TITLE = getString(R.string.movie_json_key_title);

            JSONObject movieJSON = new JSONObject(movieJsonStr); //create JSON object from input string
            JSONArray movieArray = movieJSON.getJSONArray(TMBD_RESULTS); //create JSON array of movies

            String[] moviePosterRelativePath = new String[movieArray.length()];  //to store relative path of movie poster
            String[] movieTitle = new String[movieArray.length()]; //to store the movie title

            for (int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject movieDetails = movieArray.getJSONObject(i);

                // realtive path to movie jpg is in "poster_path"
                //  moviePosterRelativePath[i] = movieDetails.getString(TMDB_POSTER_PATH);

                //get movie title
                movieTitle[i] = movieDetails.getString(TMDB_TITLE);
            }

            /*for (String s : resultStrs) { //just for debugging
                Log.v(LOG_TAG, "Forecast entry: " + s); //go through entire array and log all contents
            }*/
            return movieTitle;
        }
    }
}
