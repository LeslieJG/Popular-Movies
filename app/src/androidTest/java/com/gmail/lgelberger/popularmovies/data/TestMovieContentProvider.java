package com.gmail.lgelberger.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Leslie on 2016-05-08.
 * <p>
 * Used to test the Movie data Content Provider
 * <p>
 * Modelled after Udacity Sunshine app
 * Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
 * that at least the basic functionality has been implemented correctly.
 */
public class TestMovieContentProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestMovieContentProvider.class.getSimpleName();


    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }



    // Since we want each test to start with a clean slate - copied from TestDb.java
    //usefull for helping me test out the database
    //LJG ZZZ I'm not sure that this works!!!!??? - perhaps just replace this with deleteallrecordsfrom provider?
   /* void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }  //N.B. mContext is provided by AndroidTestCase. Use at will
*/



    //from sunshine
/*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
       /* mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );*/

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();


    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }





    /*
                This test checks to make sure that the content provider is registered correctly in AndroidManifest.xml
                Modified from Sunshine App
             */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieContentProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieContentProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {

            // I guess the provider isn't registered correctly.
            //this will ALWAYS fail the assert, since we set it to 'false'
            assertTrue("Error: MovieContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    /*
               This test doesn't touch the database.  It verifies that the ContentProvider returns
               the correct type for each type of URI that it can handle. It checks whether each type
               is a type CONTENT_URI (multple records)
               or a type CONTENT_TYPE_URI (single record)

            */
    public void testGetType() {
        // content://com.gmail.lgelberger.popularmovies/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        long testMovieID = 1357L;

        // content://com.gmail.lgelberger.popularmovies/movie/1357
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieUriWithAppendedID(testMovieID));
        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie/1357
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }


    /*
       This test uses the database directly to insert and then uses the ContentProvider to
       read out the data.  Uncomment this test to see if the basic weather query functionality
       given in the ContentProvider is working correctly.
    */
    public void testBasicMovieQuery() {  //make sure I test both types of Queries
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //I want to start with an empty database
       // db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null); //make sure this deletes the database!!!!!
       // deleteTheDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long locationRowId = TestUtilities.insertMovieValues(mContext);

        // Fantastic.  Now that we have a Mpvie


        db.close(); // to stop writing to it

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        movieCursor.close(); //close the cursor at the end of the test

    }


    // LJG ZZZ THis is to test the Detail Movie Query
    //Skip this test for now
    //it always returns an empty cursor and I can't figure out why
    //go back and recode this later on once I have the Content Provider delete methods working properly
    //THis really should be tested!
    /*
      This test uses the database directly to insert and then uses the ContentProvider to
      read out the data.  Uncomment this test to see if the basic weather query functionality
      given in the ContentProvider is working correctly.
   */

    public void testBasicDetailMovieQuery() {

        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //create a lot of table values
        // use the ones created for bulkInsertValues

        ContentValues[] testValuesArray = createBulkInsertMovieValues(); //this should be a table of 10 values
        // BULK_INSERT_RECORDS_TO_INSERT is the number of values created


        // Register a content observer for our bulk insert.
     //   TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
    //    mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);



        // do the bulk insert
        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, testValuesArray);


        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.

      //  movieObserver.waitForNotificationOrFail();
     //   mContext.getContentResolver().unregisterContentObserver(movieObserver);



        //quick confirmation that we actually inserted the correct number
        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT );


        //this test is just making sure that the bulk insert worked
        //getting cursor of entire table
        // A cursor is your primary interface to the query results.
        Cursor entireTableCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " ASC"  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(entireTableCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        entireTableCursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, entireTableCursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    entireTableCursor, testValuesArray[i]);
        }

        //preparing to get Movie number "7" back
        final int RECORD_I_WANT = 7;

       //move cursor to
        entireTableCursor.moveToFirst();
        entireTableCursor.move(RECORD_I_WANT); //move cursor to row that I will compare it with

        //now see if I can match the correct contentValue with the correct cursor row
        TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating RECORD_THAT_I_WANT from basicQuery " ,
                entireTableCursor, testValuesArray[RECORD_I_WANT]);









      /*  Just wanted to see what the other columns look like.
        delete this when done

      movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Movie Title " + i);
        movieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, i);
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://biteme.com/" + i);
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "This should be a picture, not text_" + i);
        //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!

        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Movie Original Title " + i);
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "This movie sucked, times " + i);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, i + " out of 10");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "Release date the year " + i + " A.D");

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "Movie Review: It was " + i + "thumbs up");
        //The reviews may end up being a URL - this may also need to change

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "Here is video number " + i);
        */




        // see if I can get a single row cursor from a basic Query - doing it manually (without using Movie_detail URI
       ////THis is incorrect way - find right way and implement it in Movie ContentProvider
      //  String selection = MovieContract.MovieEntry._ID + " = ?";
       // String selectionArgs[] = {"7"};



        //these ones work!!!!!!!
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " = ?";
        String selectionArgs[] = {"Movie Title " + RECORD_I_WANT}; //RECORD_I_WANT is "7"


        Cursor singleMovieTitleFromBasicQueryCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                selectionArgs,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );


        //see if this cursor has any data in it

        assertTrue(" The singleMovieTitleFromBasicQueryCursor is empty - a single row was NOT returned",
                singleMovieTitleFromBasicQueryCursor.moveToFirst());
        //this one works!!!!

        //make sure it matches the movie Title we want

        TestUtilities.validateCurrentRecord("singleMovieTitleFromBasicQueryCursor.  Error validating RECORD_THAT_I_WANT from singleMovieTitleFromBasicQueryCursor " ,
                singleMovieTitleFromBasicQueryCursor, testValuesArray[RECORD_I_WANT]);

        singleMovieTitleFromBasicQueryCursor.close(); //close the cursor








        ////////////////////////////////////////////////////
       // OK the point of movieDetail is to be able to get a single record back given just the _id
                //let's try to get the _id of a movie and see what it is!!!!

        //create a new movie // first built a new contentValue
        ContentValues movieSingleMovieValues = new ContentValues();
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "The Insert 1 Movie Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, "123456789");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://newSinlgeMove.com");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "MovieInsert1Picture - should be pic not text");
        //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "The Insert 1 Movie ORIGINAL Title");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "The Insert 1 Movie Plot Synopsis");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, "The Insert 1 Movie Vote Average");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "The Insert 1 Movie Release Date");
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "The Insert 1 Movie Movie Review");
        //The reviews may end up being a URL - this may also need to change
        movieSingleMovieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "The Insert 1 Movie Video");
        //the Video may also change format

        //just confirm that the EntireTableCursor still exists
        //may be OK to delete this line at some point
        assertEquals("The Entire Table cursor has more than " + BULK_INSERT_RECORDS_TO_INSERT + " entries in it!!!! - It shouldn't",
                BULK_INSERT_RECORDS_TO_INSERT,
                entireTableCursor.getCount());

        //insert movie
