package com.gmail.lgelberger.popularmovies.service;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Leslie on 2016-06-08.
 * <p>
 * Utility Methods for API calls
 * Most methods will be static
 */
public class ServiceApiUtility {
    private static String LOG_TAG = ServiceApiUtility.class.getSimpleName(); //for debugging


    /**
     * Used by ReviewAndTrailerUpdateService and PopularMoviesService
     *  To connect to network and fetch JSON results from API
     *
     * @param apiQueryUrl URL needed for Sepecific API call
     * @return JSON result of API call
     */
    public static String fetchJsonFromApi(URL apiQueryUrl) {
        Log.v(LOG_TAG, "fetchJsonFromApi - doing it now");
        //If we get to here then we need to make the API call to get data (i.e the data is not already in database)
        String movieJsonStr = null; // Will contain the raw JSON response as a string.

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to themoviedb.org, and open the connection
            urlConnection = (HttpURLConnection) apiQueryUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();

            //return the movie JSON info as String
            return movieJsonStr; // successful, done here (except for finally block)

        } catch (MalformedURLException e) {
            //  Toast.makeText(mContext, "Got invalid data from server", Toast.LENGTH_LONG).show();
            e.printStackTrace(); //the URL was malformed
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            return null;
        } finally { //disconnect the URL connection and close reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        // This will only happen if there was an error getting or parsing the movie data. or API delivers nothing
        return null;
    }
}
