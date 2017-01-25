package tgd.mindless.drone.weatherwidgetnumberone.redux

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
class DBZTest {

    @Test
    void dbz_getPrecipitationColor() {
        float small = 0.0001

        def types = ["RAIN", 'SLEET', "SNOW"]

        (1..17).each { i ->
            types.each {
                assert DBZ.getPrecipitationColor(Weather.PrecipitationType."${it}", DBZ."INTENSITY_${i * 5}") == DBZ."COLOR_${it}_${i * 5}"
                assert DBZ.getPrecipitationColor(Weather.PrecipitationType."${it}", (float) (DBZ."INTENSITY_${i * 5}" + small)) == DBZ."COLOR_${it}_${i * 5}"
                assert DBZ.getPrecipitationColor(Weather.PrecipitationType."${it}", (float) (DBZ."INTENSITY_${i * 5}" - small)) == DBZ."COLOR_${it}_${(i - 1) * 5}"
            }
        }
    }
}