//then get the URI back from a single insert
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieSingleMovieValues);

        //WOw, useing the below statement I just found out that the _id in the database is NOT sequential - good to know!
        //assertTrue(movieInsertUri.toString(), false); //Do not delete this commented line - good info for later!

        //confirm that there are 11 entries in database now
        //get a full table cursor again
        entireTableCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(entireTableCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT + 1);



        //and try to use THAT EXACT id from Uri for a general query
        String idFromUri = MovieContract.MovieEntry.getIdFromUri(movieInsertUri); //id returned is a String not a LONG (just fyi)
        selection = MovieContract.MovieEntry._ID + " = ?"; //select _ID
        selectionArgs[0] = idFromUri; // the specific _ID I want is the one I just inserted
        Cursor oneMovieGeneralQuery = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                selectionArgs,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        //confirm valid row
        assertTrue(" The oneMovieGeneralQuery is empty - a single row was NOT returned",
                oneMovieGeneralQuery.moveToFirst());
        //confirm that it is the same as what I inserted in
        TestUtilities.validateCurrentRecord("oneMovieGeneralQuery.  Error validating record from from oneMovieGeneralQuery " ,
                oneMovieGeneralQuery, movieSingleMovieValues);



        //then use it again for a detail query
        Long idFromUriAsLong = Long.parseLong(idFromUri);
        Uri movieDetailUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(idFromUriAsLong);
        //test to make sure that movieDetailUri is of type MOVIE_DETAIL
        String movieDetailUritype = mContext.getContentResolver().getType(movieDetailUri);
        // vnd.android.cursor.dir/com.gmail.lgelberger.popularmovies/movie/1357
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, movieDetailUritype); // confirm this Uri is of type MOVUE_DETAIL





        //selection = MovieContract.MovieEntry._ID + " = ?"; //select _ID
       //selectionArgs[0] = idFromUri; // the specific _ID I want is the one I just inserted
        //note for Movie Detail I *JUST* have to give it a Uri with the _id appeneded on the end
        Cursor movieDetailQuery =  mContext.getContentResolver().query(
                movieDetailUri,
                null, // leaving "columns" null just returns all the columns.
                null, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                null,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        //confirm valid row
        assertTrue(" The movieDetailQuery is empty - a single row was NOT returned",
                movieDetailQuery.moveToFirst());
        //confirm that it is the same as what I inserted in
        TestUtilities.validateCurrentRecord("movieDetailQuery.  Error validating record from from movieDetailQuery " ,
                movieDetailQuery, movieSingleMovieValues);









        /*
        WeatherContract.LocationEntry.TABLE_NAME +
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";
         */

        //OK Let's try again with movie_id

        // see if I can get a single row cursor from a basic Query - doing it manually (without using Movie_detail URI
        ////THis is incorrect way - find right way and implement it in Movie ContentProvider
        selection = MovieContract.MovieEntry._ID + " = ?";
        String selectionArgsForSingleId[] = {"7"};

        Cursor singleIDQueryfromBasicMovieQuery = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                selection, //MovieContract.MovieEntry._ID, // cols for "where" clause    //perhaps add  + " = ?" after this
                selectionArgsForSingleId,  //new String[]{"7"}, // values for "where" clause
                null  // sort order
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );


        //see if this cursor has any data in it

      //////////////Uncomment sooon /////////////// assertTrue(" The singleIDQueryfromBasicMovieQuery is empty - a single row was NOT returned",singleIDQueryfromBasicMovieQuery.moveToFirst());

        singleIDQueryfromBasicMovieQuery.close(); //close the cursor











        //make the detail query URI
        Uri detailMovieQueryUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(RECORD_I_WANT);

        //confirm that the query is of type Movie_Detail
        String movieUriType = mContext.getContentResolver().getType(detailMovieQueryUri);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, movieUriType);


        //get the integer _id value at end of detailMovieQueryUri
        int detailMovieQueryUriFinalIntegerId = Integer.parseInt(MovieContract.MovieEntry.getIdFromUri(detailMovieQueryUri));
        //SO I need the Id as an Integer!!!! Perhaps just change getIdFromUri to give me an integer and not a String?
        // but it needs to be a string in Content Provider Query (this is how the method signature is inherited)

        //confirm that the uri has a final id of 7 (RECORD_I_WANT);
    ////LJG UNDELETE THIS ROW ///////////////////   assertEquals("detailMovieQueryUri has wrong _id number", RECORD_I_WANT,  detailMovieQueryUriFinalIntegerId);

        //get a SINGLE ROw back - should this be so complicated? Or should it be a stripped down version with just Uri and _id number?

       /* Cursor singleRowCursor =  mContext.getContentResolver().query(
                detailMovieQueryUri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );*/

        Cursor singleRowCursor =  mContext.getContentResolver().query(
                detailMovieQueryUri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );


        //confirm cursor not empty
    //////////////////Undelete this///////////////////  assertTrue("The single row cursor has ZERO rows (it is empty) Check your Content Provider Query", singleRowCursor.moveToFirst()); //will be false is nothing in cursor

        // confirm the cursor only has one row (no more no less)
       // assertEquals("The single row cursor does not contain just ONE row", 1,  singleRowCursor.getCount() )     ;




        //confirm cursor mathces the contentValue from array
        // (if this fails then I think I need to work on my Detail_Uri sqlite query







       /* // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " ASC"  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );*/

        // we should have as many records in the database as we've inserted
       // assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created


        //Let's make sure we got the right row value
    //////////////////////     Uncommet out next line
   ///////////    TestUtilities.validateCurrentRecord("testBasicDETAILMovieQuery. You got the wrong row back", cursor, testValuesArray[RECORD_I_WANT + 1]);
    ////////////////////////
        // I have to add 1 because the sqlite starts counting at row 1, where the testValueArray starts at number zero;


       /* cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }*/



        singleRowCursor.close();


        ////////////////////////////////////////////////////////


        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.

      //  movieObserver.waitForNotificationOrFail();
      //  mContext.getContentResolver().unregisterContentObserver(movieObserver);






        //////////////////////////////////////////////////////////




















       // ContentValues testValues = TestUtilities.createMovieValues();
        /*long locationRowId = TestUtilities.insertMovieValues(mContext);

        // Fantastic.  Now that we have a Mpvie


        db.close(); // to stop writing to it

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        movieCursor.close(); //close the cursor at the end of the test


*/

        //close entireTableCursor
        entireTableCursor.close();

    }




    //LJG ZZZ Delete the below - It will be rewritten above
  /*  public void testBasicDetailMovieQuery() {  //make sure I test both types of Queries
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValuesAotherSet(); // I will be using this in the test to see if
        //I really get back the second set of values

      //   db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null); //make sure this deletes the database!!!!!
        // deleteTheDatabase();


        //create a new database
    //    db = new MovieDbHelper(
    //            this.mContext).getWritableDatabase();
        //test the delete



        //let's see how many entires we have?
        Cursor  cursor = db.rawQuery("select * from table",null);

        assertTrue("Number of Columns is not zero, database not deleted", cursor.getCount() == 0);
      //  cursor.getCount();


        //long locationRowId = TestUtilities.insertMovieValues(mContext);
       long locationRowId = TestUtilities.insertMovieValuesTwoSets(mContext);

        //temp asserting that movie values inserted
        assertTrue("Error: Failure to insert Movie Values", locationRowId != -1);

        assertEquals("locationRowId equals 2", 2, locationRowId);




        // Fantastic.  Now that we have a Mpvie


        db.close(); // to stop writing to it

        long testId = 2L; //use this to find second Movie Entry

        //make the detail URI
        Uri detailQueryUri = MovieContract.MovieEntry.buildMovieUriWithAppendedID(testId);


        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                detailQueryUri, // need to make the detail Uri
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicDetailMovieQuery", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        movieCursor.close(); //close the cursor at the end of the test

    }



*/


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri singleMovieInsertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(singleMovieInsertedUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);



        /*
        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, tco);

        Uri weatherInsertUri = mContext.getContentResolver()
                .insert(WeatherEntry.CONTENT_URI, weatherValues);
        assertTrue(weatherInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                weatherCursor, weatherValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        weatherValues.putAll(testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestUtilities.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                weatherCursor, weatherValues);

        // Get the joined Weather and Location data with a start date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(
                        TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
                weatherCursor, weatherValues);

        // Get the joined Weather data for a specific date
        weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestUtilities.TEST_LOCATION, TestUtilities.TEST_DATE),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
                weatherCursor, weatherValues);*/


    }








    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider(); //guarantees there are rows in database

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our weather delete.
       /* TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, weatherObserver);*/

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();
       // weatherObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
     //   mContext.getContentResolver().unregisterContentObserver(weatherObserver);
    }



    /*

    //LJG ZZZ rename this file and figure out what it does and modify it for pop movies
    it SHOULD be testing update() in MovieContentProvider

            This test uses the provider to insert and then update the data. Uncomment this test to
            see if your update location is functioning correctly.
         */
    public void testUpdate() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values); //insert a row of values
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "A Different Movie Title"); //just updating a value to something different

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)}); //update the values we put in at beginning of method
        assertEquals(count, 1); //make sure that no new entries added/ just updated

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues); //check to see that the values are updated properly

        cursor.close();
    }


    /*
    For making 10 values to test Bulk Insert
     */
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    //static ContentValues[] createBulkInsertMovieValues(long locationRowId) {
    static ContentValues[] createBulkInsertMovieValues() {    //don't need a location id - this is not Sunshine!
       // long currentTestDate = TestUtilities.TEST_DATE;
      //  long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT]; //create an array of Movie Entries

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues movieValues = new ContentValues();

        //    movieValues.put(MovieContract.MovieEntry

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Movie Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_API_MOVIE_ID, i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_URL, "http://biteme.com/" + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "This should be a picture, not text_" + i);
            //LJG ZZZ This will error off once the database is changed to have a jpg store here!!!!

            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Movie Original Title " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "This movie sucked, times " + i);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, i + " out of 10");
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "Release date the year " + i + " A.D");

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEW, "Movie Review: It was " + i + "thumbs up");
            //The reviews may end up being a URL - this may also need to change

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VIDEO, "Here is video number " + i);
            //the Video may also change format

            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }


    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {


       //////////////////////////////////////////////////////////////////////////////////////////////
       //LJG delete this section? Not needed?
        // first, let's create a location value
       /* ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);*/

        ////////////////////////////////////////////////////////////////////////////





        // Now we can bulkInsert some Movies
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT); //make sure we inserted exactly the correct number of rows

        // A cursor is your primary interface to the query results.
         Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieContract.MovieEntry.COLUMN_API_MOVIE_ID + " ASC"  // sort order == by _ID ASCENDING
                //I may have to have a difernt sort order for test to work?  LJG ZZZ
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }


}
