package tgd.mindless.drone.weatherwidgetnumberone;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.Locale;

final public class WeatherGraphDrawer {

    final static String TAG = "WeatherGraphDrawer";

    static public Bitmap draw(WeatherClass wc, float px, float py, SharedPreferences sharedPref, DisplayMetrics dm) {
        Log.v(TAG, "draw");

        Bitmap bmp;

        if (sharedPref.getString(WidgetConfigPreferences.TYPE, "TODO").equals("Hourly")) {  //TODO pull Hourly from xml
            //TODO draw hourly graph
            bmp = drawHourly(wc, px, py, sharedPref, dm);
        } else {  //daily
            //TODO draw daily graph
            bmp = drawDaily(wc, px, py, sharedPref, dm);
        }

        return bmp;
    }

    private static Bitmap drawDaily(WeatherClass wc, float px, float py, SharedPreferences sharedPref, DisplayMetrics dm) {

        boolean showTimeLines = sharedPref.getBoolean("pref_showTimeLines", false);
        boolean showTempLines = sharedPref.getBoolean("pref_showTempLines", false);
        int tempWidth = sharedPref.getInt(WidgetConfigPreferences.TEMP_WIDTH, 1);
        int precipWidth = sharedPref.getInt(WidgetConfigPreferences.PRECIP_WIDTH, 1);
        String tempLineColor = sharedPref.getString(WidgetConfigPreferences.TEMP_LINE_COLOR, "TODO");
        Log.v(TAG, "draw   color: " + tempLineColor);

        final int FONT_SIZE = 12;
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE, dm);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();
        paint.getTextBounds("0123456789SuMoTuWeThFrSa", 0, 24, bounds);


        float hSpacing = px / wc.daily.data.length;  //CHANGE
        float dayWidth = px / wc.daily.data.length;
        float halfDayWidth = dayWidth / 2;

        float maxTemp = Float.MIN_VALUE;
        float minTemp = Float.MAX_VALUE;

        for (WeatherClass.DailyClass.DailyDataClass d : wc.daily.data) {  //CHANGE   can i add minTemp() and getMaxTemp to DailyClass and HourlyClass?
            if (d.temperatureMax > maxTemp) {
                maxTemp = d.temperatureMax;
            }
            if (d.temperatureMin < minTemp) {
                minTemp = d.temperatureMin;
            }
        }

        Bitmap bmp = Bitmap.createBitmap((int) px, (int) py, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        int j = 0;
        for (WeatherClass.DailyClass.DailyDataClass d : wc.daily.data) {  //CHANGE
            //Draw max temp line/dots
            c.drawCircle((j + (d.temperatureMaxTime - d.time) / 86400f) * dayWidth, (py / (maxTemp - minTemp)) * (maxTemp - d.temperatureMax), tempWidth, paint);
            //Draw min temp line/dots
            c.drawCircle((j + (d.temperatureMinTime - d.time) / 86400f) * dayWidth, (py / (maxTemp - minTemp)) * (maxTemp - d.temperatureMin), tempWidth, paint);
            //TODO draw precipitation line/dots
            if (d.precipProbability > 0) {
                c.drawCircle((j + (d.precipIntensityMaxTime - d.time) / 86400f) * dayWidth, py * (1 - d.precipProbability), precipWidth, paint);
            }

            //Write day of week centered in day
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d.time * 1000);
            c.drawText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2), j * dayWidth + halfDayWidth, py, paint);

            //TODO draw cloud cover rectangles

            ++j;
        }


        //draw horizontal line for every 5 degrees
        paint.setTextAlign(Paint.Align.LEFT);
        int k = 0;
        for (float l = (k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l; temp < maxTemp; l = (++k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l) {
            if (showTempLines) {
                c.drawLine(0, y, px, y, paint);
            }
            c.drawText(String.valueOf((int) temp), 0, y + bounds.height() / 2, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);

        return bmp;
    }

    private static Bitmap drawHourly(WeatherClass wc, float px, float py, SharedPreferences sharedPref, DisplayMetrics dm) {

        boolean showTimeLines = sharedPref.getBoolean("pref_showTimeLines", false);
        boolean showTempLines = sharedPref.getBoolean("pref_showTempLines", false);
        int tempWidth = sharedPref.getInt(WidgetConfigPreferences.TEMP_WIDTH, 1);
        String tempLineColor = sharedPref.getString(WidgetConfigPreferences.TEMP_LINE_COLOR, "TODO");
        Log.v(TAG, "draw   color: " + tempLineColor);

        final int FONT_SIZE = 12;
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE, dm);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSize);

        Rect bounds = new Rect();
        paint.getTextBounds("0123456789SuMoTuWeThFrSa", 0, 24, bounds);


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
        int j = 0;
        for (WeatherClass.HourlyClass.HourlyDataClass d : wc.hourly.data) {
            c.drawCircle(j++ * hSpacing, (py / (maxTemp - minTemp)) * (maxTemp - d.temperature), tempWidth, paint);
            c.drawCircle(j * hSpacing, py * (1 - d.precipProbability), (float) (Math.log(d.precipProbability) / Math.log(2) + 12), paint);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d.time * 1000);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour % 4 == 0) {
                if (showTimeLines) {
                    c.drawLine(j * hSpacing, 0, j * hSpacing, (int) py, paint);
                }
                c.drawText(hour == 0 ? cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2) : String.valueOf(cal.get(Calendar.HOUR_OF_DAY)), j * hSpacing, py, paint);
            }
        }


        //draw horizontal line for every 5 degrees
        paint.setTextAlign(Paint.Align.LEFT);
        int k = 0;
        for (float l = (k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l; temp < maxTemp; l = (++k * 5 + (5 - minTemp % 5)), temp = minTemp + l, y = py - (py / (maxTemp - minTemp)) * l) {
            if (showTempLines) {
                c.drawLine(0, y, px, y, paint);
            }
            c.drawText(String.valueOf((int) temp), 0, y + bounds.height() / 2, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);

        return bmp;
    }
}
