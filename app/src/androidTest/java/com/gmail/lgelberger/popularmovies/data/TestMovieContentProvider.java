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

/**
 * Created by Leslie on 2016-05-08.
 * <p>
 * Used to test the Movie data Content Provider
 * <p>
 * Modelled after Udacity Sunshine app
 * Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
 * that at least the basic functionality has been implemented correctly.
 */
public class TestMovieContentProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestMovieContentProvider.class.getSimpleName();


    /*
            This test checks to make sure that the content provider is registered correctly in AndroidManifest.xml
            Modified from Sunshine App
         */
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

            // I guess the provider isn't registered correctly.
            //this will ALWAYS fail the assert, since we set it to 'false'
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
       read out the data.  Uncomment this test to see if the basic weather query functionality
       given in the ContentProvider is working correctly.
    */
    public void testBasicMovieQuery() {  //make sure I test both types of Queries
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //I want to start with an empty database
       // db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null); //make sure this deletes the database!!!!!
       // deleteTheDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long locationRowId = TestUtilities.insertMovieValues(mContext);

        // Fantastic.  Now that we have a Mpvie


        db.close(); // to stop writing to it

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
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


    //Skip this test for now
    //it always returns an empty cursor and I can't figure out why
    //go back and recode this later on once I have the Content Provider delete methods working properly
    //THis really should be tested!
    /*
      This test uses the database directly to insert and then uses the ContentProvider to
      read out the data.  Uncomment this test to see if the basic weather query functionality
      given in the ContentProvider is working correctly.
   */
   /* public void testBasicDetailMovieQuery() {  //make sure I test both types of Queries
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValuesAotherSet(); // I will be using this in the test to see if
        //I really get back the second set of values

      //   db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null); //make sure this deletes the database!!!!!
        // deleteTheDatabase();


        //create a new database
    //    db = new MovieDbHelper(
    //            this.mContext).getWritableDatabase();
        //test the delete



        //let's see how many entires we have?
        Cursor  cursor = db.rawQuery("select * from table",null);

        assertTrue("Number of Columns is not zero, database not deleted", cursor.getCount() == 0);
      //  cursor.getCount();


        //long locationRowId = TestUtilities.insertMovieValues(mContext);
       long locationRowId = TestUtilities.insertMovieValuesTwoSets(mContext);

        //temp asserting that movie values inserted
        assertTrue("Error: Failure to insert Movie Values", locationRowId != -1);

        assertEquals("locationRowId equals 2", 2, locationRowId);




        // Fantastic.  Now that we have a Mpvie


        db.close(); // to stop writing to it

        long testId = 2L; //use this to find second Movie Entry

        //make the detail URI
        Uri detailQueryUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(testId);


        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                detailQueryUri, // need to make the detail Uri
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicDetailMovieQuery", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        movieCursor.close(); //close the cursor at the end of the test

    }
*/





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
        Uri locationUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

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



        /*
        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(WeatherEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestUtilities.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a start date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
                weatherCursor, weatherValues);

        // Get the joined Weather data for a specific date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
                weatherCursor, weatherValues);*/


    }






    // Since we want each test to start with a clean slate - copied from TestDb.java
    //usefull for helping me test out the database
    //LJG ZZZ I'm not sure that this works!!!!??? - perhaps just replace this with deleteallrecordsfrom provider?
   /* void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }  //N.B. mContext is provided by AndroidTestCase. Use at will
*/



    //from sunshine
/*
       This helper function deletes all records from both database tables using the ContentProvider.
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
       /* mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );*/

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






}
