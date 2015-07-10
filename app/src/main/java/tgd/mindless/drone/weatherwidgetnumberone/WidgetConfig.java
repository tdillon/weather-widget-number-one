package tgd.mindless.drone.weatherwidgetnumberone;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;


public class WidgetConfig extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String TAG = "class WidgetConfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        setResult(RESULT_CANCELED);
        setContentView(R.layout.widget_config);
        findViewById(R.id.btnDone).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        Log.v(TAG, "onCreate   ID: " + String.valueOf(mAppWidgetId));
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            Log.v(TAG, "OnClickListener id: " + String.valueOf(mAppWidgetId));



            final Context context = WidgetConfig.this;

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            //boolean showTimeLines = sharedPref.getBoolean("prefs_" + String.valueOf(mAppWidgetId), true);
            boolean showTimeLines = sharedPref.getBoolean("pref_showTimeLines", true);
            Log.v(TAG, "OnClickListener preference: " + String.valueOf(showTimeLines));

            //WeatherWidget.updateAppWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

}
