package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.Calendar;

public class WeatherWidget extends AppWidgetProvider {

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
            views.setImageViewBitmap(R.id.ivGraph, (new Drawer(t, new Positionings(t, weather, px, py))).render());
        } catch (Exception e) {
            Log.i("not going to happen", "file not found themes.json 222");
            Log.e("themes.json", "drawWidget: ", e);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


