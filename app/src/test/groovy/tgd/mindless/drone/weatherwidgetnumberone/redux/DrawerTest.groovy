package tgd.mindless.drone.weatherwidgetnumberone.redux;

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
class DrawerTest {

    @Test
    void foo() {
        float small = 0.0001

        def types = ["RAIN", 'SLEET', "SNOW"]

        (1..17).each { i ->
            types.each {
                assert Drawer.getPrecipitationColor(Weather.PrecipitationType."${it}", Drawer."DBZ_${i * 5}_INTENSITY") == Drawer."DBZ_${i * 5}_${it}_COLOR"
                assert Drawer.getPrecipitationColor(Weather.PrecipitationType."${it}", (float) (Drawer."DBZ_${i * 5}_INTENSITY" + small)) == Drawer."DBZ_${i * 5}_${it}_COLOR"
                assert Drawer.getPrecipitationColor(Weather.PrecipitationType."${it}", (float) (Drawer."DBZ_${i * 5}_INTENSITY" - small)) == Drawer."DBZ_${(i - 1) * 5}_${it}_COLOR"
            }
        }
    }
}