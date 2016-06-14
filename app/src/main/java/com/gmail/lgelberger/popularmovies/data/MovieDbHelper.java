package com.gmail.lgelberger.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Leslie on 2016-04-05.
 * Based on WeatherDbHelper.java in Sunshine app
 * <p/>
 * Manages a local database for movie data.
 * <p/>
 * SQLiteOpenHelper helps to create the database and helps handle database
 * versioning as changes are made to the database in the future
 * Helps to modify tables
 * Version control important - in many apps being able to upgrade database
 * without loss of data is important
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1; //Typically starts at version 1
    //must be manually incremented in code if you change db version

    static final String DATABASE_NAME = "movie.db"; //the name of the actual database
    //file in the file system

    //perhaps also make a testing database name??? just an idea for testing

    private final String LOG_TAG = MovieDbHelper.class.getSimpleName(); //name of this class for error logging


    public MovieDbHelper(Context context) {


        ////// SQLiteOpenHelper Constructor //////
       /* context Context - to use to open and create the database
        name String - name of database file, or null for in-memory database
        factory SQLiteDatabase.CursorFactory - to use for creating cursor objects, or null for default
        version int - number of the database (starting at 1)
                    if the database number being passed in is older onUpgrade(SQLiteDatabase, int, int)
                    will be used to upgrade the database; if the database is
                    newer, onDowngrade(SQLiteDatabase, int, int) will be used to downgrade the database*/
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /*
    The first time the database is used, onCreate will be called
    We are creating a String that has the long SQL command create the table needed
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //don't think I need AUTOINCREMENT ? It just limits the number of movies stored in database - but that limit is huge 10^19
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " + //all the "NOT NULL" are constraints that prevent null entries being put into database
              //  MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " INTEGER NOT NULL, " +
               MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + "  INTEGER NOT NULL UNIQUE, " + //ensuring I only have one row for each single movie
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT , " + //I'm allowing this to be null if needed until I can
                //figure out how to store the damn poster image itself
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1 + " TEXT , " +  //I'm allowing this to be null if needed until I can
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR + " TEXT , " +  //I'm allowing this to be null if needed
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2 + " TEXT , " +
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR + " TEXT , " +  //I'm allowing this to be null if needed
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3 + " TEXT , " +
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR + " TEXT , " +  //I'm allowing this to be null if needed
                MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1 + " TEXT , " +  //I'm allowing this to be null if needed until I can
                MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2 + " TEXT , " +
                MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3 + " TEXT  " +

                //figure out how to store the movie videos (perhaps just a URL?)
                ");";

        /* From original sunshine
        Useful stuff in case I need just one entry for each movie (to avoid having duplicate entries for same movie
        I'm leaving this out for now.
                        // To assure the application have just one weather entry per day
                        // per location, it's created a UNIQUE constraint with REPLACE strategy
                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
                        WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";*/

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        //Then use ALTER TABLE to add columns to the existing table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        //  sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
