package tgd.mindless.drone.weatherwidgetnumberone.redux;

import java.util.Calendar;


class TimeSegment {
    long from;  //unix epoch begin of timesegment
    long to;  //unix epoch end of timesegment

    Weather.DataPoint data;
    String timeBarDisplay;
    Box timeBarBox;
    Box graphBox;
    Ranges ranges;

    float unitsPerSecond;
    float unitsPerDegree;


    TimeSegment(
            ThemesClass theme,
            Weather.DataPoint data,
            Box graphBox,
            Box timeBarBox,
            Ranges ranges) {
        this.timeBarBox = timeBarBox;
        this.ranges = ranges;
        this.graphBox = graphBox;
        this.data = data;

        from = data.time;

        //SETUP timeBarDisplay
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from * 1000);
        int hour = cal.getTime().getHours();

        if (theme.type == ThemesClass.ThemeType.Hourly) {
            timeBarDisplay = ((hour % 4 == 0) ? (hour == 0 ? new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()] : String.valueOf(cal.getTime().getHours())) : "");
        } else {
            timeBarDisplay = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()];
        }

    }

    Point getTemperatureMax() {
        return (ranges.temperature != null ? new Point(graphBox.left + (data.temperatureMaxTime - from) * unitsPerSecond, graphBox.top + (ranges.temperature.max - data.temperatureMax) * unitsPerDegree) : null);
    }
}