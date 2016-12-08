package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class WeatherWidgetConfigureFragment extends PreferenceFragment {

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String KEY_PREF_THEME = "pref_theme";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent.
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        Log.i("in fragment", "App widget id is " + String.valueOf(mAppWidgetId));

        PreferenceManager localPrefs = getPreferenceManager();
        localPrefs.setSharedPreferencesName(WidgetConfigPreferences.getSharedPreferenceName(mAppWidgetId));
        addPreferencesFromResource(R.xml.preferences);

        final ListPreference listPreference = (ListPreference) findPreference(KEY_PREF_THEME);
        ThemesClass[] themes = ThemeDAO.getThemes(getActivity());
        CharSequence[] entries = new CharSequence[themes.length];
        CharSequence[] entryValues = new CharSequence[themes.length];

        int i = 0;

        for (ThemesClass theme : themes) {
            entryValues[i] = Integer.toString(i);
            entries[i++] = "[" + theme.type.toString() + "] - " + theme.name;
        }

        listPreference.setEntries(entries);
        listPreference.setEntryValues(entryValues);

        listPreference.setDefaultValue(entryValues[0]);
    }
}

