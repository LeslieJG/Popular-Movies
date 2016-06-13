package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
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


    static final int NUMBER_OF_BULK_INSERT_RECORDS_TO_INSERT = 10; // For making 10 values to test Bulk Insert

    /**
     * Used to test whether a cursor contains exactly the same values and the ContentValues passed in
     *
     * @param error          :Message to return if cursor doesn't match values of Content Values
     * @param valueCursor    :The Cursor to be tested
     * @param expectedValues :The Content Values that the cursor should contain for the test to pass
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
    static ContentValues createMovieValuesForOneMovie() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "The test Movie");
        movieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, "123456");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://BiteMe.com");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "Movie Poster Image");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The test Movie - Original Title");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Summary - the movie sucked!");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "1/102");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-04-05");

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, "Review");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, "Review 1 Author");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, "Review_2");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR, "Review 2 Author");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, "Review_3");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR, "Review 3 Author");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, "Video");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, "Video_2");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, "Video_3");


        return movieValues;
    }


    @NonNull
    static ContentValues createMovieValuesForAnotherMovie() {
        //extract to method

        //create a new movie // first built a new contentValue
        ContentValues movieSingleMovieValues = new ContentValues();
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "The Insert 1 Movie Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, "123456789");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://newSingleMove.com");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "MovieInsert1Picture - should be pic not text");
        //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The Insert 1 Movie ORIGINAL Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "The Insert 1 Movie Plot Synopsis");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "The Insert 1 Movie Vote Average");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "The Insert 1 Movie Release Date");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, "The Insert 1 Movie Movie Review");
        //The reviews may end up being a URL - this may also need to change
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, "The Insert 1 Movie Movie Review Author");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, "The Insert 1 Movie Movie Review 2");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR, "The Insert 2 Movie Movie Review 2 Author");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, "The Insert 1 Movie Movie Review 3");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR, "The Insert 1 Movie Movie Review  3 Author");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, "The Insert 1 Movie Video");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, "The Insert 1 Movie Video 2");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, "The Insert 1 Movie Video 3");
        //the Video may also change format

        return movieSingleMovieValues;
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

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1, "Movie Review: It was " + i + "thumbs up");
            //The reviews may end up being a URL - this may also need to change
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, "Movie Review Author It was Buddy" + i );
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2, "Movie Review 2: It was " + i + "thumbs up");
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, "Movie Review 2 Author It was Buddy" + i );
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3, "Movie Review 3: It was " + i + "thumbs up");
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR, "Movie Review 3 Author It was Buddy" + i );
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1, "Here is video number " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2, "Here is video number " + i + "Second Trailer");
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3, "Here is video number " + i  + "Third Trailer");
            //the Video may also change format

            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
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
        ContentValues testValues = TestUtilities.createMovieValuesForOneMovie();

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
