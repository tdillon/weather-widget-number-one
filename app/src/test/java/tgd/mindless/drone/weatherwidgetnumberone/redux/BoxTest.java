package tgd.mindless.drone.weatherwidgetnumberone.redux;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BoxTest {

    @Test
    public void box_DefaultConstructor_SetsPropsToZero() {
        Box b = new Box();

        assertThat(b.getBottom(), is(0f));
        assertThat(b.getTop(), is(0f));
        assertThat(b.getLeft(), is(0f));
        assertThat(b.getRight(), is(0f));
        assertThat(b.getHeight(), is(0f));
        assertThat(b.getWidth(), is(0f));
        assertThat(b.getCenter().x, is(0f));
        assertThat(b.getCenter().y, is(0f));
    }

    @Test
    public void box_ArgConstructor_SetsPropsCorrectly() {
        Box b = new Box(0, 2, 0, 2);

        assertThat(b.getBottom(), is(2f));
        assertThat(b.getTop(), is(0f));
        assertThat(b.getLeft(), is(0f));
        assertThat(b.getRight(), is(2f));
        assertThat(b.getHeight(), is(2f));
        assertThat(b.getWidth(), is(2f));
        assertThat(b.getCenter().x, is(1f));
        assertThat(b.getCenter().y, is(1f));
    }

    @Test
    public void box_Setters_PropsCorrect() {
        Box b = new Box();

        b.setTop(2);
        b.setBottom(3);
        b.setLeft(2);
        b.setRight(3);

        assertThat(b.getBottom(), is(3f));
        assertThat(b.getTop(), is(2f));
        assertThat(b.getLeft(), is(2f));
        assertThat(b.getRight(), is(3f));
        assertThat(b.getHeight(), is(1f));
        assertThat(b.getWidth(), is(1f));
        assertThat(b.getCenter().x, is(2.5f));
        assertThat(b.getCenter().y, is(2.5f));
    }
}
