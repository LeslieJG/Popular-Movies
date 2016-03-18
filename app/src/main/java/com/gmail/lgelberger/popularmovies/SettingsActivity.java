package com.gmail.lgelberger.popularmovies;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Leslie on 2016-03-08.
 *
 * Modelled Off http://developer.android.com/guide/topics/ui/settings.html#Fragment
 *
 *
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the SettingsFragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}