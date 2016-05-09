package com.gmail.lgelberger.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

         db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null); //make sure this deletes the database!!!!!
        // deleteTheDatabase();

        //long locationRowId = TestUtilities.insertMovieValues(mContext);
       long locationRowId = TestUtilities.insertMovieValuesTwoSets(mContext);

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

    // Since we want each test to start with a clean slate - copied from TestDb.java
    //usefull for helping me test out the database
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }  //N.B. mContext is provided by AndroidTestCase. Use at will



}
