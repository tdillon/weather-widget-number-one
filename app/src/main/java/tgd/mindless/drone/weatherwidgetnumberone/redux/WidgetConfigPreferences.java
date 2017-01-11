package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class WidgetConfigPreferences {

    /**
     * Passed to getSharedPreferences or setSharedPreferencesName  so that each widget has it's own set of prefs
     */
    public static final String SHARED_PREFERENCE_PREFIX = "prefs_";


    public static final String getSharedPreferenceName(int appWidgetId) {
        return SHARED_PREFERENCE_PREFIX + String.valueOf(appWidgetId);
    }


    //TODO do these have to be prefixed with pref_ ??  can i use xml somehow to abstract this out?
    public static final String LATITUDE = "pref_latitude";
    public static final String LONGITUDE = "pref_longitude";

    public static final String RAW_WEATHER_JSON = "pref_raw_data";
    public static final String WIDGET_CONFIG_COMPLETE = "pref_config_complete";
    public static final String TEMP_WIDTH = "pref_temp_width";
    public static final String PRECIP_WIDTH = "pref_precip_width";
    public static final String TEMP_LINE_WIDTH = "pref_temp_line_width";
    public static final String PRECIP_LINE_WIDTH = "pref_precip_line_width";
    public static final String TEMP_FONT_SIZE = "pref_temp_font_size";
    public static final String TIME_FONT_SIZE = "pref_time_font_size";
    public static final String TYPE = "pref_type";
    public static final String TEMP_DOT_COLOR = "pref_temp_dot_color";
    public static final String TEMP_LINE_COLOR = "pref_temp_line_color";
    public static final String DAYLIGHT_COLOR = "pref_daylight_color";
    public static final String TEMP_FONT_COLOR = "pref_temp_font_color";
    public static final String TIME_FONT_COLOR = "pref_time_font_color";
}
