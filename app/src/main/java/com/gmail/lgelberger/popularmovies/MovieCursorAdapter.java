package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Leslie on 2016-05-19.
 * <p>
 * Trying to make a cursor adapter to populate the GridView of movies, instead of my old MovieAdapter which extended ArrayAdapter
 * <p>
 * Modified from https://coderwall.com/p/fmavhg/android-cursoradapter-with-custom-layout-and-how-to-use-it
 */
public class MovieCursorAdapter extends CursorAdapter {

    //constructor
    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /**
     * For ViewHolder pattern. I need a static inner class to hold my views
     * So that they are only assigned ONCE, and they don't have to use view.findViewById every
     * time they are displayed.
     * This will cache of the children views for a movie grid item.
     */
    public static class ViewHolder {
        public final TextView movieTitle;
        public final ImageView moviePosterView;

        public ViewHolder(View view) {
            movieTitle = (TextView) view.findViewById(R.id.grid_item_movies_textview);
            moviePosterView = (ImageView) view.findViewById(R.id.grid_item_poster);
        }
    }


    /*
               Remember that these views are reused as needed.
      */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //this is xml layout for each row (or grid item)
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies_layout, parent, false);

        //For ViewHolder pattern add reference to a ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder); //view now has the object viewHolder attached to it

        return view;
    }


    /*
            This is where we fill-in the views with the contents of the cursor.

            set the elements of the view
         */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        //get a reference to ViewHolder from the current (passed in) View that we are working with
        ViewHolder viewHolder = (ViewHolder) view.getTag();


        //set up the views - using the viewHolder this time!
        TextView movieTitle = viewHolder.movieTitle; //(TextView) view.findViewById(R.id.grid_item_movies_textview);
        ImageView moviePosterView = viewHolder.moviePosterView; //(ImageView) view.findViewById(R.id.grid_item_poster);

        //get the data needed to put into views - using Cursor Projection  )
        String movieTitleString = cursor.getString(MainActivityFragment.COL_MOVIE_TITLE); //get data to put into text view
        String moviePosterUrl = cursor.getString(MainActivityFragment.COL_MOVIE_POSTER_URL); //get data to put into image view

        //set the views to proper values
        movieTitle.setText(movieTitleString);
        Picasso.with(context).load(moviePosterUrl).into(moviePosterView);

    }


   /* @Override
   //These are used to give a different view for each position of the list view. Important feature
   //not being used by me
    public int getItemViewType(int position) {
      //  return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT; // count must be higher than all the view
    }
    */


}
