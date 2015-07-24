package tgd.mindless.drone.weatherwidgetnumberone;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class WidgetConfigFragment extends PreferenceFragment /*implements SharedPreferences.OnSharedPreferenceChangeListener*/ {

    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.v(TAG, "onSharedPreferenceChanged:   " + key);
            switch (key) {
                case WidgetConfigPreferences.LATITUDE:
                case WidgetConfigPreferences.LONGITUDE:
                case WidgetConfigPreferences.TYPE:
                case WidgetConfigPreferences.TEMP_DOT_COLOR:
                case WidgetConfigPreferences.TEMP_LINE_COLOR:
                case WidgetConfigPreferences.DAYLIGHT_COLOR:
                case WidgetConfigPreferences.TEMP_FONT_COLOR:
                case WidgetConfigPreferences.TIME_FONT_COLOR:
                    findPreference(key).setSummary(sharedPreferences.getString(key, ""));
                    break;
                case WidgetConfigPreferences.TEMP_WIDTH:
                case WidgetConfigPreferences.PRECIP_WIDTH:
                case WidgetConfigPreferences.TEMP_LINE_WIDTH:
                case WidgetConfigPreferences.TEMP_FONT_SIZE:
                case WidgetConfigPreferences.TIME_FONT_SIZE:
                    findPreference(key).setSummary(String.valueOf(sharedPreferences.getInt(key, 0)));
                    break;
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

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

        SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        findPreference(WidgetConfigPreferences.LATITUDE).setSummary(sp.getString(WidgetConfigPreferences.LATITUDE, "TODO"));
        findPreference(WidgetConfigPreferences.LONGITUDE).setSummary(sp.getString(WidgetConfigPreferences.LONGITUDE, "TODO"));
        findPreference(WidgetConfigPreferences.TEMP_WIDTH).setSummary(String.valueOf(sp.getInt(WidgetConfigPreferences.TEMP_WIDTH, 0)));
        findPreference(WidgetConfigPreferences.PRECIP_WIDTH).setSummary(String.valueOf(sp.getInt(WidgetConfigPreferences.PRECIP_WIDTH, 0)));
        findPreference(WidgetConfigPreferences.TEMP_LINE_WIDTH).setSummary(String.valueOf(sp.getInt(WidgetConfigPreferences.TEMP_LINE_WIDTH, 0)));
        findPreference(WidgetConfigPreferences.TEMP_FONT_SIZE).setSummary(String.valueOf(sp.getInt(WidgetConfigPreferences.TEMP_FONT_SIZE, 0)));
        findPreference(WidgetConfigPreferences.TIME_FONT_SIZE).setSummary(String.valueOf(sp.getInt(WidgetConfigPreferences.TIME_FONT_SIZE, 0)));
        findPreference(WidgetConfigPreferences.TYPE).setSummary(sp.getString(WidgetConfigPreferences.TYPE, "TODO"));
        findPreference(WidgetConfigPreferences.TEMP_DOT_COLOR).setSummary(sp.getString(WidgetConfigPreferences.TEMP_DOT_COLOR, "TODO"));
        findPreference(WidgetConfigPreferences.TEMP_LINE_COLOR).setSummary(sp.getString(WidgetConfigPreferences.TEMP_LINE_COLOR, "TODO"));
        findPreference(WidgetConfigPreferences.DAYLIGHT_COLOR).setSummary(sp.getString(WidgetConfigPreferences.DAYLIGHT_COLOR, "TODO"));
        findPreference(WidgetConfigPreferences.TEMP_FONT_COLOR).setSummary(sp.getString(WidgetConfigPreferences.TEMP_FONT_COLOR, "TODO"));
        findPreference(WidgetConfigPreferences.TIME_FONT_COLOR).setSummary(sp.getString(WidgetConfigPreferences.TIME_FONT_COLOR, "TODO"));
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
