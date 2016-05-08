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
 * LJG Copied into PopularMovies from Sunshine on 7 April 2016
 * Will be using this to test my PopularMovies database.
 * The db will be used for storing favourite movies
 *
 * Perhaps I should just make a test database Name for my testing
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
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
    public void testDeleteDatabase(){
        //This is not done in Sunshine - I'm just including it here if needed
    }


    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
       // tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME); //old from sunshine - delete

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME); //delete the old database
        // - does this delete my working database once I have a working app?
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());







        // verify that the tables have been created - LJG How to deal with this with only 1 table???
        //the do while loop should take care of this. It should look at ALL the tables (even if there is only 1 table to look at)
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());






        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

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


        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }






    /*
            Students:  Here is where you will build code to test that we can insert and query the
            database.  We've done a lot of work for you.  You'll want to look in TestUtilities
            where you can use the "createWeatherValues" function.  You can
            also make use of the validateCurrentRecord function from within TestUtilities.
         */
    //LJG FIgure out whether this is a @SmallTest(unit test) or @MediumTest (can access more resources)
    public void testMovieTable() {
        // First insert the location, and then use the locationRowId to insert
        // the movie. Make sure to cover as many failure cases as you can.

        //long locationRowId = insertMovie(); //this would be the first row inserted. Notneeded will do it later on anyway

        // Make sure we have a valid row ID.
      //  assertFalse("Error: Movie Not Inserted Correctly", locationRowId == -1L); //caused an extra row to be inserted

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create movie values
        ContentValues movieValues = TestUtilities.createMovieValues();

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
       // assertTrue(movieRowId != -1);

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
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.

        LJG This is really just ALL the code ffrom testLocationTable. Moved here so that testWeatherTable can also use it and get a valid rowID back
        which can't happen between tests (i.e. I can't make testLocationTable return anything other than void because it is an independant test
     */
    public long insertMovie() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.

       // Context appContext = this.getContext(); //this was my line. The course used mContext, but I never see it declared or instantiated
        //ZZZ  LJG   appContext "should" be the same as mContext in Android helper. It would be good to assert that.



        //MovieDbHelper dbHelper = new MovieDbHelper(appContext); //course uses  WeatherDbHelper dbHelper = new WeatherDbHelper(mContext); //not sure where mContext is declared or instantiated
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)



        ContentValues testValues = TestUtilities.createMovieValues();

        //making my own values
        /*ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, -147.353);*/
        //ContentValues testValues = TestUtilities.createNorthPoleLocationValues(); //the official way from the course

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
        assertTrue("Error: No Records returned from location query", dbCursor.moveToFirst());


       /* String dbLocationSetting = dbCursor.getString(dbCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING));
        String dbCityName = dbCursor.getString(dbCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME));
        Float dbCoordLat = dbCursor.getFloat(dbCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT));
        //String dbCoordLat = dbCursor.getString(dbCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT));
        Float dbCoordLong = dbCursor.getFloat(dbCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LONG));*/

        // String firstName = c.getString(c.getColumnIndex("FirstName"));
        // int age = c.getInt(c.getColumnIndex("Age"));


        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", //this is a nice method to re-use in other projects to validate a table
                dbCursor, testValues);


        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like) LJG I used this at first

       /* assertEquals("Location setting was " + dbLocationSetting + " and not the expected 99705", "99705", dbLocationSetting);
        assertEquals("City Name was " + dbCityName + " and not the expected North Pole", "North Pole", dbCityName);
        assertEquals("Coord Lat was " + dbCoordLat + " and not the expected 64.7488", 64.7488f, dbCoordLat, 0.0f);
        assertEquals("Coord Long was " + dbCoordLong + " and not the expected -147.353", -147.353f, dbCoordLong, 0.0f); //for testing floats must also provide a delta of how far apart they can be and still pass
*/


        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                dbCursor.moveToNext());


        // Finally, close the cursor and database
        //LJG also, delete test row perhaps???
        dbCursor.close();
        db.close();
        //end of original testLocationTable()


        return movieRowId;
        // return -1L;
    }
}
