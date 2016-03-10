package com.gmail.lgelberger.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Leslie on 2016-03-08.
 *
 * Modelled off http://developer.android.com/guide/topics/ui/settings.html#Fragment
 *
 * Note: A PreferenceFragment doesn't have a its own Context object.
 * If you need a Context object, you can call getActivity().
 * However, be careful to call getActivity() only when the fragment
 * is attached to an activity.
 * When the fragment is not yet attached, or was detached during the
 * end of its lifecycle, getActivity() will return null.
 *
 * Some of the code is modified from the Sunshine app gist at:
 * https://gist.github.com/udacityandroid/41aca2eb9ff6942e769b
 *
 * /**
 * A PreferenceActivity that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 *
 */

public class SettingsFragmnetOld extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
   // public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";
    public static final String MOVE_SORT_ORDER_KEY = String.valueOf((R.string.movie_sort_order_key));

    SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    // listener implementation
                }
            };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //from Sunshine
        // Add 'general' preferences, defined in the XML file
        // TODO: Add preferences from XML

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        // TODO: Add preferences




        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //For all preferences, attach an onPreferenceChangeListener so the UI

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

    }


    //from sunshine app
    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
      //  preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


//from sunshine app
   // @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }







    /**
     * needed for listening to preference changes
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(MOVE_SORT_ORDER_KEY)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }

        //just a test
        //this works
        Preference testPref = findPreference(key);
        testPref.setSummary("Testing");
        testPref.setSummary(sharedPreferences.getString(key, ""));


        //can add other elseif statements too for other keys listened for
    }


    /**
     * From http://developer.android.com/guide/topics/ui/settings.html#PreferenceHeaders
     *
     * For proper lifecycle management in the activity,
     * we recommend that you register and unregister your
     * SharedPreferences.OnSharedPreferenceChangeListener
     * during the onResume() and onPause() callbacks, respectively:
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }



}
