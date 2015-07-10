package tgd.mindless.drone.weatherwidgetnumberone;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static final String TAG = "class WeatherWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//BUG onUpdate is called even though a configuration activity is defined google code issue #3696
        android.util.Log.v(TAG, "onUpdate");

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        Log.v(TAG, "onUpdate before MyAsyncTask.execute");
        new AsyncWeatherDAO(context).execute("https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/39.3061,-81.3664");
        Log.v(TAG, "onUpdate after MyAsyncTask.execute");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        android.util.Log.v(TAG, "onAppWidgetOptionsChanged");
        //TODO redraw on update?  can i send intent from MyAsyncTask?
        new AsyncWeatherDAO(context).execute("https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/39.3061,-81.3664");
        //DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        //Toast.makeText(context, "onAppWidgetOptionsChanged: min-width: " + String.valueOf(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) + " xdpi: " + String.valueOf(metrics.xdpi), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEnabled(Context context) {
        android.util.Log.v(TAG, "onEnabled");

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        android.util.Log.v(TAG, "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    static void onDataReturned(Context context, WeatherClass wc) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, WeatherWidget.class));


        for (int i = 0; i < appWidgetIds.length; ++i) {
            SharedPreferences sharedPref = context.getSharedPreferences("prefs_" + String.valueOf(appWidgetIds[i]), Context.MODE_PRIVATE);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            int width = manager.getAppWidgetOptions(appWidgetIds[i]).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int height = manager.getAppWidgetOptions(appWidgetIds[i]).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());
            float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());

            views.setImageViewBitmap(R.id.ivGraph, WeatherGraphDrawer.draw(wc, px, py, sharedPref, context.getResources().getDisplayMetrics()));

            manager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        android.util.Log.v(TAG, "updateAppWidget  appWidgetId: " + String.valueOf(appWidgetId));


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvUpdateTime, DateFormat.getTimeFormat(context).format(new Date()));


        Intent intent = new Intent(context, WidgetConfig.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent, 0);
        views.setOnClickPendingIntent(R.id.ivGraph, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


}

