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
}
