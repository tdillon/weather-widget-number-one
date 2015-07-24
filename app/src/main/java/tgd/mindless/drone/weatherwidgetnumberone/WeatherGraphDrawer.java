package tgd.mindless.drone.weatherwidgetnumberone;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
        int tempLineWidth = sharedPref.getInt(WidgetConfigPreferences.TEMP_LINE_WIDTH, 1);
        String tempDotColor = sharedPref.getString(WidgetConfigPreferences.TEMP_DOT_COLOR, "TODO");
        String tempLineColor = sharedPref.getString(WidgetConfigPreferences.TEMP_LINE_COLOR, "TODO");
        String daylightColor = sharedPref.getString(WidgetConfigPreferences.DAYLIGHT_COLOR, "TODO");
        int tempFontColor = Color.parseColor(sharedPref.getString(WidgetConfigPreferences.TEMP_FONT_COLOR, "TODO"));
        int timeFontColor = Color.parseColor(sharedPref.getString(WidgetConfigPreferences.TIME_FONT_COLOR, "TODO"));
        float tempFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sharedPref.getInt(WidgetConfigPreferences.TEMP_FONT_SIZE, 1), dm);
        float timeFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sharedPref.getInt(WidgetConfigPreferences.TIME_FONT_SIZE, 1), dm);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(tempLineWidth);

        Rect bounds = new Rect();
        paint.setTextSize(timeFontSize);
        paint.getTextBounds("SuMoTuWeThFrSa", 0, 14, bounds);

        int paddingTop, paddingRight;
        paddingRight = paddingTop = Math.max(tempWidth, precipWidth);
        Log.v(TAG, "draw   color: " + tempLineColor + "   padding: " + String.valueOf(paddingTop));

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

        float graphHeight = py - paddingTop - bounds.height();
        float graphBottom = py - bounds.height();

        Map<Integer, Float> ys = new HashMap<Integer, Float>();
        Rect bounds2 = new Rect();
        int maxTempTextWidth = Integer.MIN_VALUE;
        paint.setTextSize(tempFontSize);
        for (float temp = minTemp + (5 - minTemp % 5), y = graphBottom - ((temp - minTemp) * (graphHeight / (maxTemp - minTemp))); temp < maxTemp; temp += 5, y = graphBottom - ((temp - minTemp) * (graphHeight / (maxTemp - minTemp)))) {
            paint.getTextBounds(String.valueOf((int) temp), 0, String.valueOf((int) temp).length(), bounds2);
            if (bounds2.width() > maxTempTextWidth) {
                maxTempTextWidth = bounds2.width();
            }
            ys.put((int) temp, y);
        }

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(tempFontSize);
        paint.setColor(tempFontColor);
        for (Integer key : ys.keySet()) {
            if (showTempLines) {
                c.drawLine(maxTempTextWidth, ys.get(key), px - paddingRight, ys.get(key), paint);
            }
            paint.getTextBounds(key.toString(), 0, key.toString().length(), bounds2);
            c.drawText(key.toString(), bounds2.width(), ys.get(key) + bounds2.height() / 2, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);

        float dayWidth = (px - paddingRight - maxTempTextWidth) / wc.daily.data.length;
        float halfDayWidth = dayWidth / 2;

        //http://radar.weather.gov/Legend/N0R/DMX_N0R_Legend_0.gif
        //https://en.wikipedia.org/wiki/DBZ_(meteorology)
        SortedMap<Float, Integer> dbzs = new TreeMap<Float, Integer>();
        dbzs.put(0f, Color.TRANSPARENT);
        dbzs.put(.003f, Color.argb(255, 4, 233, 231));
        dbzs.put(.006f, Color.argb(255, 1, 159, 244));
        dbzs.put(.01f, Color.argb(255, 3, 0, 244));
        dbzs.put(.02f, Color.argb(255, 2, 253, 2));
        dbzs.put(.05f, Color.argb(255, 1, 197, 1));
        dbzs.put(.1f, Color.argb(255, 0, 142, 0));
        dbzs.put(.22f, Color.argb(255, 253, 248, 2));
        dbzs.put(.45f, Color.argb(255, 229, 188, 0));
        dbzs.put(.92f, Color.argb(255, 253, 149, 0));
        dbzs.put(1.9f, Color.argb(255, 253, 0, 0));
        dbzs.put(4f, Color.argb(255, 212, 0, 0));
        dbzs.put(8f, Color.argb(255, 188, 0, 0));
        dbzs.put(16.6f, Color.argb(255, 248, 0, 253));
        dbzs.put(34f, Color.argb(255, 152, 84, 198));
        dbzs.put(69.9f, Color.argb(255, 253, 253, 253));

        paint.setTextSize(timeFontSize);
        float prevMaxTempX = 0, prevMaxTempY = 0, prevMinTempX = 0, prevMinTempY = 0;
        int j = 0;
        for (WeatherClass.DailyClass.DailyDataClass d : wc.daily.data) {  //CHANGE
            //TODO draw cloud cover rectangles
            paint.setColor(Color.parseColor(daylightColor));
            paint.setAlpha((int) ((1 - d.cloudCover) * 255));
            c.drawRect(maxTempTextWidth + (j + (d.sunriseTime - d.time) / 86400f) * dayWidth, paddingTop, maxTempTextWidth + (j + (d.sunsetTime - d.time) / 86400f) * dayWidth, graphBottom, paint);

            //Write day of week centered in day
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d.time * 1000);
            paint.setColor(timeFontColor);
            c.drawText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2), maxTempTextWidth + j * dayWidth + halfDayWidth, py, paint);

            //Draw max temp line/dots
            float maxTempX = maxTempTextWidth + (j + (d.temperatureMaxTime - d.time) / 86400f) * dayWidth;
            float maxTempY = paddingTop + (graphHeight / (maxTemp - minTemp)) * (maxTemp - d.temperatureMax);
            if (prevMaxTempX > 0) {
                paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
                c.drawLine(prevMaxTempX, prevMaxTempY, maxTempX, maxTempY, paint);
            }
            prevMaxTempX = maxTempX;
            prevMaxTempY = maxTempY;
            paint.setColor(Color.parseColor(tempDotColor));  //TODO set colors per prefs
            c.drawCircle(maxTempX, maxTempY, tempWidth, paint);

            //Draw min temp line/dots
            float minTempX = maxTempTextWidth + (j + (d.temperatureMinTime - d.time) / 86400f) * dayWidth;
            float minTempY = paddingTop + (graphHeight / (maxTemp - minTemp)) * (maxTemp - d.temperatureMin);
            if (prevMinTempX > 0) {
                paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
                c.drawLine(prevMinTempX, prevMinTempY, minTempX, minTempY, paint);
            }
            prevMinTempX = minTempX;
            prevMinTempY = minTempY;
            paint.setColor(Color.parseColor(tempDotColor));  //TODO set colors per prefs
            c.drawCircle(minTempX, minTempY, tempWidth, paint);

            //TODO draw precipitation line/dots
            for (Float key : dbzs.keySet()) {
                if (key > d.precipIntensityMax) {
                    break;
                }
                paint.setColor(dbzs.get(key));
            }
            if (d.precipProbability > 0) {
                c.drawCircle(maxTempTextWidth + ((j + (d.precipIntensityMaxTime - d.time) / 86400f) * dayWidth), paddingTop + graphHeight * (1 - d.precipProbability), precipWidth, paint);
            }

            ++j;
        }

        return bmp;
    }


    private static Bitmap drawHourly(WeatherClass wc, float px, float py, SharedPreferences sharedPref, DisplayMetrics dm) {

        boolean showTimeLines = sharedPref.getBoolean("pref_showTimeLines", false);
        boolean showTempLines = sharedPref.getBoolean("pref_showTempLines", false);
        int tempWidth = sharedPref.getInt(WidgetConfigPreferences.TEMP_WIDTH, 1);
        int precipWidth = sharedPref.getInt(WidgetConfigPreferences.PRECIP_WIDTH, 1);
        int tempLineWidth = sharedPref.getInt(WidgetConfigPreferences.TEMP_LINE_WIDTH, 1);
        String tempDotColor = sharedPref.getString(WidgetConfigPreferences.TEMP_DOT_COLOR, "TODO");
        String tempLineColor = sharedPref.getString(WidgetConfigPreferences.TEMP_LINE_COLOR, "TODO");
        String daylightColor = sharedPref.getString(WidgetConfigPreferences.DAYLIGHT_COLOR, "TODO");
        int tempFontColor = Color.parseColor(sharedPref.getString(WidgetConfigPreferences.TEMP_FONT_COLOR, "TODO"));
        int timeFontColor = Color.parseColor(sharedPref.getString(WidgetConfigPreferences.TIME_FONT_COLOR, "TODO"));
        float tempFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sharedPref.getInt(WidgetConfigPreferences.TEMP_FONT_SIZE, 1), dm);
        float timeFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sharedPref.getInt(WidgetConfigPreferences.TIME_FONT_SIZE, 1), dm);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(tempLineWidth);

        Rect bounds = new Rect();
        paint.setTextSize(timeFontSize);
        paint.getTextBounds("0123456789SuMoTuWeThFrSa", 0, 24, bounds);

        int paddingTop, paddingRight;
        paddingRight = paddingTop = Math.max(tempWidth, precipWidth);
        Log.v(TAG, "draw   color: " + tempLineColor + "   padding: " + String.valueOf(paddingTop));

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

        float graphHeight = py - paddingTop - bounds.height();
        float graphBottom = py - bounds.height();

        Map<Integer, Float> ys = new HashMap<Integer, Float>();
        Rect bounds2 = new Rect();
        int maxTempTextWidth = Integer.MIN_VALUE;
        paint.setTextSize(tempFontSize);
        for (float temp = minTemp + (5 - minTemp % 5), y = graphBottom - ((temp - minTemp) * (graphHeight / (maxTemp - minTemp))); temp < maxTemp; temp += 5, y = graphBottom - ((temp - minTemp) * (graphHeight / (maxTemp - minTemp)))) {
            paint.getTextBounds(String.valueOf((int) temp), 0, String.valueOf((int) temp).length(), bounds2);
            if (bounds2.width() > maxTempTextWidth) {
                maxTempTextWidth = bounds2.width();
            }
            ys.put((int) temp, y);
        }

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(tempFontSize);
        paint.setColor(tempFontColor);
        for (Integer key : ys.keySet()) {
            if (showTempLines) {
                c.drawLine(maxTempTextWidth, ys.get(key), px - paddingRight, ys.get(key), paint);
            }
            paint.getTextBounds(key.toString(), 0, key.toString().length(), bounds2);
            c.drawText(key.toString(), bounds2.width(), ys.get(key) + bounds2.height() / 2, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);


        //float dayWidth = (px - paddingRight - maxTempTextWidth) / wc.daily.data.length;
        //float halfDayWidth = dayWidth / 2;

        //http://radar.weather.gov/Legend/N0R/DMX_N0R_Legend_0.gif
        //https://en.wikipedia.org/wiki/DBZ_(meteorology)
        SortedMap<Float, Integer> dbzs = new TreeMap<Float, Integer>();
        dbzs.put(0f, Color.TRANSPARENT);
        dbzs.put(.003f, Color.argb(255, 4, 233, 231));
        dbzs.put(.006f, Color.argb(255, 1, 159, 244));
        dbzs.put(.01f, Color.argb(255, 3, 0, 244));
        dbzs.put(.02f, Color.argb(255, 2, 253, 2));
        dbzs.put(.05f, Color.argb(255, 1, 197, 1));
        dbzs.put(.1f, Color.argb(255, 0, 142, 0));
        dbzs.put(.22f, Color.argb(255, 253, 248, 2));
        dbzs.put(.45f, Color.argb(255, 229, 188, 0));
        dbzs.put(.92f, Color.argb(255, 253, 149, 0));
        dbzs.put(1.9f, Color.argb(255, 253, 0, 0));
        dbzs.put(4f, Color.argb(255, 212, 0, 0));
        dbzs.put(8f, Color.argb(255, 188, 0, 0));
        dbzs.put(16.6f, Color.argb(255, 248, 0, 253));
        dbzs.put(34f, Color.argb(255, 152, 84, 198));
        dbzs.put(69.9f, Color.argb(255, 253, 253, 253));

        float prevTempY = 0, prevPrecY = -1, prevHourX = -1;
        int lastPrecipColor = Color.TRANSPARENT;
        int j = 0;
        for (WeatherClass.HourlyClass.HourlyDataClass d : wc.hourly.data) {  //CHANGE
            //TODO draw cloud cover rectangles
            /*
            paint.setColor(Color.parseColor(daylightColor));
            paint.setAlpha((int) ((1 - d.cloudCover) * 255));
            c.drawRect(maxTempTextWidth + (j + (d.sunriseTime - d.time) / 86400f) * dayWidth, paddingTop, maxTempTextWidth + (j + (d.sunsetTime - d.time) / 86400f) * dayWidth, graphBottom, paint);
            */

            //Write day of week centered in day
            /*
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d.time * 1000);
            paint.setColor(timeFontColor);
            c.drawText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2), maxTempTextWidth + j * dayWidth + halfDayWidth, py, paint);
            */

            float hourSpace = (px - paddingRight - maxTempTextWidth) / (wc.hourly.data.length - 1);
            float hourX = j * hourSpace + maxTempTextWidth;

            //Write every 4th Hour/Day centered on hour
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d.time * 1000);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour % 4 == 0) {
                paint.setTextSize(timeFontSize);
                paint.setColor(timeFontColor);
                if (showTimeLines) {
                    c.drawLine(hourX, paddingTop, hourX, graphBottom, paint);
                }
                c.drawText(hour == 0 ? cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 2) : String.valueOf(cal.get(Calendar.HOUR_OF_DAY)), hourX, py, paint);
            }

            //Draw temp line/dots
            float tempY = paddingTop + (graphHeight / (maxTemp - minTemp)) * (maxTemp - d.temperature);
            if (prevHourX >= 0) {
                paint.setColor(Color.parseColor(tempLineColor));  //TODO set colors per prefs
                c.drawLine(prevHourX, prevTempY, hourX, tempY, paint);
            }
            prevTempY = tempY;
            paint.setColor(Color.parseColor(tempDotColor));  //TODO set colors per prefs
            c.drawCircle(hourX, tempY, tempWidth, paint);

            //TODO draw precipitation line/dots
            int currentPrecipColor = Color.TRANSPARENT;
            for (Float key : dbzs.keySet()) {
                if (key > d.precipIntensity) {
                    break;
                }
                currentPrecipColor = dbzs.get(key);
            }
            float precY = paddingTop + graphHeight * (1 - d.precipProbability);
            if (prevPrecY >= 0) {
                //paint.setColor(Color.parseColor(tempLineColor));  //TODO set line as gradient
                 paint.setShader(new LinearGradient(prevHourX, prevPrecY, hourX, precY, lastPrecipColor, currentPrecipColor, Shader.TileMode.MIRROR));
                // canvas.drawPath(arrowPath, paint);
                c.drawLine(prevHourX, prevPrecY, hourX, precY, paint);
                paint.setShader(null);
                //TODO add precip dot width and precip line width as preferences
            }
            prevPrecY = precY;
            prevHourX = hourX;
            lastPrecipColor = currentPrecipColor;
            //if (d.precipProbability > 0) {
            paint.setColor(currentPrecipColor);
            c.drawCircle(hourX, precY, precipWidth, paint);
            //}

            ++j;
        }

        return bmp;
    }
}