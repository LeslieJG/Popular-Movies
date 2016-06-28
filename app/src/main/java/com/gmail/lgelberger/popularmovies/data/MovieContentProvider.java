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
 * Content Provider to access the movies database.
 * There are two tables that can be accessed - MovieEntry for general API call storage
 * and FavouriteEntry for the table of locally stored favourite movies
 */
public class MovieContentProvider extends ContentProvider {
    /*
    NB: Here is a good order to write the content provider. Before content provider is written, some parts of tests
    may have to access database directly,  and not just the contentResolver

    -make URI constants for matching
    -Make inner class UriMatcher (Test UriMatcher)
    -register content provider in Android.Manifest (testProviderRegistry)
    -onCreate - instantiate database helper
    -getType - using UriMatcher (Test getType)
    -query (Test Basic and other Queries)
    -Insert (testInsert)
    -update (test update)
    -delete (test delete)
    -bulkInsert (test bulkInsert)
     */

    private static final String LOG_TAG = "MovieContentProvider";

    ////////////URI Matcher Stuff///////////////////////////////////////////////////

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //The database used by this content provider
    private MovieDbHelper mOpenHelper;

    // Movie URI's to be matched to these constants
    static final int MOVIE = 100;  //content://com.gmail.lgelberger.popularmovies/movie
    static final int MOVIE_DETAIL = 101;//com.gmail.lgelberger.popularmovies/movie/3423

    static final int FAVOURITE = 300; //content://com.gmail.lgelberger.popularmovies/favourite
    static final int FAVOURITE_DETAIL = 301;//content://com.gmail.lgelberger.popularmovies/favourite/2468

