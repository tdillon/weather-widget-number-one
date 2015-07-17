package tgd.mindless.drone.weatherwidgetnumberone;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * A placeholder fragment containing a simple view.
 */
public class WidgetConfigFragment extends PreferenceFragment /*implements SharedPreferences.OnSharedPreferenceChangeListener*/ {

    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.v(TAG, "onSharedPreferenceChanged:   " + key);
            if (key.equals(WidgetConfigPreferences.LATITUDE)) {
                Preference connectionPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
            }
        }
    };
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String TAG = "clsWidgetConfigFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent.
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        Log.v(TAG, "onCreate   ID: " + String.valueOf(mAppWidgetId));

        PreferenceManager localPrefs = getPreferenceManager();
        localPrefs.setSharedPreferencesName(WidgetConfigPreferences.getSharedPreferenceName(mAppWidgetId));
        addPreferencesFromResource(R.xml.preferences);

        //getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
*//*
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "onSharedPreferenceChanged:   " + key);
        if (key.equals(WidgetConfigPreferences.LATITUDE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
*/
}
