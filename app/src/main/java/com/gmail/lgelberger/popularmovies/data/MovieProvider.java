package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leslie on 2016-05-03.
 *
 * Starting to make a content provider to deal with my locally stored movies (favourites) in Popular Movies Part 2
 */
public class MovieProvider extends ContentProvider{


    ///////////////////////////////////////////////////////////////
    // URI Matcher Stuff

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;


    static final int MOVIE = 100;
    static final int MOVIE_DETAIL = 101;

    //Weather URI's to be matched to these constants
   // static final int WEATHER = 100;
  //  static final int WEATHER_WITH_LOCATION = 101;
   // static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
   // static final int LOCATION = 300;

    /////////////////////////////////////////////////////////


    //Android Provides URI Matcher to assist in matching URIs to queries(?)
/*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        //for matching a query for the whole movie database
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        //for matching a query for just the details of ONE movie
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);

        //below are examples from Sunshine app
       // matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
      //  matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
       // matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

       // matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);


        return matcher;
    }






    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }





}
