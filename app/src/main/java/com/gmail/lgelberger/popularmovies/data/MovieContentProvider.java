package com.gmail.lgelberger.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Leslie on 2016-05-03.
 * <p/>
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


    /*

    For us,
    query will be the most complex of the required content provider methods.
    We've given you the bones, of the query operation.

    As you can see, we use our SURI nature object,
    once again to switch on the type of URI.

    Note that each response from this function, will return a cursor that
    corresponds to the incoming query as defined by the URI.

    This is the only function where we will have to fill out a different
    response for every type of URI in the content provider,
    as several of them are used in queries only.


    We'll see more on this later, when we cover inserting and
    updating the database.

    Uncomment the test basic weather query function within test provider and
    run this code when you're finished, to make sure all of this is working well.

    NEED TO TEST THIS!!!!!

     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // return null;


        Cursor retCursor; //the cursor with the database information to be returned

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        switch (sUriMatcher.match(uri)) {

            //

            //  content://com.gmail.lgelberger.popularmovies/movie
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, //table name to query
                        projection, // leaving "columns" null just returns all the columns.
                        selection,// columns for "where" clause
                        selectionArgs, // values for "where" clause
                        null,       // columns to group by
                        null,      // columns to filter by row groups
                        sortOrder //sort order
                );
                break;
            }

            //LJG ZZZ Note, Movie_detail query still needs to be properly tested!!!!!

            // content://com.gmail.lgelberger.popularmovies/movie/#
            // "movie/#"
            case MOVIE_DETAIL: {
                // select * from movie where _id = 3
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, //table name to query
                        projection, // leaving "columns" null just returns all the columns.
                        MovieContract.MovieEntry._ID + " = ? ",                     //selection,// columns for "where" clause - where  (Just need to look for _ID column)
                        new String[]{MovieContract.MovieEntry.getIdFromUri(uri)},                 //selectionArgs, // values for "where" clause  (when it equals the one we are looking for  -as specified by the last bit of uri)
                        //// --- This needs to be an array not a single value
                        //also it may need to be a LONG to work....let's try testing it
                        null,       // columns to group by
                        null,      // columns to filter by row groups
                        sortOrder); //sort order
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        /* This causes the cursor to register a content observer, to watch for
        changes that happen to that URI and any of its descendants. This allows the content provider, to easily tell the UI when the cursor
        changes, on operations like a database insert or update. */
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;


    }


    /*
    Inserts a row into database

    For inserts we simply want to make sure that the correct data ends up in the correct table
    No need to deal with the non-base Uri's.
    Only the base Uri is needed
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(_id);//  WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null); //this line notifies changes to all children Uri's as well
        return returnUri;

    }





//returns the rows modified instead of Uri
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            /*case LOCATION:
                rowsDeleted = db.delete(
                        WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;*/
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }







    //returns the rows modified instead of Uri
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
               // normalizeDate(values);
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            /*case LOCATION:
                rowsUpdated = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;*/
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null); //this line notifies changes to all children Uri's as well
        }
        return rowsUpdated;


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















  /*  @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }*/

    //THis is specifically for testing content providers
    // This should be called at the end of content provider tests to properly shut down the
    //content provider.
    // This doesn't happen in tests, but the content provider normally shuts down
    //on it's own in normal usage, just not tests
    //
    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}







