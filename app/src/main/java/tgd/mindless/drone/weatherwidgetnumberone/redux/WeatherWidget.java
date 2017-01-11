package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.Calendar;

public class WeatherWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = WeatherWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Integer[] newArray = new Integer[appWidgetIds.length];
        int i = 0;
        for (int value : appWidgetIds) {
            newArray[i++] = value;
        }

        new AsyncWeatherDAO(context).execute(newArray);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        drawWidget(context, appWidgetId, appWidgetManager, AsyncWeatherDAO.getWeather(context, appWidgetId));
    }

    public static void onDataReturned(Context context, Weather weather, Integer[] appWidgetIds) {
        if (weather == null) {
            return;  //TODO what should the user see when we can get no data at all?
        } else {
        }
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        for (int appWidgetId : appWidgetIds) {
            drawWidget(context, appWidgetId, manager, weather);
        }
    }

    private static void drawWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager, Weather weather) {

        ThemesClass[] themes = ThemeDAO.getThemes(context);
        SharedPreferences sharedPref = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        int themePref = Integer.parseInt(sharedPref.getString(WeatherWidgetConfigureFragment.KEY_PREF_THEME, "0"));  //TODO what should i do if pref value doesn't exist? for now default to 0
        ThemesClass theme = themes[themePref];
        Log.i("widgetID:themePref", String.valueOf(appWidgetId) + ":" + String.valueOf(themePref));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        Calendar cal = Calendar.getInstance();
        //cal.setTimeInMillis(weather.currently.time * 1000);
        views.setTextViewText(R.id.tvUpdateTime, DateFormat.getTimeFormat(context).format(cal.getTime()));

        int width = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int widthMAX = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int height = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());
        float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());

        views.setImageViewBitmap(R.id.ivGraph, (new Drawer(theme, new Positionings(theme, weather, px, py))).render());

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


