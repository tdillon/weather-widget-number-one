package tgd.mindless.drone.weatherwidgetnumberone;

import android.app.Activity;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;


public class WidgetConfig extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String TAG = "class WidgetConfig";
    ImageView ivGraph;
    Context context;
    boolean locationChanged = false;

    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.v(TAG, "onSharedPreferenceChanged:   " + key);

            if (key.equals(WidgetConfigPreferences.LATITUDE) || key.equals(WidgetConfigPreferences.LONGITUDE)) {
                locationChanged = true;
            } else {
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
                float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());

                ivGraph.setImageBitmap(WeatherGraphDrawer.draw(AsyncWeatherDAO.getDummyWeather(context), px, py, context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(mAppWidgetId), Context.MODE_PRIVATE), getResources().getDisplayMetrics()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate");

        setResult(RESULT_CANCELED);
        setContentView(R.layout.widget_config);
        findViewById(R.id.btnDone).setOnClickListener(mOnClickListener);
        ivGraph = (ImageView)findViewById(R.id.ivGraph);
        context = this;

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

        ((TextView) findViewById(R.id.tvFoo)).setText("Widget ID: " + String.valueOf(mAppWidgetId));

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
        float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());

        ivGraph.setImageBitmap(WeatherGraphDrawer.draw(AsyncWeatherDAO.getDummyWeather(this), px, py, this.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(mAppWidgetId), Context.MODE_PRIVATE), getResources().getDisplayMetrics()));
        SharedPreferences localPrefs = this.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(mAppWidgetId), Context.MODE_PRIVATE);
        localPrefs.registerOnSharedPreferenceChangeListener(listener);

        Log.v(TAG, "onCreate   ID: " + String.valueOf(mAppWidgetId));
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.v(TAG, "OnClickListener id: " + String.valueOf(mAppWidgetId));

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();

            Context context = v.getContext();
            AsyncWeatherDAO.setConfigComplete(context, mAppWidgetId);
            WeatherWidget.onConfigured(context, mAppWidgetId, locationChanged);  //TODO only get new data if lat/lon have changed
        }
    };

}
