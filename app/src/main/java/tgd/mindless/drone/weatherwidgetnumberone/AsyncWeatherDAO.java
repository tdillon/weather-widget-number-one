package tgd.mindless.drone.weatherwidgetnumberone;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AsyncWeatherDAO extends AsyncTask<String, Void, WeatherClass> {

    private static final String TAG = "class AsyncWeatherDAO";
    Context context;

    public AsyncWeatherDAO(Context context) {
        android.util.Log.v(TAG, "constructor");
        this.context = context;
    }

    @Override
    protected WeatherClass doInBackground(String... urls) {
        Log.v(TAG, "doInBackground");

        WeatherClass wc = null;
        byte[] ba = new byte[10];
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urls[0]);
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

        Log.v(TAG, "doInBackground end");

        return wc;
    }

    @Override
    protected void onPostExecute(WeatherClass wc) {
        //TODO store data in DAO.  can i send ACTION_APPWIDGET_UPDATE or OPTIONS_CHANGED or MY_CUSTOM_INTENT intent back to widget for redraw?
        if (wc != null) {
            //save to cache
        } else {
            //get from cache
        }

        WeatherWidget.onDataReturned(context, wc);  //TODO what if the config called the dao?
    }

    private WeatherClass getCache() {
        return null;
    }

    private void setCache(WeatherClass wc) {
    
    }

}