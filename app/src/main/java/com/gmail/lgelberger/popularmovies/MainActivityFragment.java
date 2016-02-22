package com.gmail.lgelberger.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> mMovieAdapter; //need this as global variable within class so all subclasses can access it


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

        //create/ initialize an adapter that will populate each list item
        mMovieAdapter = new ArrayAdapter<String>(
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
        listView.setAdapter(mMovieAdapter);

        //adding click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listItemClicked, long itemClicked) {

                Toast.makeText(getActivity(), "Item clicked is number " + itemClicked + " and the contents of the item are " + mMovieAdapter.getItem((int) itemClicked), Toast.LENGTH_LONG).show(); //this works and gets the item number
            }
        });


        // return inflater.inflate(R.layout.fragment_main, container, false); - old original default code --> delete
        return rootView;
    }
}
