package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Color;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DrawerTest {

    @Test
    public void drawer_getPrecipitationColor_ReturnsDBZColor() {
        final float small = .0001f;

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_75_INTENSITY), is(Drawer.DBZ_75_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_75_INTENSITY + small), is(Drawer.DBZ_75_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_75_INTENSITY - small), is(Drawer.DBZ_70_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_70_INTENSITY), is(Drawer.DBZ_70_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_70_INTENSITY + small), is(Drawer.DBZ_70_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_70_INTENSITY - small), is(Drawer.DBZ_65_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_65_INTENSITY), is(Drawer.DBZ_65_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_65_INTENSITY + small), is(Drawer.DBZ_65_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_65_INTENSITY - small), is(Drawer.DBZ_60_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_60_INTENSITY), is(Drawer.DBZ_60_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_60_INTENSITY + small), is(Drawer.DBZ_60_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_60_INTENSITY - small), is(Drawer.DBZ_55_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_55_INTENSITY), is(Drawer.DBZ_55_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_55_INTENSITY + small), is(Drawer.DBZ_55_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_55_INTENSITY - small), is(Drawer.DBZ_50_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_50_INTENSITY), is(Drawer.DBZ_50_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_50_INTENSITY + small), is(Drawer.DBZ_50_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_50_INTENSITY - small), is(Drawer.DBZ_45_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_45_INTENSITY), is(Drawer.DBZ_45_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_45_INTENSITY + small), is(Drawer.DBZ_45_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_45_INTENSITY - small), is(Drawer.DBZ_40_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_40_INTENSITY), is(Drawer.DBZ_40_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_40_INTENSITY + small), is(Drawer.DBZ_40_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_40_INTENSITY - small), is(Drawer.DBZ_35_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_35_INTENSITY), is(Drawer.DBZ_35_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_35_INTENSITY + small), is(Drawer.DBZ_35_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_35_INTENSITY - small), is(Drawer.DBZ_30_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_30_INTENSITY), is(Drawer.DBZ_30_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_30_INTENSITY + small), is(Drawer.DBZ_30_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_30_INTENSITY - small), is(Drawer.DBZ_25_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_25_INTENSITY), is(Drawer.DBZ_25_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_25_INTENSITY + small), is(Drawer.DBZ_25_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_25_INTENSITY - small), is(Drawer.DBZ_20_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_20_INTENSITY), is(Drawer.DBZ_20_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_20_INTENSITY + small), is(Drawer.DBZ_20_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_20_INTENSITY - small), is(Drawer.DBZ_15_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_15_INTENSITY), is(Drawer.DBZ_15_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_15_INTENSITY + small), is(Drawer.DBZ_15_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_15_INTENSITY - small), is(Drawer.DBZ_10_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_10_INTENSITY), is(Drawer.DBZ_10_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_10_INTENSITY + small), is(Drawer.DBZ_10_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_10_INTENSITY - small), is(Drawer.DBZ_5_COLOR));

        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_5_INTENSITY), is(Drawer.DBZ_5_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_5_INTENSITY + small), is(Drawer.DBZ_5_COLOR));
        assertThat(Drawer.getPrecipitationColor(Drawer.DBZ_5_INTENSITY - small), is(Color.TRANSPARENT));

        assertThat(Drawer.getPrecipitationColor(0), is(Color.TRANSPARENT));
    }
}
