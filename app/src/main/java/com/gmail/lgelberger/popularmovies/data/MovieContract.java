package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leslie on 2016-03-22.
 * <p>
 * Defines table and column names for movie favourites database
 * Modelled after the class "WeatherContract" in the sunshine app lesson 4A
 * https://github.com/udacity/Sunshine-Version-2/blob/4.03_define_contract_constants/app/src/main/java/com/example/android/sunshine/app/data/WeatherContract.java
 */
public class MovieContract {


    //The following are for the Content provider - as modelled on the Udacity Sunshine app

   /* For my own info - the URIs needed for my content provider
    i.e. the searches I will be doing in my app (what will be needed to show?)

    ZZZ I will need to:
    -Write to the database (add the a row of movie data whenever the user selects a movie to be
    one of their "favourites"

    -Read a list of movie posters and names (I will read the whole list)
    -Read the details of just ONE movie to make a detailed view once the user has clicked on
    a movie poster and the app displays the movie details


    ZZZ LJG  WHAT IF I JUST make my URIs simple to start
    content://com.gmail.lgelberger.popularmovies/movie - to access the entire table

    content://com.gmail.lgelberger.popularmovies/movie/4 - to access a single row

    and perhaps some uri's to add rows or modify rows?


////////////////////////////////////////////////////////////////
    To Read from DB
    MOVIE_WITH_POSTER (DIR) = 101  //to read the movie posters for the grid display
    content://com.gmail.lgelberger.popularmovies/movie/[Movie List Query]

    MOVIE_WITH_DETAILS (ITEM) = 102  //to show details of movie
    content://com.gmail.lgelberger.popularmovies/movie/[Movie ID Query]


    To Write from DB
    MOVIE (DIR) = 100 //to write a new movie to data base
    content://com.gmail.lgelberger.popularmovies/movie
////////////////////////////////////////////////////////////
    */


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
    public static final String PATH_SOME_OTHER_INFO = "other_info";

//////////////////////////////////////////////////////////////////////////////


    //The following are the inner classes that define the database tables


/*
       Table Name = movies

    _ID - each row will have own id number
    movieTitle
    api_movie_id
    moviePosterUrl
    movie_poster (stored as image somehow)
    originalTitle
    overview
    voteAverage
    releaseDate

    also need
    Movie review
    Movie Trailer Video
    //currently these 2 are not in movie provider

    OFFICIAL REQUIREMENTS
    NB - App displays favorite movie details (title, poster, synopsis, user rating, release date) even when offline
    These are the only thing that I think I will need!

 */

    /*
    Inner Class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns {//BaseColumns allows the _ID String to be already included

        public static final String TABLE_NAME = "movies";

        //String containing name of movie (stored as String)
        public static final String COLUMN_MOVIE_TITLE = "title";

        //Movie details to store
        //API Movie ID (Stored as Int)
        public static final String COLUMN_API_MOVIE_ID = "api_movie_id";

        //String containing url of movie poster (Stored as String)
        public static final String COLUMN_MOVIE_POSTER_URL = "movie_poster_url";

        //Movie poster is stored as (ZZZ - I HAVE NO FUCKING CLUE? Image????? Bitmap?)
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        //Original title stored as String
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        //Plot Synopsis/ Overview (stored as String)
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        //Vote average for movie as per external DB of vote average (stored as String
        // - possibly Float??? ZZZ)
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //Movie release date (stored as String)
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //ZZZ More columns needed?

        //movie reviews - stored as ZZZ ?? perhaps URL?
        public static final String COLUMN_MOVIE_REVIEW = "movie_review";

        //movie videos - stored as ZZZ ??? perhaps URL?
        public static final String COLUMN_MOVIE_VIDEO = "movie_video";


        /*
        What I load into the MovieDataProvider
        I used this to figure out my column names in this contract


        moviePosterResource = in.readInt();
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
         */

/////////////////////////////////////////////////////////////////////////////

        // The following are needed for the Content Provider

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
        //this is used to just get ONE row of data (instead of querying the whole damn table)
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


