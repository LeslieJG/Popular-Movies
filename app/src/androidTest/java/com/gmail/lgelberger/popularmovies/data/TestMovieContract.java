package com.gmail.lgelberger.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Leslie on 2016-05-08.
 */
public class TestMovieContract extends AndroidTestCase {


    private static final long TEST_MOVIE_ID = 123456L;  // Some random id number for now

    public void testBuildMovieUriWithAppendedID () {

        Uri movieUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);

        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
               Long.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.gmail.lgelberger.popularmovies/movie/" + TEST_MOVIE_ID);
    }
}

