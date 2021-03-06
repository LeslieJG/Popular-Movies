package com.gmail.lgelberger.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.gmail.lgelberger.popularmovies.data.MovieContract;

/**
 * Created by Leslie on 2016-03-10.
 * <p/>
 * Modelled off http://developer.android.com/guide/topics/ui/settings.html#Fragment
 * <p/>
 * Note: A PreferenceFragment doesn't have a its own Context object.
 * If you need a Context object, you can call getActivity().
 * However, be careful to call getActivity() only when the fragment
 * is attached to an activity.
 * When the fragment is not yet attached, or was detached during the
 * end of its lifecycle, getActivity() will return null.
 * <p/>
 * <p/>
 * adding  implements OnSharedPreferenceChangeListener to listen for preference changes
 * need to implement the onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) method
 * <p/>
 * For proper lifecycle management in the activity, we recommend that you register
 * and unregister your SharedPreferences.OnSharedPreferenceChangeListener
 * during the onResume() and onPause() callbacks, respectively:
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    //added to have reference to movie_sort_order_key
    final String LOG_TAG = SettingsFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging
    //SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);


        Preference button = (Preference) findPreference(getString(R.string.delete_favourites_key));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(), "Favourites List Deleted", Toast.LENGTH_LONG).show();
                Log.v(LOG_TAG, "Pressed the Delete Favourites Button");
                getActivity().getApplicationContext().getContentResolver().delete(MovieContract.FavouriteEntry.CONTENT_URI,
                        null, null); //perhaps put this in if (isAdded()) {} block to ensure that the fragment is
                //attached to an activity?

                ///LJG Add delete the entire Favourites Database here!
                return true;
            }
        });
    }


    /**
     * needed for implements OnSharedPreferenceChangeListener
     * as described in http://developer.android.com/guide/topics/ui/settings.html#ReadingPrefs
     * This is needed to make a sharedPreferencesListener
     * <p/>
     * will change the preference summary if changed
     *
     * @param sharedPreferences Shared Preferences
     * @param key               Shared Preference Key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String LOG_TAG = SettingsFragment.class.getSimpleName(); //name of MainActivityFragment class for error logging
        final String MOVE_SORT_ORDER_KEY = getString(R.string.movie_sort_order_key);
        final String DELETE_FAVOURITES_KEY = getString(R.string.delete_favourites_key);

        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            if (key.equals(MOVE_SORT_ORDER_KEY)) {
                ListPreference listPref = (ListPreference) pref;
                pref.setSummary(listPref.getEntry());
            }
        } else if (!(pref instanceof CheckBoxPreference)) {
            //don't change summary of checkbox preference, but change all others

            pref.setSummary(sharedPreferences.getString(key, "")); // Set summary to be the user-description for the selected value
        }
    }


    /**
     * needed to register my SharedPreferences.OnSharedPreferenceChangeListener
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }


    /**
     * needed to un register my SharedPreferences.OnSharedPreferenceChangeListener
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
