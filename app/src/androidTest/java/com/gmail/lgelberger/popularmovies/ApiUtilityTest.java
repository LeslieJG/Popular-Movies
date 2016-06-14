package com.gmail.lgelberger.popularmovies;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Leslie on 2016-06-14.
 */
public class ApiUtilityTest extends AndroidTestCase {


   /* public void testMakeReviewsAPIQueryURL() throws Exception {

    }


    public void testMakeTrailersAPIQueryURL() throws Exception {

    }


    public void testUpdateDatabaseFromAPI() throws Exception {

    }*/


    //Need to get Movie ID from this
    //  http://api.themoviedb.org/3/movie/293660/reviews?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    //
    //  http://api.themoviedb.org/3/movie/293660/videos?api_key=547bc9d14e2d25a3e3429d2f7c8292db
    public void testGetApiMovieIdFromUri() throws Exception {
        String movieId;

        String reviewUriString = "http://api.themoviedb.org/3/movie/293660/reviews?api_key=123456789";
        Uri reviewUri = Uri.parse(reviewUriString);


        movieId = ApiUtility.getApiMovieIdFromUri(reviewUri);
        assertNotNull("Error: Movie ID is not - not properly parsed from Review URI", movieId);
        assertEquals("Error: Movie ID not properly extracted from Review Uri", "293660",movieId );

        String trailerUriString = "http://api.themoviedb.org/3/movie/293660/videos?api_key=123456789";
        Uri trailerUri = Uri.parse(trailerUriString);
        movieId = ApiUtility.getApiMovieIdFromUri(trailerUri);
        assertNotNull("Error: Movie ID is not - not properly parsed from Trailer URI", movieId);
        assertEquals("Error: Movie ID not properly extracted from Trailer Uri", "293660",movieId );
    }
}