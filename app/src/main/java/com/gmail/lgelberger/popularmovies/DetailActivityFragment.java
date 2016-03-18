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
        Picasso.with(detailContext).load(movieURL).into((ImageView) posterThumbnailView);

        ((TextView) getActivity().findViewById(R.id.textview_title)).setText(movieDetails.getMovieTitle());
        ((TextView) getActivity().findViewById(R.id.textview_plot_synopsis)).setText(movieDetails.getOverview());
        ((TextView) getActivity().findViewById(R.id.textview_user_rating)).setText(movieDetails.getVoteAverage());
        ((TextView) getActivity().findViewById(R.id.textview_release_date)).setText(movieDetails.getReleaseDate());
    }
}
