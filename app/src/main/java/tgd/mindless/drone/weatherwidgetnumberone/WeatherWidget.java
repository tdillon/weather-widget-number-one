package tgd.mindless.drone.weatherwidgetnumberone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static final String TAG = "class WeatherWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate:   appWidgetIds.length: " + String.valueOf(appWidgetIds.length));

        //BUG onUpdate is called even though a configuration activity is defined google code issue #3696
        if (appWidgetIds.length == 1 && !AsyncWeatherDAO.getConfigComplete(context, appWidgetIds[0])) {
            Log.v(TAG, "onUpdate:   widgetID: " + String.valueOf(appWidgetIds[0]) + " not configured");
            return;
        }

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            setClickHandler(context, appWidgetManager, appWidgetIds[i]);
        }

        AsyncWeatherDAO dao = new AsyncWeatherDAO(context);
        Integer[][] appWidgetIdsGroupedByLatLon = dao.groupByLatLon(context, appWidgetIds);

        Log.v(TAG, "onUpdate:   " + appWidgetIdsGroupedByLatLon.length + " groups of lat/lons");
        for (Integer[] appWidgetIdGroup : appWidgetIdsGroupedByLatLon) {
            Log.v(TAG, "onUpdate:   execute group with " + appWidgetIdGroup.length + " widgets");
            new AsyncWeatherDAO(context).execute(appWidgetIdGroup);
        }
        Log.v(TAG, "onUpdate:   done");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.v(TAG, "onAppWidgetOptionsChanged");
        drawWidget(context, appWidgetId, appWidgetManager, new AsyncWeatherDAO(context).getWeather(context, appWidgetId));
    }

    @Override
    public void onEnabled(Context context) {
        Log.v(TAG, "onEnabled");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.v(TAG, "onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void onDataReturned(Context context, WeatherClass wc, Integer[] appWidgetIds) {
        Log.v(TAG, "onDataReturned:   widget ids: " + TextUtils.join(", ", appWidgetIds));
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        for (int i = 0; i < appWidgetIds.length; ++i) {
            drawWidget(context, appWidgetIds[i], manager, wc);
        }
    }

    public static void onConfigured(Context context, int appWidgetId, boolean locationChanged) {
        Log.v(TAG, "onConfigured:   widget id: " + appWidgetId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        setClickHandler(context, appWidgetManager, appWidgetId);
        if (locationChanged) {
            new AsyncWeatherDAO(context).execute(new Integer[]{appWidgetId});
        } else {
            drawWidget(context, appWidgetId, appWidgetManager, AsyncWeatherDAO.getWeather(context, appWidgetId));
        }
    }

    private static void drawWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager, WeatherClass weatherClass) {
        Log.v(TAG, "drawWidget:   widget id: " + appWidgetId);

        SharedPreferences sharedPref = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvUpdateTime, DateFormat.getTimeFormat(context).format(new Date()));

        int width = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());
        float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());

        views.setImageViewBitmap(R.id.ivGraph, WeatherGraphDrawer.draw(weatherClass, px, py, sharedPref, context.getResources().getDisplayMetrics()));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void setClickHandler(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.v(TAG, "setClickHandler  appWidgetId: " + String.valueOf(appWidgetId));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        Intent intent = new Intent(context, WidgetConfig.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ivGraph, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

