package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.Calendar;

public class WeatherWidget extends AppWidgetProvider {

    private static final String CLASS_NAME = "WeatherWidget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onUpdate", "# IDs: " + String.valueOf(appWidgetIds.length));

        //BUG onUpdate is called even though a configuration activity is defined google code issue #3696
        if (appWidgetIds.length == 1 && !AsyncWeatherDAO.getConfigComplete(context, appWidgetIds[0])) {
            WidgetConfigPreferences.writeToFile(CLASS_NAME, "onUpdate", "ID: " + String.valueOf(appWidgetIds[0]) + " not configured");
            return;
        }

        Integer[][] appWidgetIdsGroupedByLatLon = AsyncWeatherDAO.groupByLatLon(context, appWidgetIds);

        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onUpdate", appWidgetIdsGroupedByLatLon.length + " groups of lat/lons");
        for (Integer[] appWidgetIdGroup : appWidgetIdsGroupedByLatLon) {
            WidgetConfigPreferences.writeToFile(CLASS_NAME, "onUpdate", "execute group with " + appWidgetIdGroup.length + " widgets");
            new AsyncWeatherDAO(context).execute(appWidgetIdGroup);
        }
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onUpdate", "done");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.i("bundleWidth", String.valueOf(newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)));
        drawWidget(context, appWidgetId, appWidgetManager, AsyncWeatherDAO.getWeather(context, appWidgetId));
    }

    @Override
    public void onEnabled(Context context) {
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onEnabled", "");
    }

    @Override
    public void onDisabled(Context context) {
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onDisabled", "");
    }

    public static void onDataReturned(Context context, Weather weather, Integer[] appWidgetIds) {
        if (weather == null) {
            WidgetConfigPreferences.writeToFile(CLASS_NAME, "onDataReturned", "IDs: " + TextUtils.join(", ", appWidgetIds) + "  WeatherClass is null");
            return;  //TODO what should the user see when we can get no data at all?
        } else {
            WidgetConfigPreferences.writeToFile(CLASS_NAME, "onDataReturned", "IDs: " + TextUtils.join(", ", appWidgetIds) + "  WeatherClass.length: " + weather.toString().length());
        }
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetId, manager, weather);
        }
    }

    public static void onConfigured(Context context, int appWidgetId, boolean needsDataUpdate) {
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "onConfigured", "ID: " + appWidgetId + "   needsDataUpdate: " + needsDataUpdate);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (needsDataUpdate) {
            new AsyncWeatherDAO(context).execute(appWidgetId);
        } else {
            //TODO if cannot get API data, then this widget cannot draw.  need to be able to pull from other widget's data.
            //TODO DAO should have a mapping of widgetIds and LAT/LON that it keeps track of.
            //TODO currently DAO does not delete old data.
            drawWidget(context, appWidgetId, appWidgetManager, AsyncWeatherDAO.getWeather(context, appWidgetId));
        }
    }

    private static void drawWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager, Weather weather) {
        WidgetConfigPreferences.writeToFile(CLASS_NAME, "drawWidget", "ID: " + appWidgetId);

        SharedPreferences sharedPref = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        Calendar cal = Calendar.getInstance();
        //cal.setTimeInMillis(weather.currently.time * 1000);
        views.setTextViewText(R.id.tvUpdateTime, DateFormat.getTimeFormat(context).format(cal.getTime()));

        int width = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int widthMAX = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int height = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());
        float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());

        Log.i("width", String.valueOf(width));
        Log.i("widthMAX", String.valueOf(widthMAX));
        Log.i("height", String.valueOf(height));
        Log.i("px", String.valueOf(px));
        Log.i("py", String.valueOf(py));
        //views.setImageViewBitmap(R.id.ivGraph, WeatherGraphDrawer.draw(weatherClass, px, py, sharedPref, context.getResources().getDisplayMetrics()));

        Gson g = new Gson();
        try {
            ThemesClass t = g.fromJson( new InputStreamReader( context.getAssets().open("themes.json")), ThemesClass.class);
            views.setImageViewBitmap(R.id.ivGraph, (new Drawer(new Positionings(t, weather, px, py))).render());
        } catch (Exception e) {
            Log.i("not going to happen", "file not found themes.json 222");
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


