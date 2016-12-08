package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.content.Context;

import com.google.gson.Gson;

import java.io.InputStreamReader;

class ThemeDAO {
    static ThemesClass[] getThemes(Context context) {
        Gson g = new Gson();

        ThemesClass[] t;
        try {
            t = g.fromJson(new InputStreamReader(context.getAssets().open("themes.json")), ThemesClass[].class);
        } catch (Exception e) {
            t = null;
        }

        return t;
    }
}
