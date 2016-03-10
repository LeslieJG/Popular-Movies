package com.gmail.lgelberger.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Leslie on 2016-03-10.
 *
 * Modelled off http://developer.android.com/guide/topics/ui/settings.html#Fragment
 *
 * Note: A PreferenceFragment doesn't have a its own Context object.
 * If you need a Context object, you can call getActivity().
 * However, be careful to call getActivity() only when the fragment
 * is attached to an activity.
 * When the fragment is not yet attached, or was detached during the
 * end of its lifecycle, getActivity() will return null.
 *
 *
 */



public class SettingsFragment extends PreferenceFragment {
    //added to have reference to movie_sort_order_key
    public static final String MOVE_SORT_ORDER_KEY = String.valueOf((R.string.movie_sort_order_key));





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);



    }





}
