package tgd.mindless.drone.weatherwidgetnumberone.redux;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


public class PointTest {

    @Test
    public void point_Construction_SetsProps() {
        Point p = new Point(0,0);
        assertThat(p.x, is(0f));
    }
}
