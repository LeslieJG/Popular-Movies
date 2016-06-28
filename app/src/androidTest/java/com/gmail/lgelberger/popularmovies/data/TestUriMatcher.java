package com.gmail.lgelberger.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Leslie on 2016-05-08.
 * To Test UriMatcher in MovieContentProvider (the Content Provider for Movies)
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 12L;

    //content://com.gmail.lgelberger.popularmovies/movie
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID_DIR = MovieContract.MovieEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);

    private static final Uri TEST_FAVOURITE_DIR = MovieContract.FavouriteEntry.CONTENT_URI;
    private static final Uri TEST_FAVOURITE_WITH_ID_DIR = MovieContract.FavouriteEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);
    /*
        This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that the ContentProvider can handle.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieContentProvider.MOVIE);
        assertEquals("Error: The MOVIE WITH ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID_DIR), MovieContentProvider.MOVIE_DETAIL);

        assertEquals("Error: The FAVOURITE URI was matched incorrectly.",
                testMatcher.match(TEST_FAVOURITE_DIR), MovieContentProvider.FAVOURITE);
        assertEquals("Error: The FAVOURITE WITH ID URI was matched incorrectly.",
                testMatcher.match(TEST_FAVOURITE_WITH_ID_DIR), MovieContentProvider.FAVOURITE_DETAIL);

    }
}
