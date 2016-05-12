package com.gmail.lgelberger.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

// LJG ZZZ make sure all tests will see if contentObserver is refistered correctly!

/**
 * Created by Leslie on 2016-05-08.
 * <p/>
 * Used to test the Movie data Content Provider
 * <p/>
 * Modelled after Udacity Sunshine app
 * Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
 * that at least the basic functionality has been implemented correctly.
 */
public class TestMovieContentProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestMovieContentProvider.class.getSimpleName();
    static private final int NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT = 10; // For making 10 values to test Bulk Insert

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    /*
       This helper function deletes all records from the database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
       /* delete any other table in database if needed;*/

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }
     /* deleteAllRecords, used to just delete the database, using the following functions
     void deleteTheDatabase() {
            mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        }  //N.B. mContext is provided by AndroidTestCase. Use at will
    */


    /*
    This test checks to make sure that the content provider is registered correctly in AndroidManifest.xml
    odified from Sunshine App */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieContentProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieContentProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // if content provider not registered correctly, provide error message:
            assertTrue("Error: MovieContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    /*
   This test doesn't touch the database.  It verifies that the ContentProvider returns
   the correct type for each type of URI that it can handle. It checks whether each type
   is a type CONTENT_URI (multple records)
   or a type CONTENT_TYPE_URI (single record)

            */
    public void testGetType() {
        // content://com.gmail.lgelberger.popularmovies/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        long testMovieID = 1357L;
        // content://com.gmail.lgelberger.popularmovies/movie/1357
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieUriWithAppendedID(testMovieID));
        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie/1357
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }


    /*
       This test uses the database directly to insert and then uses the ContentProvider to
       read out the data.  Use this test to see if the basic movie query functionality
       given in the ContentProvider is working correctly.
    */
    public void testBasicMovieQuery() {  //make sure I test both types of Queries
        // LJG MAKe sure I test content observers for query!!!!

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long locationRowId = TestUtilities.insertMovieValues(mContext); //movie inserted into database
         db.close(); // to stop writing to it

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,  //basic query uri
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        movieCursor.close(); //close the cursor at the end of the test
    }



    /*
       This test uses the database directly to insert and then uses the ContentProvider to
       read out the data.  Use this test to see if the detail movie query functionality
       given in the ContentProvider is working correctly.
    */
    public void testDetailMovieQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //create a lot of table values
        ContentValues[] testValuesArray = createBulkInsertMovieValues(); //this should be a table of 10 values

        // Register a content observer for our bulk insert.
        //   TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        //    mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // do the bulk insert
        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, testValuesArray);


        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.

        //  movieObserver.waitForNotificationOrFail();
        //   mContext.getContentResolver().unregisterContentObserver(movieObserver);


        //quick confirmation that we actually inserted the correct number
        assertEquals(insertCount, NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT);

        //this test is just making sure that the bulk insert worked
        //getting cursor of entire table
        Cursor entireTableCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " ASC"  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(entireTableCursor.getCount(), NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        entireTableCursor.moveToFirst();
        for (int i = 0; i < NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT; i++, entireTableCursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    entireTableCursor, testValuesArray[i]);
        }

        //preparing to get Movie number "7" back
        final int RECORD_I_WANT = 7;

        //move cursor to
        entireTableCursor.moveToFirst();
        entireTableCursor.move(RECORD_I_WANT); //move cursor to row that I will compare it with

        //now see if I can match the correct contentValue with the correct cursor row
        TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating RECORD_THAT_I_WANT from basicQuery ",
                entireTableCursor, testValuesArray[RECORD_I_WANT]);

        //I'm not closing the entireTableCursor, becuase it will be used in a later test


        // see if I can get a single row cursor from a basic Query - doing it manually (without using Movie_detail URI
        ////THis is incorrect way - find right way and implement it in Movie ContentProvider
        //  String selection = MovieContract.MovieEntry._ID + " = ?";
        // String selectionArgs[] = {"7"};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ?";
        String selectionArgs[] = {"Movie Title " + RECORD_I_WANT}; //RECORD_I_WANT is "7"

        //creating a cursor with just one movie in it using Basic MOVIE uri, but making the query exactly the same
        //as the MOVIE_DETAIL uri
        Cursor singleMovieTitleFromBasicQueryCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                selectionArgs,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );


        //see if this cursor has any data in it

        assertTrue(" The singleMovieTitleFromBasicQueryCursor is empty - a single row was NOT returned",
                singleMovieTitleFromBasicQueryCursor.moveToFirst());
        //this one works!!!!

        //make sure it matches the movie Title we want
        TestUtilities.validateCurrentRecord("singleMovieTitleFromBasicQueryCursor.  Error validating RECORD_THAT_I_WANT from singleMovieTitleFromBasicQueryCursor ",
                singleMovieTitleFromBasicQueryCursor, testValuesArray[RECORD_I_WANT]);

        singleMovieTitleFromBasicQueryCursor.close(); //close the cursor


        ////////////////////////////////////////////////////
        // OK the point of movieDetail is to be able to get a single record back given just the _id
        //let's try to get the _id of a movie and see what it is!!!!

        //create a new movie // first built a new contentValue
        ContentValues movieSingleMovieValues = new ContentValues();
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "The Insert 1 Movie Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, "123456789");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://newSinlgeMove.com");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "MovieInsert1Picture - should be pic not text");
        //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The Insert 1 Movie ORIGINAL Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "The Insert 1 Movie Plot Synopsis");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "The Insert 1 Movie Vote Average");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "The Insert 1 Movie Release Date");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "The Insert 1 Movie Movie Review");
        //The reviews may end up being a URL - this may also need to change
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "The Insert 1 Movie Video");
        //the Video may also change format

        //just confirm that the EntireTableCursor still exists
        //may be OK to delete this line at some point
        assertEquals("The Entire Table cursor has more than " + NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT + " entries in it!!!! - It shouldn't",
                NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT,
                entireTableCursor.getCount());

        //insert movie
        //then get the URI back from a single insert
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieSingleMovieValues);

        //WOw, using the below statement I just found out that the _id in the database is NOT sequential - good to know!
        //assertTrue(movieInsertUri.toString(), false); //Do not delete this commented line - good info for later!

        //confirm that there are 11 entries in database now
        //get a full table cursor again
        entireTableCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(entireTableCursor.getCount(), NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT + 1);


        //and try to use THAT EXACT id from Uri for a general query
        String idFromUri = MovieContract.MovieEntry.getIdFromUri(movieInsertUri); //id returned is a String not a LONG (just fyi)
        selection = MovieContract.MovieEntry._ID + " = ?"; //select _ID
        selectionArgs[0] = idFromUri; // the specific _ID I want is the one I just inserted
        Cursor oneMovieGeneralQuery = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                selectionArgs,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        //confirm valid row
        assertTrue(" The oneMovieGeneralQuery is empty - a single row was NOT returned",
                oneMovieGeneralQuery.moveToFirst());
        //confirm that it is the same as what I inserted in
        TestUtilities.validateCurrentRecord("oneMovieGeneralQuery.  Error validating record from from oneMovieGeneralQuery ",
                oneMovieGeneralQuery, movieSingleMovieValues);

        //assert the Content Type is correct for detail query - but get type is already tested!!

        //then use it again for a detail query
        Long idFromUriAsLong = Long.parseLong(idFromUri);
        Uri movieDetailUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(idFromUriAsLong);
        //test to make sure that movieDetailUri is of type MOVIE_DETAIL
        String movieDetailUritype = mContext.getContentResolver().getType(movieDetailUri);
        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie/1357
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, movieDetailUritype); // confirm this Uri is of type MOVUE_DETAIL


        //selection = MovieContract.MovieEntry._ID + " = ?"; //select _ID
        //selectionArgs[0] = idFromUri; // the specific _ID I want is the one I just inserted
        //note for Movie Detail I *JUST* have to give it a Uri with the _id appeneded on the end
        Cursor movieDetailQuery = mContext.getContentResolver().query(
                movieDetailUri,
                null, // leaving "columns" null just returns all the columns.
                null, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                null,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        //confirm valid row
        assertTrue(" The movieDetailQuery is empty - a single row was NOT returned",
                movieDetailQuery.moveToFirst());
        //confirm that it is the same as what I inserted in
        TestUtilities.validateCurrentRecord("movieDetailQuery.  Error validating record from from movieDetailQuery ",
                movieDetailQuery, movieSingleMovieValues);




        //close entireTableCursor
        entireTableCursor.close();
    }



    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri singleMovieInsertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(singleMovieInsertedUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider(); //guarantees there are rows in database

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our weather delete.
       /* TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, weatherObserver);*/

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();
        // weatherObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        //   mContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }


    /*

    //LJG ZZZ rename this file and figure out what it does and modify it for pop movies
    it SHOULD be testing update() in MovieContentProvider

            This test uses the provider to insert and then update the data. Uncomment this test to
            see if your update location is functioning correctly.
         */
    public void testUpdate() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values); //insert a row of values
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "A Different Movie Title"); //just updating a value to something different

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)}); //update the values we put in at beginning of method
        assertEquals(count, 1); //make sure that no new entries added/ just updated

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues); //check to see that the values are updated properly

        cursor.close();
    }



    /*
    Creates the bulk insert values for 10 movies
     */
    static ContentValues[] createBulkInsertMovieValues() {    //don't need a location id - this is not Sunshine!
        ContentValues[] returnContentValues = new ContentValues[NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT]; //create an array of Movie Entries

        for (int i = 0; i < NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Movie Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://biteme.com/" + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "This should be a picture, not text_" + i);
            //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!

            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Movie Original Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "This movie sucked, times " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, i + " out of 10");
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "Release date the year " + i + " A.D");

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "Movie Review: It was " + i + "thumbs up");
            //The reviews may end up being a URL - this may also need to change

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "Here is video number " + i);
            //the Video may also change format

            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }


    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // Now we can bulkInsert some Movies
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT); //make sure we inserted exactly the correct number of rows

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " ASC"  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
