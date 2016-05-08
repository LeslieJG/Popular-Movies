package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leslie on 2016-05-03.
 * <p>
 * Starting to make a content provider to deal with my locally stored movies (favourites) in Popular Movies Part 2
 */
public class MovieContentProvider extends ContentProvider {


    ///////////////////////////////////////////////////////////////
    // URI Matcher Stuff

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //The database used by this content provider
    private MovieDbHelper mOpenHelper;

    // Movie URI's to be matched to these constants
    static final int MOVIE = 100;
    static final int MOVIE_DETAIL = 101;
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
        mOpenHelper = new MovieDbHelper(getContext()); //initialize new Database Helper (to access the database)
        return true; //indicate that onCreate has been run
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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


    /*
     getType is a good way to review the content URI's that will be handled

    The Key information that this conveys is whether

    We partially filled out the getType function for you.
    We use the urimatcher we built earlier to match the given uri
    against the expressions we've compiled in.
    For each match, we return the types that we've defined in the weather contract.
    Remember that the key information that this conveys, is weather the content uri
    will be returning a database cursor containing a single record type item, or
    multiple records type directory.

    Content providers can also be used to return other kinds of data
    than just database cursors.
    For example, if we wanted the content provider to return JPEG images for
    a content uri, we would have this function return the standard mime type,
    image/jpeg.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE; //MOVIE Uri returns multiple records type directory - for grid display info
            case MOVIE_DETAIL:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE; //returns a single record type (for movie detail page)
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

                //LJG ZZZ
                //will have to add one for Movie Poster JPG to return MIME type image/jpg
                //this needs more research. Implement after the rest of the Content Provider is up and running
        }
    }


}
