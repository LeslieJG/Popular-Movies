package com.gmail.lgelberger.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //don't need Snackbar
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //may produce 'java.lang.NullPointerException' LJG ZZZ Deal with this?


        //dynamically add the detail fragment
        if (savedInstanceState == null){ //if the fragment hasn't already been created

            //get intent data and pass it to fragment
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle(); //this will hold the MovieQuery URI
            Uri movieDetailQueryUri = getIntent().getData(); //get the movieDetailQuery Uri from intent
            arguments.putParcelable(DetailActivityFragment.MOVIE_DETAIL_URI, movieDetailQueryUri); // put movieDetailQueryUri into arguments with key MOVIE_DETAIL_URI
            DetailActivityFragment detailFragment = new DetailActivityFragment(); //make a new DetailActivityFragment
            detailFragment.setArguments(arguments); //add the arguments (movie query Uri) to the fragment
            // Now dynamically load fragment into DetailActivity
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment) //add a new DetailActivityFragment
                    // to the place where it should be displayed (R.id.movie_detail_container)
                    .commit();
        }


    }



    //added the following to make the overflow icon on toolbar
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
}
