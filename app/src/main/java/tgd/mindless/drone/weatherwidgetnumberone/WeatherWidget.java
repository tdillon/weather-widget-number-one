package tgd.mindless.drone.weatherwidgetnumberone;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
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

        android.util.Log.v(TAG, "onUpdate");

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

        new MyAsyncTask(context).execute("https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/39.3061,-81.3664");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        android.util.Log.v(TAG, "onAppWidgetOptionsChanged");

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


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        android.util.Log.v(TAG, "updateAppWidget  appWidgetId: " + String.valueOf(appWidgetId));


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.tvUpdateTime, DateFormat.getTimeFormat(context).format(new Date()));
/*
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            paint.setTextSize(16);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.MONOSPACE);
    //
            Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            c.drawRect(5, 5, 80, 70, paint);
            c.drawCircle(2, 2, 5, paint);
    //        //c.drawText("foo", 0, 0, paint);
    //
            views.setImageViewBitmap(R.id.ivGraph, bmp);

    */

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static class MyAsyncTask extends AsyncTask<String, Void, WeatherClass> {

        private static final String TAG = "class MyAsyncTask";
        Context context;

        public MyAsyncTask(Context context) {
            android.util.Log.v(TAG, "constructor");
            Toast.makeText(context, "asynctask ctor", Toast.LENGTH_LONG).show();
            this.context = context;
        }

        @Override
        protected WeatherClass doInBackground(String... params) {
            android.util.Log.v(TAG, "doInBackground");

            WeatherClass wc = null;
            byte[] ba = new byte[10];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/39.3061,-81.3664");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //in.read(ba);

                // GSON
                Gson g = new Gson();
                wc = g.fromJson(new InputStreamReader(in, "UTF-8"), WeatherClass.class);
            } catch (Exception e) {
                return null;
            } finally {
                urlConnection.disconnect();
            }


            return wc;
        }

        @Override
        protected void onPostExecute(WeatherClass wc) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, WeatherWidget.class));


            for (int i = 0; i < appWidgetIds.length; ++i) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
                //views.setTextViewText(R.id.appwidget_text, wc.currently.temperature + "(" + '\u00b0' + "F) : " + wc.latitude + "  " + String.valueOf(wc.currently.time) + "  " +
                //        String.valueOf(wc.hourly.data.length) + "  " + String.valueOf(wc.minutely.data.length) +
                //        "   " + String.valueOf(wc.daily.data.length));



/*
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.GREEN);
                paint.setTextSize(16);
                paint.setAntiAlias(true);
                paint.setTypeface(Typeface.MONOSPACE);
//
                Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);
                c.drawRect(5, 5, 80, 70, paint);
                c.drawCircle(2, 2, 15, paint);
                */
//        //c.drawText("foo", 0, 0, paint);
//
                final int FONT_SIZE = 12;
                float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE, context.getResources().getDisplayMetrics());
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                paint.setAntiAlias(true);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(fontSize);

                Rect bounds = new Rect();
                paint.getTextBounds("0123456789SuMoTuWeThFrSa", 0, 24, bounds);


                int width = manager.getAppWidgetOptions(appWidgetIds[i]).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int height = manager.getAppWidgetOptions(appWidgetIds[i]).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, context.getResources().getDisplayMetrics());
                float py = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());

                float hSpacing = px / wc.hourly.data.length;

                float maxTemp = Float.MIN_VALUE;
                float minTemp = Float.MAX_VALUE;

                for (WeatherClass.HourlyClass.HourlyDataClass d : wc.hourly.data) {
                    if (d.temperature > maxTemp) {
                        maxTemp = d.temperature;
                    }
                    if (d.temperature < minTemp) {
                        minTemp = d.temperature;
                    }
                }


                Bitmap bmp = Bitmap.createBitmap((int) px, (int) py, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);
                Toast.makeText(context, String.format("px: %1$f   py: %2$f   hS: %3$f   maxT: %4$f   minT: %5$f   width: %6$d   height: %7$d", px, py, hSpacing, maxTemp, minTemp, width, height), Toast.LENGTH_LONG).show();
                int j = 0;
                for (WeatherClass.HourlyClass.HourlyDataClass d : wc.hourly.data) {
                    c.drawCircle(j++ * hSpacing, (py / (maxTemp - minTemp)) * (maxTemp - d.temperature), 5, paint);
                    c.drawLine(j * hSpacing, 0, j * hSpacing, (int) py, paint);
                    c.drawCircle(j * hSpacing, py * (1 - d.precipProbability), (float) (Math.log(d.precipProbability) / Math.log(2) + 12), paint);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(d.time * 1000);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    if (hour % 4 == 0) {
                        c.drawText(hour == 0 ? cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2) : String.valueOf(cal.get(Calendar.HOUR_OF_DAY)), j * hSpacing, py, paint);
                    }
                }


                //draw horizontal line for every 5 degrees
                //for (var i = 0, j = (i * 5 + (5 - tempMin % 5)), temp = tempMin + j, y = this.bottom - this.tempScale * j; temp < tempMax; j = (++i * 5 + (5 - tempMin % 5)), temp = tempMin + j, y = this.bottom - this.tempScale * j) {
                paint.setTextAlign(Paint.Align.LEFT);
                int k = 0;
                for (float l = (k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l; temp < maxTemp; l = (++k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l) {
                    Toast.makeText(context, String.format("k: %1$d   l: %2$f   temp: %3$f   y: %4$f   py: %5$f   minT: %6$f   maxT: %7$f", k, l, temp, y, py, minTemp, maxTemp), Toast.LENGTH_LONG).show();
                    c.drawLine(0, y, px, y, paint);
                    c.drawText(String.valueOf((int) temp), 0, y + bounds.height() / 2, paint);
                }
                paint.setTextAlign(Paint.Align.CENTER);

                views.setImageViewBitmap(R.id.ivGraph, bmp);


                manager.updateAppWidget(appWidgetIds[i], views);
            }
        }

    }
}

