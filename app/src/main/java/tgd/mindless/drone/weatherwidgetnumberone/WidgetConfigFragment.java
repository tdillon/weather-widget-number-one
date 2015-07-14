package tgd.mindless.drone.weatherwidgetnumberone;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;


/**
 * A placeholder fragment containing a simple view.
 */
public class WidgetConfigFragment extends PreferenceFragment {

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
    }
}
