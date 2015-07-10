package tgd.mindless.drone.weatherwidgetnumberone;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Trisha on 7/10/2015.
 */
final public class WeatherGraphDrawer {

    final static String TAG = "WeatherGraphDrawer";

    static public Bitmap draw(WeatherClass wc, float px, float py, SharedPreferences sharedPref, DisplayMetrics dm) {
        Log.v(TAG, "draw");


        boolean showTimeLines = sharedPref.getBoolean("pref_showTimeLines", false);
        boolean showTempLines = sharedPref.getBoolean("pref_showTempLines", false);

        final int FONT_SIZE = 12;
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE, dm);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
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
            c.drawCircle(j++ * hSpacing, (py / (maxTemp - minTemp)) * (maxTemp - d.temperature), 5, paint);
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
