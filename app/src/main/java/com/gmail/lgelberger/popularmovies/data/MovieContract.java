package com.gmail.lgelberger.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by Leslie on 2016-03-22.
 *
 * Defines table and column names for movie favourites database
 * Modelled after the class "WeatherContract" in the sunshine app lesson 4A
 *https://github.com/udacity/Sunshine-Version-2/blob/4.03_define_contract_constants/app/src/main/java/com/example/android/sunshine/app/data/WeatherContract.java
 */
public class MovieContract {

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
 */

    /*
    Inner Class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns{//BaseColumns allows the _ID String to be already included

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
        public static final String COLUMN_ORIGINAL_TITLE =  "original_title";

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


        moviePosterResource = in.readInt();
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
         */



    }


    /*
    Inner Class that defines the contents of some other, as yet unknown table
     */
    public static final class SomeOtherTable implements BaseColumns {
        public static final String TABLE_NAME = "some_other_table";

        //placeholder if I need another table
        public static final String COLUMN_OTHER_DATA = "other_data";

    }


}
