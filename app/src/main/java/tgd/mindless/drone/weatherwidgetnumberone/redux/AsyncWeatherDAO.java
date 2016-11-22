package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tgd.mindless.drone.weatherwidgetnumberone.redux.widget.Weather;


public class AsyncWeatherDAO extends AsyncTask<Integer, Void, Weather> {

    private static final String API_BASE_URL = "https://api.forecast.io/forecast/c5f42d85c93f3a489363a8f410a78b57/";
    private static final String TAG = "AsyncWeatherDAO";
    Context context;
    Integer[] mAppWidgetIds;


    public AsyncWeatherDAO(Context context) {
        WidgetConfigPreferences.writeToFile(TAG, "constructor", "");
        this.context = context;
    }

    @Override
    protected Weather doInBackground(Integer... appWidgetIds) {
        WidgetConfigPreferences.writeToFile(TAG, "doInBackground", "IDs: " + TextUtils.join(", ", appWidgetIds));

        mAppWidgetIds = appWidgetIds;

        Weather wc = null;
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
            wc = g.fromJson(line, Weather.class);

            for (int appWidgetId : appWidgetIds) {
                SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
                localPrefs.edit().putString(WidgetConfigPreferences.RAW_WEATHER_JSON, line).apply();
            }
        } catch (Exception e) {
            WidgetConfigPreferences.writeToFile(TAG, "doInBackground", "EXCEPTION: " + e.getMessage());
            return null;
        } finally {
            urlConnection.disconnect();
        }

        return wc;
    }

    @Override
    protected void onPostExecute(Weather wc) {
        if (wc == null) {
            WidgetConfigPreferences.writeToFile(TAG, "onPostExecute", "WeatherClass is null");
            //TODO what should i do here?  we didn't get new data.  should i return null or a previous good data?
            wc = AsyncWeatherDAO.getWeather(context, mAppWidgetIds);
            if (wc == null) {
                WidgetConfigPreferences.writeToFile(TAG, "onPostExecute", "WeatherClass is null for all mAppWidgetIds");
            }
        } else {
            WidgetConfigPreferences.writeToFile(TAG, "onPostExecute", "WeatherClass.length: " + wc.toString().length());
        }
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

        Integer[][] retVal = new Integer[latLonMap.size()][];
        int i = 0;

        for (List<Integer> vals : latLonMap.values()) {
            retVal[i++] = vals.toArray(new Integer[0]);
        }

        return retVal;
    }

    /**
     * For now, get the first WeatherClass for any of the appWidgetIds.
     * Later we could get the most recent WeatherClass.
     * Otherwise returns null;
     *
     * @param context
     * @param appWidgetIds
     * @return
     */
    public static Weather getWeather(Context context, Integer... appWidgetIds) {
        Weather wc = null;

        for (int appWidgetId : appWidgetIds) {
            wc = getWeather(context, appWidgetId);
            if (wc != null) {
                break;
            }
        }

        return wc;
    }

    public static Weather getWeather(Context context, int appWidgetId) {
        SharedPreferences localPrefs = context.getSharedPreferences(WidgetConfigPreferences.getSharedPreferenceName(appWidgetId), Context.MODE_PRIVATE);
        Gson g = new Gson();
        Weather weather = g.fromJson(localPrefs.getString(WidgetConfigPreferences.RAW_WEATHER_JSON, ""), Weather.class);

        return weather;
    }

    public static Weather getDummyWeather(Context context) {
        Gson g = new Gson();
        return g.fromJson(context.getResources().getString(R.string.sample_weather_data), Weather.class);
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