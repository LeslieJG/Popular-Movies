package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Leslie on 2016-03-03.
 * <p>
 * Modelled on the custom adapter at
 * https://plus.google.com/s/ListView%20with%20Custom%20Adapter%20Prabeesh%20R%20K/top
 * <p>
 * <p>
 * The pattern id as folloew
 * if the convertView (which is just a strange parameter name for views that may be recycled)
 * does not exist, create it.
 * Then use the appropriate data (determined by the current position
 * to configure the view, which is then returned.
 * <p>
 * Have to override a bunch of methods
 * add
 * add all
 */
public class MovieAdapter extends ArrayAdapter {
    List movieList = new ArrayList(); //the list of all the movieData that we will be putting into gridView
    Context context; //needed for picasso

    //Constructor - this is the one Prabeesh recommends - but you will need to add the list separately (as opposed to when it is constructed)
    public MovieAdapter(Context context, int resource) {
        super(context, resource);

        this.context = context; //added this for picasso to work
    }


    //Constructor - OK use this one, with a list
    public MovieAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context; //added this for picasso to work
    }


    //override add to allow my custom list to be added to , and not just the default list
    @Override
    public void add(Object object) {
        super.add(object);
        movieList.add(object); //added this line to add to the movieList
    }

    //override addAll, to allow an entire arraylist to be added to my custom list
    @Override
    public void addAll(Collection collection) {
        super.addAll(collection);
        movieList.addAll(collection);// added this line to deal with an addAll command
    }

//to solve some extra adding problems
    @Override
    public void clear() {
        super.clear();
        movieList.clear(); //just ensuring that I'm really clearing the data
    }

    //override getCount to return the number of elements in my custom list
    @Override
    public int getCount() {
        //return super.getCount();
        return this.movieList.size(); //I added this to return the size of the movieList ArrayList
    }

    //override to return items in my custom list
    @Override
    public Object getItem(int position) {
        //return super.getItem(position);
        return this.movieList.get(position);
    }

    /**
     * needed to populate a gridview item. This is new and NOT an override
     * create the view types that exist in the gridview layout that this adapter will
     * be populating
     */
    static class DataHandler {
        ImageView moviePoster;
        TextView movieTitle;
    }


    /**
     * return each grid item (or row for listView) of data to the gridView
     * <p>
     * if the convertView (which is just a strange parameter name for views that may be recycled)
     * does not exist, create it.
     * Then use the appropriate data (determined by the current position
     * to configure the view, which is then returned.
     *
     * @param position    gridPosition
     * @param convertView the gridView we are populating
     * @param parent      the parent of the gridView
     * @return the convertView that is passed in, but modified with all the data for the grid
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem = convertView; //the convertView is the gridView item that is passed in
        DataHandler handler; //will contain the types of views in my custom view

        //if no griditem, make it
        if (gridItem == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //now we have to inflate the layout
            //arguments (custom layout resource, viewGroup, boolean)
            gridItem = inflater.inflate(R.layout.grid_item_movies_layout, parent, false);

            //now initialize  each view components
            handler = new DataHandler();
            handler.moviePoster = (ImageView) gridItem.findViewById(R.id.grid_item_poster);
            handler.movieTitle = (TextView) gridItem.findViewById(R.id.grid_item_movies_textview);

            //now attatch each view component into grid item
            gridItem.setTag(handler);
        } else {
            // if gridItem exisits already, no need to make a new one
            //just populate our handler with the items that already exist in this gridItem
            handler = (DataHandler) gridItem.getTag();
        }

        //Configure the View

        //now we have to get each object from the MovieDataProvider (i.e. each movie will have it's own object)
        //we need to get the data
        MovieDataProvider dataProvider = (MovieDataProvider) this.getItem(position);

        String moviePosterURLString = dataProvider.getMoviePosterUrl();

        //now set data resources
        // handler.moviePoster.setImageResource(dataProvider.getMoviePosterResource());
        Picasso.with(context).load(moviePosterURLString).into(handler.moviePoster);
        handler.movieTitle.setText(dataProvider.getMovieTitle());

        // return the view
        return gridItem; //gridItem is the convertView that is passed in, now that it is
        //set with the correct information -return it
        //return super.getView(position, convertView, parent); //default return method - overriden


    }
}
