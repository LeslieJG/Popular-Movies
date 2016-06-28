package com.gmail.lgelberger.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by Leslie on 2016-03-08.
 * <p/>
 * Modelled Off http://developer.android.com/guide/topics/ui/settings.html#Fragment
 */

public class SettingsActivity extends AppCompatActivity { //changed from extends Activity to allow to action bar   AppCompatActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //below needed for toolbar
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //making a toolbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //allows up button

        // Display the SettingsFragment as the main content.
        getFragmentManager().beginTransaction()
                // .replace(android.R.id.content, new SettingsFragment())
                .replace(R.id.movie_setting_container, new SettingsFragment())
                .commit();
    }
}