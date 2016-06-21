/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package com.gmail.lgelberger.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Modelled After Udacity Sunshine app on 7 April 2016
 * For testing PopularMovies database.
 * There are two tales, MovieEntry - used for storing the general movies from API calls
 * Favourite Movies - used for storing the user selected favouites they choose.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    /*
        Helper function to delete the entire database (all tables)
     */
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }  //N.B. mContext is provided by AndroidTestCase. Use at will

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
    copy create DB stuff into here and then try deleting it and confirm that the DB is deleted
     */
    public void testDeleteDatabase() {
        //This is not done in Sunshine - I'm just including it here if needed
    }


    /*
        Note that this only tests that the  tables have the correct columns
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME); //add other table names if more than one table

        deleteTheDatabase(); //mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME); //delete the old database

        // - LJG ZZZ  does this delete my working database once I have a working app?
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor movieTableCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                movieTableCursor.moveToFirst());


        // verify that the tables have been created
        do {
            tableNameHashSet.remove(movieTableCursor.getString(0)); //all tables taken care of with just this statement - do NOT add anything here
        } while (movieTableCursor.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the movie entry and favourite entry tables",
                tableNameHashSet.isEmpty());


        /////////////////////// MovieEntry Tests
        // now, do our tables contain the correct columns?
        movieTableCursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                movieTableCursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_1);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_2);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO_3);

        int columnNameIndex = movieTableCursor.getColumnIndex("name");
        do {
            String columnName = movieTableCursor.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (movieTableCursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());

        movieTableCursor.close();


        ////////////////////////////////////////////////////////
        //Favourite Entry Tests
        // now, do our tables contain the correct columns?
        Cursor favouriteTableCursor = db.rawQuery("PRAGMA table_info(" + MovieContract.FavouriteEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                favouriteTableCursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> favouriteColumnHashSet = new HashSet<String>();
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry._ID);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_TITLE);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_API_MOVIE_ID);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_POSTER_URL);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_POSTER);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_PLOT_SYNOPSIS);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_VOTE_AVERAGE);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_1);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_1_AUTHOR);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_2);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_2_AUTHOR);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_3);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_REVIEW_3_AUTHOR);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_1);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_2);
        favouriteColumnHashSet.add(MovieContract.FavouriteEntry.COLUMN_MOVIE_VIDEO_3);

        int favouriteColumnNameIndex = favouriteTableCursor.getColumnIndex("name");
        do {
            String columnName = favouriteTableCursor.getString(columnNameIndex);
            favouriteColumnHashSet.remove(columnName);
        } while (favouriteTableCursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required favourite entry columns",
                favouriteColumnHashSet.isEmpty());

        favouriteTableCursor.close();


        db.close(); //close the entire database at the end of the tests
    }





    /////////////////////////////////////////////////////////////////
    //Specific Tests for MovieEntry Table - Favourite Entry tests follow


    /*
            Students:  Here is where you will build code to test that we can insert and query the
            database.  You'll want to look in TestUtilities
            where you can use the "createMovieValuesForOneMovie" function.  You can
            also make use of the validateCurrentRecord function from within TestUtilities.
         */
    //LJG Figure out whether this is a @SmallTest(unit test) or @MediumTest (can access more resources)
    public void testMovieTable() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step : Create movie values
        ContentValues movieValues = TestUtilities.createMovieValuesForOneMovie();

        // Third Step : Insert ContentValues into database and get a row ID back
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        // Make sure we have a valid row ID.
        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor movieCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from movieEntry query", movieCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb movieEntry failed to validate",
                movieCursor, movieValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from movie query",
                movieCursor.moveToNext());

        // Sixth Step: Close cursor and database
        movieCursor.close();
        dbHelper.close();
    }




    /////////////////////////////////////////////////////////////////
    //Specific Tests for Favourite Table - Movie Entry tests above

    /*
            Students:  Here is where you will build code to test that we can insert and query the
            database.  You'll want to look in TestUtilities
            where you can use the "createMovieValuesForOneMovie" function.  You can
            also make use of the validateCurrentRecord function from within TestUtilities.
         */
    //LJG Figure out whether this is a @SmallTest(unit test) or @MediumTest (can access more resources)
    public void testFavouriteTable() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step : Create movie values
        ContentValues movieValues = TestUtilities.createFavouriteValuesForOneMovie();

        // Third Step : Insert ContentValues into database and get a row ID back
        long movieRowId = db.insert(MovieContract.FavouriteEntry.TABLE_NAME, null, movieValues);

        // Make sure we have a valid row ID.
        assertFalse("Error: Favourite Movie Not Inserted Correctly", movieRowId == -1L);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor movieCursor = db.query(
                MovieContract.FavouriteEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from favouriteEntry query", movieCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb favouriteEntry failed to validate",
                movieCursor, movieValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from favourite movie query",
                movieCursor.moveToNext());

        // Sixth Step: Close cursor and database
        movieCursor.close();
        dbHelper.close();
    }
}
