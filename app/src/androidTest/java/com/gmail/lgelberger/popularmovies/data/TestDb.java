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
 * The db will be used for storing favourite movies
 * <p>
 * Perhaps I should just make a test database Name for my testing - to not affect current working database?
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    /*
        Helper function to delete database
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
        Note that this only tests that the  table has the correct columns
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
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());


        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0)); //all tables taken care of with just this statement - do NOT add anything here
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());


        // now, do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

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
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO);


        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }


    /*
            Students:  Here is where you will build code to test that we can insert and query the
            database.  You'll want to look in TestUtilities
            where you can use the "createMovieValuesForOneMovie" function.  You can
            also make use of the validateCurrentRecord function from within TestUtilities.
         */
    //LJG FIgure out whether this is a @SmallTest(unit test) or @MediumTest (can access more resources)
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
        assertTrue("Error: No Records returned from location query", movieCursor.moveToFirst());

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


    /*
        Helper Method to insert a movie into the database
             */
    public long insertMovie() {
        //mContext provided by test framework
        //Get reference to writable database
        //MovieDbHelper dbHelper = new MovieDbHelper(appContext); //course uses  WeatherDbHelper dbHelper = new WeatherDbHelper(mContext); //not sure where mContext is declared or instantiated
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createMovieValuesForOneMovie();

        // Insert ContentValues into database and get a row ID back
        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        //LJG the database insert takes 3 arguments (table name, nullColumn hack - name of column to make null if no data in content
        // , and ContentValues;

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor dbCursor;
        dbCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );


        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from movie query", dbCursor.moveToFirst());

        // LJG can get the data from cursor manually - for example
        // String dbMovieName = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // - here using validateCurrentRecord function in TestUtilities
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", //this is a nice method to re-use in other projects to validate a table
                dbCursor, testValues);

        //can validate manually if needed to
        //assertEquals("Movie Title was" + dbMovieName + " and not the expected ", "Insert Expected String here" , dbLocationSetting);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                dbCursor.moveToNext());

        // Finally, close the cursor and database
        //LJG also, delete test row perhaps???
        dbCursor.close();
        db.close();


        return movieRowId;
    }
}
