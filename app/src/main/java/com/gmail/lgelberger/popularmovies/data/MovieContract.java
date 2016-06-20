package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leslie on 2016-03-22.
 * <p>
 * Defines table and column names for regular movie database
 * Modelled after the class "WeatherContract" in the sunshine app lesson 4A
 * https://github.com/udacity/Sunshine-Version-2/blob/4.03_define_contract_constants/app/src/main/java/com/example/android/sunshine/app/data/WeatherContract.java
 */
public class MovieContract {

    ////////////////////The following are for the Content provider - as modelled on the Udacity Sunshine app //////////

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.gmail.lgelberger.popularmovies";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // Note: These paths match the database tables
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVOURITES = "favourite";



   //////////The following are the inner classes that define the database tables ////////////

    /*
    Inner Class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns {//BaseColumns allows the _ID String to be already included

        public static final String TABLE_NAME = "movies";
        //Movie details to store
        public static final String COLUMN_MOVIE_TITLE = "title"; //String containing name of movie (stored as String)
        public static final String COLUMN_API_MOVIE_ID = "api_movie_id"; //API Movie ID (Stored as Int)
        public static final String COLUMN_MOVIE_POSTER_URL = "movie_poster_url";//String containing url of movie poster (Stored as String)
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";//Movie poster is stored as (ZZZ - I HAVE NO FUCKING CLUE? Image????? Bitmap?)
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";//Original title stored as String
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis"; //Plot Synopsis/ Overview (stored as String)
        public static final String COLUMN_VOTE_AVERAGE = "vote_average"; //Vote average for movie as per external DB of vote average (stored as String// - possibly Float??? ZZZ)
        public static final String COLUMN_RELEASE_DATE = "release_date";//Movie release date (stored as String)
        public static final String COLUMN_MOVIE_REVIEW_1 = "movie_review_1"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_1_AUTHOR = "movie_review_1_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_REVIEW_2 = "movie_review_2"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_2_AUTHOR = "movie_review_2_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_REVIEW_3 = "movie_review_3"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_3_AUTHOR = "movie_review_3_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_VIDEO_1 = "movie_video"; //movie videos - stored as YouTube movie id key
        public static final String COLUMN_MOVIE_VIDEO_2 = "movie_video_2"; //movie videos - stored as YouTube movie id key
        public static final String COLUMN_MOVIE_VIDEO_3 = "movie_video_3"; //movie videos - stored as YouTube movie id key

        /////////////////The following are needed for the Content Provider////////////////////////////////////////////////////////////

        // Create Content Uri that represents base location for this table
        //This way the content uri can be built for each table in a content provider
        //This just makes the base content uri, and adds the movie path to it.
        //  content://com.gmail.lgelberger.popularmovies/movie
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();


        //functions to help build content provider queries
        //helpful to have so that other parts of app don't have to know how to do this. It
        // can be self contained
        //The below method just adds an ID to the end of the URL
        //this is used to just get ONE row of data (instead of querying the whole  table)
        //  content://com.gmail.lgelberger.popularmovies/movie/ #
        public static Uri buildMovieUriWithAppendedID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //ZZZ LJG I think this is for making cursors?
        // for each of the return types, we ask for 1 record (item) or a list of records (dir)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;



        ///////////////////////////////////////////////////////////////////////
        //All of the below is to make content provider query URIs
        //designed to make life easier to query the Content Provider

        //Decoder functions

        //used to find the final id tag appended to end of uri
        //example  // content://com.gmail.lgelberger.popularmovies/movie/999
        //this method should return "999"
        public static String getIdFromUri(Uri uri) {
            //return uri.getPathSegments().get(1); //returns the second part of uri, second after the authority part
            return uri.getLastPathSegment(); //return whatever is at the end of Uri
        }
    }


    /*
    Inner Class that defines the contents of some other, as yet unknown table
    I left this in here for now in case it is needed later on
     */
    public static final class FavouriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourites";


        //Movie details to store for favourites - Identical Column values to movies table, just keeping
        //my regular movies and my favourite movies separate.
        public static final String COLUMN_MOVIE_TITLE = "title"; //String containing name of movie (stored as String)
        public static final String COLUMN_API_MOVIE_ID = "api_movie_id"; //API Movie ID (Stored as Int)
        public static final String COLUMN_MOVIE_POSTER_URL = "movie_poster_url";//String containing url of movie poster (Stored as String)
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";//Movie poster is stored as (ZZZ - I HAVE NO FUCKING CLUE? Image????? Bitmap?)
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";//Original title stored as String
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis"; //Plot Synopsis/ Overview (stored as String)
        public static final String COLUMN_VOTE_AVERAGE = "vote_average"; //Vote average for movie as per external DB of vote average (stored as String// - possibly Float??? ZZZ)
        public static final String COLUMN_RELEASE_DATE = "release_date";//Movie release date (stored as String)
        public static final String COLUMN_MOVIE_REVIEW_1 = "movie_review_1"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_1_AUTHOR = "movie_review_1_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_REVIEW_2 = "movie_review_2"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_2_AUTHOR = "movie_review_2_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_REVIEW_3 = "movie_review_3"; //movie reviews - stored as String
        public static final String COLUMN_MOVIE_REVIEW_3_AUTHOR = "movie_review_3_author"; //movie review author - stored as String
        public static final String COLUMN_MOVIE_VIDEO_1 = "movie_video"; //movie videos - stored as YouTube movie id key
        public static final String COLUMN_MOVIE_VIDEO_2 = "movie_video_2"; //movie videos - stored as YouTube movie id key
        public static final String COLUMN_MOVIE_VIDEO_3 = "movie_video_3"; //movie videos - stored as YouTube movie id key



        /////////////////////////////////////////////////////////////////////////////

        // The following are needed for the Content Provider
        // Create Content Uri that represents base location for this table
        //This way the content uri can be built for each table in a content provider
        //This just makes the base content uri, and adds the table path to it.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        //functions to help build content provider queries
        //helpful to have so that other parts of app don't have to know how to do this. It
        // can be self contained
        //The below method just adds an ID to the end of the URL
        //this is used to just get ONE row of data (instead of querying the whole  table)
        //  content://com.gmail.lgelberger.popularmovies/movie/ #
        public static Uri buildMovieUriWithAppendedID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        //ZZZ LJG I think this is for making cursors?
        // for each of the return types, we ask for 1 record (item) or a list of records (dir)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITES;


        ///////////////////////////////////////////////////////////////////////
        //All of the below is to make content provider query URIs
        //designed to make life easier to query the Content Provider

        //Decoder functions

        //used to find the final id tag appended to end of uri
        //example  // content://com.gmail.lgelberger.popularmovies/favourite/999
        //this method should return "999"
        public static String getIdFromUri(Uri uri) {
            //return uri.getPathSegments().get(1); //returns the second part of uri, second after the authority part
            return uri.getLastPathSegment(); //return whatever is at the end of Uri
        }

    }
}


//ZZZ I will need to:
// Write to the database (add the a row of movie data whenever the user selects a movie to be
// one of their "favourites"
// Read a list of movie posters and names (I will read the whole list)
// Read the details of just ONE movie to make a detailed view once the user has clicked on
// a movie poster and the app displays the movie details
//  ZZZ LJG  WHAT IF I JUST make my URIs simple to start
// content://com.gmail.lgelberger.popularmovies/movie - to access the entire table
// content://com.gmail.lgelberger.popularmovies/movie/4 - to access a single row
// and perhaps some uri's to add rows or modify rows?

