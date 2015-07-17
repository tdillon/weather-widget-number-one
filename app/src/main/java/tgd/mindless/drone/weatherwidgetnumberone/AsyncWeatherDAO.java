package tgd.mindless.drone.weatherwidgetnumberone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AsyncWeatherDAO extends AsyncTask<Integer, Void, WeatherClass> {

    private static final String API_BASE_URL = "https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/";
    private static final String TAG = "class AsyncWeatherDAO";
    Context context;
    Integer[] mAppWidgetIds;


    public AsyncWeatherDAO(Context context) {
        android.util.Log.v(TAG, "constructor");
        this.context = context;
    }

    @Override
    protected WeatherClass doInBackground(Integer... appWidgetIds) {
        Log.v(TAG, "doInBackground");

        mAppWidgetIds = appWidgetIds;

        WeatherClass wc = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(API_BASE_URL + getLatLon(context, appWidgetIds[0]));
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            line = total.toString();

            Gson g = new Gson();
            wc = g.fromJson(line, WeatherClass.class);

            for (int appWidgetId : appWidgetIds) {
                SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
                localPrefs.edit().putString(WidgetConfigPreferences.RAW_WEATHER_JSON, line).apply();
            }
        } catch (Exception e) {
            return null;
        } finally {
            urlConnection.disconnect();
        }

        Log.v(TAG, "doInBackground end");

        return wc;
    }

    @Override
    protected void onPostExecute(WeatherClass wc) {
        WeatherWidget.onDataReturned(context, wc, mAppWidgetIds);
    }

    public static Integer[][] groupByLatLon(Context context, int[] appWidgetIds) {
        Map<String, List<Integer>> latLonMap = new HashMap<String, List<Integer>>();
        String latlon;
        List<Integer> val;

        for (int i : appWidgetIds) {
            latlon = getLatLon(context, i);

            if (latLonMap.containsKey(latlon)) {
                val = latLonMap.get(latlon);
                val.add(i);
            } else {
                val = new ArrayList<Integer>();
                val.add(i);
                latLonMap.put(latlon, val);
            }
        }

        Set<String> keys = latLonMap.keySet();
        for (String key : keys) {
            Log.v(TAG, "groupByLatLon   key: " + key + "   values: " + TextUtils.join(", ", latLonMap.get(key)));
        }

        Integer[][] retVal = new Integer[latLonMap.size()][];
        int i = 0;

        for (List<Integer> vals : latLonMap.values()) {
            retVal[i++] = vals.toArray(new Integer[0]);
        }

        return retVal;
    }

    public static WeatherClass getWeather(Context context, int appWidgetId) {
        SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        Gson g = new Gson();
        WeatherClass wc = g.fromJson(localPrefs.getString(WidgetConfigPreferences.RAW_WEATHER_JSON, ""), WeatherClass.class);

        return wc;
    }

    private static String getLatLon(Context context, int appWidgetId) {
        SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        return localPrefs.getString(WidgetConfigPreferences.LATITUDE, "") + ',' + localPrefs.getString(WidgetConfigPreferences.LONGITUDE, "");
    }

    public static void setConfigComplete(Context context, int appWidgetId) {
        SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        localPrefs.edit().putBoolean(WidgetConfigPreferences.WIDGET_CONFIG_COMPLETE, true).apply();
    }

    public static boolean getConfigComplete(Context context, int appWidgetId) {
        SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        return localPrefs.getBoolean(WidgetConfigPreferences.WIDGET_CONFIG_COMPLETE, false);
    }
}