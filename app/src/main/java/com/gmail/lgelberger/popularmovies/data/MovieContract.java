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
    Inner Class that defines the contents of the movie table
     */
    public static final class MovieEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies";

        //Movie details to store
        //API Movie ID (Stored as Int)
        public static final String COLUMN_API_MOVIE_ID = "api_movie_id";


        //String containing name of movie (stored as String)
        public static final String COLUMN_MOVIE_TITLE = "title";

        //Movie release date (stored as String)
        public static final String COLUMN_RELEASE_DATE = "release_date";

        //Movie poster is stored as (ZZZ - I HAVE NO FUCKING CLUE? Image????? Bitmap?)
        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        //Vote average for movie as per external DB of vote average (stored as String
        // - possibly Float??? ZZZ)
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        //Plot Synopsis (stored as String)
        public static final String COLUMN_PLOT_SYNOPSIS = "plot_synopsis";

        //ZZZ More columns needed?

        //movie reviews - stored as ZZZ ?? perhaps URL?
        public static final String COLUMN_MOVIE_REVIEW = "movie_review";

        //movie videos - stored as ZZZ ??? perhaps URL?
        public static final String COLUMN_MOVIE_VIDEO = "movie_video";
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
