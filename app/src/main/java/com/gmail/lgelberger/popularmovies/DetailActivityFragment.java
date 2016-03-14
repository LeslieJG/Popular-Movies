package com.gmail.lgelberger.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }


    /*
    added code from http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment
    to help get intent data from MainActivityFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // I just needed to add this line to the fragment Activity onCreate() method(othwewise the getArguments() would return null):

        /*getFragmentManager().beginTransaction()
                .add(android.R.id.content, frag).commit();*/


        //Intent intent = this.getIntent();
        //POJO myPOJO = (POJO)intent.getParcelableExtra("MyPojo");


        //WOrking in getting my movie data into fragment

        /*Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        String tmp = extras.getString("myKey");
*/




        //this may be good to keep here and get the intent extra stuff here
       // Bundle extras = getActivity().getIntent().getExtras();


        //Intent testINtent = getI


        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    // From http://stackoverflow.com/questions/15392261/android-pass-dataextras-to-a-fragment


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getting information from intent once the activity is created (after activity and fragment are created)

       // Toast.makeText(getActivity(), "in Detail Fragment onActivityCreated", Toast.LENGTH_LONG).show(); //for debugging




        MovieDataProvider testMovieAgain = getActivity().getIntent().getParcelableExtra(getString(R.string.movie_details_intent_key));
        String testMovieAgainHasTtitle;
        testMovieAgainHasTtitle = testMovieAgain.getMovieTitle();
        Toast.makeText(getActivity(), "THe movie I'm getting in Detail Fragment has title:  " + testMovieAgain.getMovieTitle(), Toast.LENGTH_LONG).show(); //for debugging

        if (testMovieAgain == null){
            Toast.makeText(getActivity(), "DAMNIT all, the movie stuff is null AGAIN!", Toast.LENGTH_LONG).show(); //for debugging
        }



/*

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras ==null){
            Toast.makeText(getActivity(), "bundle extras is null!", Toast.LENGTH_LONG).show(); //for debugging
        } else{

            MovieDataProvider bundleMovie = extras.getParcelable("movie");

            Toast.makeText(getActivity(), "bundle extras is NOT null!, it is " + bundleMovie.getMovieTitle(), Toast.LENGTH_LONG).show(); //for debugging
        }

*/


/*

       // MovieDataProvider testMovieDetails = extras.getParcelable(String.valueOf(R.string.movie_details_intent_key));
        MovieDataProvider testMovieDetails = extras.getParcelable("movie");
        if (testMovieDetails == null){
            Toast.makeText(getActivity(), "testMovieDetails is NULL!", Toast.LENGTH_LONG).show(); //for debugging
        } else {
            Toast.makeText(getActivity(), "testMovieDetails is NOT NULL!", Toast.LENGTH_LONG).show(); //for debugging
        }
*/





/*
        Bundle bundle = getArguments();
        if (bundle != null) {
            MovieDataProvider movieDataDetail = bundle.getParcelable(String.valueOf(R.string.movie_details_intent_key));

            Toast.makeText(getActivity(), "movieDataDetail came in ---- Movie Title is: " + movieDataDetail.getMovieTitle(), Toast.LENGTH_LONG).show(); //for debugging

            // listMusics = bundle.getParcelableArrayList("arrayMusic");
            //listMusic.setAdapter(new MusicBaseAdapter(getActivity(), listMusics));
        } else {
            Toast.makeText(getActivity(), "bundle data is null!", Toast.LENGTH_LONG).show(); //for debugging
        }





        Object testObject =         getActivity().getIntent().getExtras();*/


    }
}
