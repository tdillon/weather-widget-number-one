package tgd.mindless.drone.weatherwidgetnumberone.redux

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
class ThemeDAOTest {

    @Test
    void themeDAO_getTheme() {

        def t = new ThemeDAO2();

        Theme[] x = t.getThemes2(getClass().getClassLoader().getResourceAsStream('themes.json'));

        def theme = x[0];

        assert theme != null
        assert theme.name == "Default"
        assert theme.fontSize == 12.3
        assert theme.type == ThemeType.DAILY
        assert theme.cloudCoverage != null
        assert theme.cloudCoverage.day == '#0099CC'
        assert theme.cloudCoverage.night == 'black'
        assert theme.cloudCoverage.location == CloudCoverageLocation.GRAPH

        assert theme.temperatureMin == null

        assert theme.temperatureMax != null
        assert theme['temperatureMax'] != null
        assert theme.temperatureMax == theme['temperatureMax']

        assert theme.properties.length == 1
        assert theme.properties[0].name == 'temperatureMin'
        assert theme.properties[0].dot.type == DotType.RING

        assert theme.getFoo() == 1


        def propNames = ['temperatureMax', 'temperatureMin', 'windSpeed']
        def baz = propNames.findAll{ theme[it] != null }.collect{ theme[it] }
        assert baz.size == 2
        assert baz[0] == theme.temperatureMax
        assert baz[0].dot.size == 50

    }
}