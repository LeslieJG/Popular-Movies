package com.gmail.lgelberger.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
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
    ArrayAdapter<String> mMovieAdapterForList; //need this as global variable within class so all subclasses can access it
    ArrayAdapter<String> mMovieAdapterForGrid; //need this as global variable within class so all subclasses can access it

    //used for logging - to keep the log tag the same as the class name
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //try actually creating stuff in the fragment before the fragment returns the inflated view
        // Create some dummy data for the ListView. (Or Grid View)
        // Try listview first
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


        //Now let's try to get real data

        //build URL
        //trying to make URL
        URL movieURL = makeURL();
        Log.v(LOG_TAG, "The movie URL is " + movieURL);
        Toast.makeText(getActivity(), "The URL is " + movieURL, Toast.LENGTH_LONG).show(); //this works and gets th


        // call API for data


        //create/ initialize an adapter that will populate each list item
        mMovieAdapterForList = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_movies, // The name of the layout ID.
                R.id.list_item_movies_textview, // The ID of the textview to populate.
                movieData); //the ArrayList of data
        // new ArrayList<String>()); //the ArrayList of data


        //infalte the fragment view
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // now bind the adapter to the actual listView so it knows which view it is populating
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMovieAdapterForList);

        //adding click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listItemClicked, long itemClicked) {

                Toast.makeText(getActivity(), "Item clicked is number " + itemClicked + " and the contents of the item are " + mMovieAdapterForList.getItem((int) itemClicked), Toast.LENGTH_LONG).show(); //this works and gets the item number
            }
        });


        //*** Now do the same with GridView
        //create/ initialize an adapter that will populate each grid item
        //is this the correct way to make a gridView adapter??????? - research!
        mMovieAdapterForGrid = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.grid_item_movies, // The name of the layout ID File.
                R.id.grid_item_movies_textview, // The ID of the textview to populate.
                movieData); //the ArrayList of data


        // now bind the adapter to the actual gridView so it knows which view it is populating
        // Get a reference to the gridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapterForGrid);

        //adding click listener for grid
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int gridItemClicked, long grItemClicked) {
                Toast.makeText(getActivity(), "Item clicked is number " + grItemClicked + " and the contents of the item are " + mMovieAdapterForGrid.getItem((int) gridItemClicked), Toast.LENGTH_LONG).show(); //this works and gets the item number
            }
        });


        // return inflater.inflate(R.layout.fragment_main, container, false); - old original default code --> delete
        return rootView;
    }

    /**
     * Makes URL to access API to get movie info
     *
     * @return URL for themoviedb.org
     */
    private URL makeURL() {
        URL url2 = null; //url to be built

        //copied from SUnshine
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        // parameters for the URL weather call
        String format = "json";
        String units = "metric";
        int numDays = 7;

        final String MOVIE_BASE_URL =
                "http://api.themoviedb.org/3";
        // final String QUERY_PARAM = "q";
        // final String FORMAT_PARAM = "mode";
        //  final String UNITS_PARAM = "units";
        //  final String DAYS_PARAM = "cnt";
        //  final String APPID_PARAM = "APPID";

        Uri.Builder builtUri2 = new Uri.Builder();
        builtUri2.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "popularity.desc")
                .appendQueryParameter("api_key", getString(R.string.api_key));
        try {
            url2 = new URL(builtUri2.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url2;
    }
}
