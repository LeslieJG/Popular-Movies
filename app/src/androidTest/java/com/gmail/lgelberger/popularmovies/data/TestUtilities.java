package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.gmail.lgelberger.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;


/*
    Modelled After Udacty Sunshine Test Utilities Function
    These are functions and some test data to make it easier to test the database and
    Content Provider.
 */
public class TestUtilities extends AndroidTestCase {


    /**
     * Used to test whether a cursor contains exactly the same values and the ContentValues passed in
     *
     * @param error          :Message to return if cursor doesn't match values of Content Values
     * @param valueCursor    :The Cursor to be tested
     * @param expectedValues :The Content Valuess that the cursor should contain for the test to pass
     */
    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }


    /*
    Validate current record grabs the set of value pairs from the content values that were inserted.
    It then iterates through them,using cursor.getColumnIndex to get the index of each column in the record set by name.
    We need the column index to get data from the cursor.
    Note the projections are always return in order.
    So if we specify a projection,we can safely use the indexes from our projection array without having to look them up like this.
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    /**
     * Use  to create some default movieValues for the database and content Provider tests tests.
     *
     * @return Content Values containing the contents of one movie
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "The test Movie");
        movieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, "123456");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://BiteMe.com");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "Movie Poster Image");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The test Movie - Original Title");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Summary - the movie sucked!");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "1/102");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-04-05");

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "Review");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "Video");

        return movieValues;
    }

    /**
     * Inserts a movie into the database
     *
     * @param context
     * @return : Row Id of Movie inserted into database
     */
    static long insertMovieValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }


    /*
        The functions inside of TestMovieContentProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that was grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        //uncomment when polling check needed LJG ZZZ
        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