    /**
     * UriMatcher will match each URI to the MOVIE nad MOVIE_DETAIL integer constants defined above.
     * You can test this by uncommenting the testUriMatcher test within TestUriMatcher.
     *
     * @return UriMatcher
     */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); //set the initial matcher to NO_MATCH in case we can't find a match
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        ////////////////////// Accessing Movie Entry Table ///////////////////////
        //for matching a query for the whole movie database
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        //for matching a query for just the details of ONE movie
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);

        ////////////////////// Accessing Favourite Entry Table ///////////////////////
        //for matching a query for the whole movie database
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE, FAVOURITE);

        //for matching a query for just the details of ONE movie
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE + "/#", FAVOURITE_DETAIL);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext()); //initialize new Database Helper (to access the database)
        return true; //indicate that onCreate has been run
    }


    /*
    * @param uri :Query Uri (should match one of the URI's in UriMatcher()
     * @param projection :Which Columns in Table to Query
     * @param selection :Columns for "where" clause
     * @param selectionArgs : Values for "where" clause
     * @param sortOrder : Sort order of returned column data
     * @return :Cursor with table data from query
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //for debugging 2 pane layout on tablet
       // Log.v(LOG_TAG, " Content Provider Query uri is " + uri);

        Cursor retCursor; //the cursor with the database information to be returned

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        switch (sUriMatcher.match(uri)) {

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

            // content://com.gmail.lgelberger.popularmovies/movie/#
            // "movie/#"
            case MOVIE_DETAIL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME, //table name to query
                        projection, // leaving "columns" null just returns all the columns.
                        MovieContract.MovieEntry._ID + " = ? ",  //selection -  columns for "where" clause - where  (Just need to look for _ID column)
                        new String[]{MovieContract.MovieEntry.getIdFromUri(uri)}, //selectionArgs - values for "where" clause  (when it equals the one we are looking for  -as specified by the last bit of uri)
                        null,       // columns to group by
                        null,      // columns to filter by row groups
                        sortOrder); //sort order
                break;
            }


            //  content://com.gmail.lgelberger.popularmovies/favourite
            // "favourite"
            case FAVOURITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME, //table name to query
                        projection, // leaving "columns" null just returns all the columns.
                        selection,// columns for "where" clause
                        selectionArgs, // values for "where" clause
                        null,       // columns to group by
                        null,      // columns to filter by row groups
                        sortOrder //sort order
                );
                break;
            }

            // content://com.gmail.lgelberger.popularmovies/favourite/#
            // "favourite/#"
            case FAVOURITE_DETAIL: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavouriteEntry.TABLE_NAME, //table name to query
                        projection, // leaving "columns" null just returns all the columns.
                        MovieContract.FavouriteEntry._ID + " = ? ",  //selection -  columns for "where" clause - where  (Just need to look for _ID column)
                        new String[]{MovieContract.FavouriteEntry.getIdFromUri(uri)}, //selectionArgs - values for "where" clause  (when it equals the one we are looking for  -as specified by the last bit of uri)
                        null,       // columns to group by
                        null,      // columns to filter by row groups
                        sortOrder); //sort order
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* This causes the cursor to register a content observer, to watch for
        changes that happen to that URI and any of its descendants. This allows the content provider,
        to easily tell the UI when the cursor changes, on operations like a database insert or update. */
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
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case FAVOURITE: {
                long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavouriteEntry.buildMovieUriWithAppendedID(_id);
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
        if (null == selection) selection = "1";

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case FAVOURITE:
                rowsDeleted = db.delete(
                        MovieContract.FavouriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
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
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_DETAIL:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        MovieContract.MovieEntry._ID + " = ? ",  //selection -  columns for "where" clause - where  (Just need to look for _ID column)
                        new String[]{MovieContract.MovieEntry.getIdFromUri(uri)}); //selectionArgs - values for "where" clause  (when it equals the one we are looking for  -as specified by the last bit of uri)
                break;
            case FAVOURITE:
                rowsUpdated = db.update(MovieContract.FavouriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVOURITE_DETAIL:
                rowsUpdated = db.update(MovieContract.FavouriteEntry.TABLE_NAME,
                        values,
                        MovieContract.FavouriteEntry._ID + " = ? ",  //selection -  columns for "where" clause - where  (Just need to look for _ID column)
                        new String[]{MovieContract.FavouriteEntry.getIdFromUri(uri)}); //selectionArgs - values for "where" clause  (when it equals the one we are looking for  -as specified by the last bit of uri)
                break;

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

    We use the UriMatcher we built earlier to match the given uri
    against the expressions we've compiled in.

    For each match, we return the types that we've defined in the movie contract.
    Remember that the key information that this conveys, is weather the content uri
    will be returning a database cursor containing a single record type item (CONTENT_ITEM_TYPE), or
    multiple records type directory. (CONTENT_TYPE)

    Content providers can also be used to return other kinds of data
    than just database cursors.
    For example, if we wanted the content provider to return JPEG images for
    a content uri, we would have this function return the standard mime type,
    image/jpeg.
     */
    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri); // Use the Uri Matcher to determine what kind of URI this is.

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE; //MOVIE Uri returns multiple records type directory - for grid display info
            case MOVIE_DETAIL:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE; //returns a single record type (for movie detail page)

            case FAVOURITE:
                return MovieContract.FavouriteEntry.CONTENT_TYPE; //MOVIE Uri returns multiple records type directory - for grid display info
            case FAVOURITE_DETAIL:
                return MovieContract.FavouriteEntry.CONTENT_ITEM_TYPE; //returns a single record type (for movie detail page)

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

                //If the actual movie poster jpg is to be stored
                //will have to add one for Movie Poster JPG to return MIME type image/jpg
                //this needs more research. Implement after the rest of the Content Provider is up and running
        }
    }


    /*
    Allow for bulk inserting of rows into database
    It is faster and less wear and tear on flash memory
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();  //if this is not done, the inserts won't be saved in database
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case FAVOURITE:
                db.beginTransaction();
                int returnFavouriteCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnFavouriteCount++;
                        }
                    }
                    db.setTransactionSuccessful();  //if this is not done, the inserts won't be saved in database
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnFavouriteCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

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







