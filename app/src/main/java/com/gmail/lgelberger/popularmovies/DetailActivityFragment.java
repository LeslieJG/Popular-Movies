package com.gmail.lgelberger.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    // From http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getting information from intent once the activity is created (after activity and fragment are created)

        MovieDataProvider movieDetails = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_details_intent_key));

        Context detailContext = getContext();
        String movieURL = movieDetails.getMoviePosterUrl();
        View posterThumbnailView = getActivity().findViewById(R.id.imageview_poster_thumbnail);

      //  Picasso.with(detailContext).load(movieURL).into((ImageView) posterThumbnailView);


        /*
        From Popular Movies 1 code review:
        Here, to learn more, you can also try to use error and placeholder in Picasso
        here to avoid crashing down due to empty string values or null values.
        Before the error placeholder is shown, your request will be retried three times.
        sample codes (from Picasso documentation):

Picasso.with(context)
    .load(url)
    .placeholder(R.drawable.user_placeholder)
    .error(R.drawable.user_placeholder_error)
    .into(imageView);

    The quality of the data from this movie database is really good.
    However, when you want to use Picasso to show image fetched from other places (
    for example, Spotify), there is much higher chance for you to get Picasso an empty
    string value or null value. This is just for your information. :)
         */

        Picasso.with(detailContext)
                .load(movieURL)
                //.placeholder(R.drawable.placeholder) //put a placeholder in place of image while it is loading
                .placeholder(R.drawable.placeholder_error_vertical) //put a placeholder in place of image while it is loading
                .error(R.drawable.placeholder_error_vertical) //put a picture if there is an error retrieving file
                .into((ImageView) posterThumbnailView);


        ((TextView) getActivity().findViewById(R.id.textview_title)).setText(movieDetails.getMovieTitle());
        ((TextView) getActivity().findViewById(R.id.textview_plot_synopsis)).setText(movieDetails.getOverview());
        ((TextView) getActivity().findViewById(R.id.textview_user_rating)).setText(movieDetails.getVoteAverage());
        ((TextView) getActivity().findViewById(R.id.textview_release_date)).setText(movieDetails.getReleaseDate());
    }
}