        //Now for some URI builders
        //these help keep the details of the URI contained here in the contract


       /* To Read from DB
        MOVIE_WITH_POSTER (DIR) = 101  //to read the movie posters for the grid display
        content://com.gmail.lgelberger.popularmovies/movie/[Movie List Query]

        MOVIE_WITH_DETAILS (ITEM) = 102  //to show details of movie
        content://com.gmail.lgelberger.popularmovies/movie/[Movie ID Query]


        To Write from DB
        MOVIE (DIR) = 100 //to write a new movie to data base
        content://com.gmail.lgelberger.popularmovies/movie*/


        ///////////////////////////////////////////////////////////////////////
        //All of the below crap is to make content provider query URIs
        //designed to make life easier to query the Content Provider
        //skip this for now for initial implementation
        //do it after the basic content provider is made




        //I don't think I need this one. I just built it to follow along in Sunshine
        /*public static Uri buildMovieAppendPathURI(String SomeMovieListInfo) {
            return CONTENT_URI.buildUpon().appendPath(SomeMovieListInfo).build();
        }*/

/*

        public static Uri buildMovieUriWithID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
*/




        // To Write from DB
        // MOVIE (DIR) = 100 //to write a new movie to data base
        //  content://com.gmail.lgelberger.popularmovies/movie
        //ZZZ LJG Need to implement tha above!!!!!!!!!!

        // MOVIE_WITH_POSTER (DIR) = 101  //to read the movie posters for the grid display
        // content://com.gmail.lgelberger.popularmovies/movie/[Movie List Query]

        //buildMoviePosters ()  - should use the entire db
        //perhaps change the movieQueryKey to be "posters" or something like that to make
        //this query specific to posters only
       /* public static Uri buildMoviePoster(String movieQueryKey, String movieQueryValue) {
            return CONTENT_URI.buildUpon().appendQueryParameter(movieQueryKey, movieQueryValue).build();
        }*/

       // public static Uri buildMoviePoster(String movieQueryValue) {
       //     return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_MOVIE_POSTER, movieQueryValue).build();
       // }


        //  MOVIE_WITH_DETAILS (ITEM) = 102  //to show details of movie
        // content://com.gmail.lgelberger.popularmovies/movie/[Movie ID Query]
        //build MovieDetails (movie Id passed in)   - should make a query with just that movie returned

       // public static Uri buildMovieDetails(String movieQueryValue) {
        //    return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_API_MOVIE_ID, movieQueryValue).build();
       // }




        //Decoder fucntions
        //to write when I know what the fuck I'm doing

        //used to find the final id tag appended to end of uri
        //example  // content://com.gmail.lgelberger.popularmovies/movie/999
        //this method should return "999"
        //NOTE: I am returning a string - perhaps I will need to return an integer? As the _id is really an integer
        //we will see what I will need
        public static String getIdFromUri(Uri uri) {
            //return uri.getPathSegments().get(1);

            return uri.getLastPathSegment(); //return whatever is at the end of Uri
        }


       /* public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
*/

        //decode all the movie posters??

        //decode each part of the movie  - ie. title
        //poster jpg
        //other parts
        //as separate decoding stuff

    }


    /*
    Inner Class that defines the contents of some other, as yet unknown table
     */
    public static final class SomeOtherTable implements BaseColumns {
        public static final String TABLE_NAME = "some_other_table";

        //placeholder if I need another table
        public static final String COLUMN_OTHER_DATA = "other_data";


        /////////////////////////////////////////////////////////////////////////////

        // The following are needed for the Content Provider
        // Create Content Uri that represents base location for this table
        //This way the content uri can be built for each table in a content provider
        //This just makes the base content uri, and adds the table path to it.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SOME_OTHER_INFO).build();


        //functions to help build content provider queries
        //helpful to have so that other parts of app don't have to know how to do this. It
        // can be self contained
        public static Uri buildOtherTableUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        //ZZZ LJG I think this is for making cursors?
        // for each of the return types, we ask for 1 record (item) or a list of records (dir)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SOME_OTHER_INFO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SOME_OTHER_INFO;


    }


}