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

    //test the decoder function too!
    public void testGetIdFromUri () {
        Uri movieUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);

        assertEquals("Error: GetIfFromUri not returning proper ID value",
                MovieContract.MovieEntry.getIdFromUri(movieUri),
            Long.toString(TEST_MOVIE_ID));
    }

    ///////////////////////////////////////////////////////////////
    //Testing the Favourites Table

    public void testBuildFavourieMovieUriWithAppendedID () {
        Uri movieUri = MovieContract.FavouriteEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);

        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
                Long.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.gmail.lgelberger.popularmovies/favourite/" + TEST_MOVIE_ID);
    }

    //test the decoder function too!
    public void testGetFavouriteIdFromUri () {
        Uri movieUri = MovieContract.FavouriteEntry.buildMovieUriWithAppendedID(TEST_MOVIE_ID);

        assertEquals("Error: GetIfFromUri not returning proper ID value",
                MovieContract.FavouriteEntry.getIdFromUri(movieUri),
                Long.toString(TEST_MOVIE_ID));
    }


}

