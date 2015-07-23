package tgd.mindless.drone.weatherwidgetnumberone;

/**
 * Created by Trisha on 7/14/2015.
 */
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
    public static final String TYPE = "pref_type";
    public static final String TEMP_LINE_COLOR = "pref_temp_line_color";
}
