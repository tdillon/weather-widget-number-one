package tgd.mindless.drone.weatherwidgetnumberone.redux

import android.graphics.Color

class DBZ {
    //Use the color palette from weather underground: http://icons-ak.wunderground.com/data/wximagenew/w/wuproductteam/9.jpg
    //https://www.wunderground.com/blog/wuproductteam/radar-palette-improvements
    //Calc the dBZ per: https://en.wikipedia.org/wiki/DBZ_(meteorology)
    //JavaScript to perform calculation:
    //getRainfallInInches = dbz => (Math.pow(Math.pow(10, (dbz / 10)) / 200, 5 / 8) / 25.4).toFixed(3)
    static final float INTENSITY_85 = 294.797
    static final float INTENSITY_80 = 143.556
    static final float INTENSITY_75 = 69.907
    static final float INTENSITY_70 = 34.043
    static final float INTENSITY_65 = 16.578
    static final float INTENSITY_60 = 8.073
    static final float INTENSITY_55 = 3.931
    static final float INTENSITY_50 = 1.914
    static final float INTENSITY_45 = 0.932
    static final float INTENSITY_40 = 0.454
    static final float INTENSITY_35 = 0.221
    static final float INTENSITY_30 = 0.108
    static final float INTENSITY_25 = 0.052
    static final float INTENSITY_20 = 0.026
    static final float INTENSITY_15 = 0.012
    static final float INTENSITY_10 = 0.006
    static final float INTENSITY_5 = 0.003
    static final float INTENSITY_0 = 0.0

    static final int COLOR_RAIN_85 = Color.argb(255, 255, 255, 255);
    static final int COLOR_RAIN_80 = Color.argb(255, 100, 0, 99);
    static final int COLOR_RAIN_75 = Color.argb(255, 130, 2, 138);
    static final int COLOR_RAIN_70 = Color.argb(255, 169, 0, 203);
    static final int COLOR_RAIN_65 = Color.argb(255, 235, 0, 139);
    static final int COLOR_RAIN_60 = Color.argb(255, 178, 0, 0);
    static final int COLOR_RAIN_55 = Color.argb(255, 232, 0, 0);
    static final int COLOR_RAIN_50 = Color.argb(255, 245, 99, 0);
    static final int COLOR_RAIN_45 = Color.argb(255, 255, 138, 42);
    static final int COLOR_RAIN_40 = Color.argb(255, 248, 184, 0);
    static final int COLOR_RAIN_35 = Color.argb(255, 255, 236, 1);
    static final int COLOR_RAIN_30 = Color.argb(255, 0, 94, 41);
    static final int COLOR_RAIN_25 = Color.argb(255, 0, 108, 46);
    static final int COLOR_RAIN_20 = Color.argb(255, 0, 137, 55);
    static final int COLOR_RAIN_15 = Color.argb(255, 0, 165, 67);
    static final int COLOR_RAIN_10 = Color.argb(255, 0, 198, 85);
    static final int COLOR_RAIN_5 = Color.argb(255, 0, 225, 129);
    static final int COLOR_RAIN_0 = Color.argb(255, 0, 252, 173);

    static final int COLOR_SLEET_85 = Color.argb(255, 120, 15, 100);
    static final int COLOR_SLEET_80 = Color.argb(255, 130, 10, 100);
    static final int COLOR_SLEET_75 = Color.argb(255, 142, 6, 104);
    static final int COLOR_SLEET_70 = Color.argb(255, 155, 22, 115);
    static final int COLOR_SLEET_65 = Color.argb(255, 161, 39, 124);
    static final int COLOR_SLEET_60 = Color.argb(255, 171, 49, 134);
    static final int COLOR_SLEET_55 = Color.argb(255, 182, 64, 138);
    static final int COLOR_SLEET_50 = Color.argb(255, 189, 78, 147);
    static final int COLOR_SLEET_45 = Color.argb(255, 197, 91, 157);
    static final int COLOR_SLEET_40 = Color.argb(255, 205, 105, 165);
    static final int COLOR_SLEET_35 = Color.argb(255, 212, 118, 171);
    static final int COLOR_SLEET_30 = Color.argb(255, 219, 131, 181);
    static final int COLOR_SLEET_25 = Color.argb(255, 226, 145, 188);
    static final int COLOR_SLEET_20 = Color.argb(255, 233, 158, 197);
    static final int COLOR_SLEET_15 = Color.argb(255, 241, 171, 205);
    static final int COLOR_SLEET_10 = Color.argb(255, 248, 185, 214);
    static final int COLOR_SLEET_5 = Color.argb(255, 254, 199, 222);
    static final int COLOR_SLEET_0 = Color.argb(255, 255, 213, 230);

    static final int COLOR_SNOW_85 = Color.argb(255, 0, 0, 80);
    static final int COLOR_SNOW_80 = Color.argb(255, 1, 1, 75);
    static final int COLOR_SNOW_75 = Color.argb(255, 2, 0, 86);
    static final int COLOR_SNOW_70 = Color.argb(255, 7, 18, 98);
    static final int COLOR_SNOW_65 = Color.argb(255, 13, 35, 111);
    static final int COLOR_SNOW_60 = Color.argb(255, 20, 52, 125);
    static final int COLOR_SNOW_55 = Color.argb(255, 26, 71, 138);
    static final int COLOR_SNOW_50 = Color.argb(255, 30, 88, 152);
    static final int COLOR_SNOW_45 = Color.argb(255, 37, 106, 165);
    static final int COLOR_SNOW_40 = Color.argb(255, 43, 122, 178);
    static final int COLOR_SNOW_35 = Color.argb(255, 49, 141, 192);
    static final int COLOR_SNOW_30 = Color.argb(255, 55, 158, 203);
    static final int COLOR_SNOW_25 = Color.argb(255, 55, 174, 214);
    static final int COLOR_SNOW_20 = Color.argb(255, 55, 190, 220);
    static final int COLOR_SNOW_15 = Color.argb(255, 56, 206, 230);
    static final int COLOR_SNOW_10 = Color.argb(255, 57, 222, 238);
    static final int COLOR_SNOW_5 = Color.argb(255, 57, 238, 247);
    static final int COLOR_SNOW_0 = Color.argb(255, 58, 254, 255);

    static int getPrecipitationColor(Weather.PrecipitationType type, float intensity) {
        return DBZ."COLOR_${type}_${ (17..0).find { intensity >= DBZ."INTENSITY_${it * 5}" }  * 5}"
    }
}