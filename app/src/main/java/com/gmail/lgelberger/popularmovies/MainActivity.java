package com.gmail.lgelberger.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnMovieSelectedListener {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";// Create a Tag to identify the Detail Fragment
    //it will be used in OnResume

    private Boolean mTwoPane; //used to indicate if we are using a two pane main layout (i.e. if it is a tablet)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //need to put this here to load the default preferences for app.
        //3 arguments   (context, xml file for settings, reload defaults more than once)
        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false); //trying to set default values for all of app


        /* Don't think I need snackbar - delete later if needed LJG
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        //See if our layout says this is a 2-Pane layout. If yes, set mTwoPane to true, and inflate the second pane fragment
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class)); //add this line to launch settings
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Call back from MainActivityFragment with detail Movie Uri when Movie Grid has been clicked
    Need to launch the Detail Fragement with this information.
    Will pass the information to Detail Movie Fragment
    Either with intent (if one pane view)
    Or pass it directly to the fragment (if two pane view)
     */
    @Override
    public void OnMovieSelected(Uri movieUri) {


        if (mTwoPane == false) { //Just one pane, start DetailActivity and send the Query Uri with an intent
            //Let's just try to make and intent like old days
            // Intent intentDetailActivity = new Intent(getActivity(), DetailActivity.class); //new intent with Detail Activity as recipient




            Intent intentDetailActivity = new Intent(this, DetailActivity.class); //new intent with Detail Activity as recipient
            intentDetailActivity.setData(movieUri);//setData puts a URI into the Intent - to be required by whomever received the intent
            startActivity(intentDetailActivity);
        }

        if (mTwoPane == true) { //then two panes - Update the detail Fragment with new data





        }

    }
}
