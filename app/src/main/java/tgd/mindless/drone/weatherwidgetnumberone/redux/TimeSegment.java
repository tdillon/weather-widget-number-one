package tgd.mindless.drone.weatherwidgetnumberone.redux;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


class TimeSegment {
    long from;  //unix epoch begin of timesegment
    long to;  //unix epoch end of timesegment

    List<Box> graphDaytimes;
    List<Box> graphNighttimes;
    List<Box> timeBarDaytimes;
    List<Box> timeBarNighttimes;

    Weather.DataPoint data;
    String timeBarDisplay;
    Box timeBarBox;
    Box graphBox;
    Ranges ranges;

    private float unitsPerSecond;
    private float unitsPerDegree;
    private float _cloudCover;


    TimeSegment(ThemesClass theme, Weather.DataPoint data, Box graphBox, Box timeBarBox, Ranges ranges) {

        this.timeBarBox = timeBarBox;
        this.ranges = ranges;
        this.graphBox = graphBox;
        this.data = data;


        int secondsPerSegment = (theme.type == ThemesClass.ThemeType.Daily ? 86400 : 3600);

        unitsPerDegree = this.graphBox.getHeight() / (this.ranges.temperature != null ? this.ranges.temperature.max - this.ranges.temperature.min : 1);
        unitsPerSecond = this.graphBox.getWidth() / secondsPerSegment;

        from = data.time;
        to = from + secondsPerSegment;
        _cloudCover = data.cloudCover;
        //this.windBearing = _data.windBearing;
        //this.moonPhase = _data.moonPhase;


        //SETUP timeBarDisplay
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from * 1000);
        int hour = cal.getTime().getHours();

        if (theme.type == ThemesClass.ThemeType.Hourly) {
            timeBarDisplay = ((hour % 4 == 0) ? (hour == 0 ? new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()] : String.valueOf(cal.getTime().getHours())) : "");
        } else {
            timeBarDisplay = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()];
        }

        float widthPerSecond = this.graphBox.getWidth() / secondsPerSegment;

        graphDaytimes = new ArrayList<>();
        graphNighttimes = new ArrayList<>();
        timeBarDaytimes = new ArrayList<>();
        timeBarNighttimes = new ArrayList<>();

        if (data.sunsetTime < from || data.sunriseTime > to) {  //sunset before segment, or sunrise after segment
            timeBarNighttimes.add(timeBarBox);
            graphNighttimes.add(graphBox);
        } else if (data.sunriseTime < from) {  //sunrise before segment, sunset during or after segment
            timeBarDaytimes.add(new Box(timeBarBox.getLeft(), timeBarBox.getLeft() + (Math.min(data.sunsetTime, to) - from) * widthPerSecond, timeBarBox.getTop(), timeBarBox.getBottom()));
            graphDaytimes.add(new Box(graphBox.getLeft(), graphBox.getLeft() + (Math.min(data.sunsetTime, to) - from) * widthPerSecond, graphBox.getTop(), graphBox.getBottom()));
            if (data.sunsetTime < to) {  //sunset during segment
                timeBarNighttimes.add(new Box(timeBarBox.getLeft() + (data.sunsetTime - from) * widthPerSecond, timeBarBox.getLeft() + (data.sunsetTime - from) * widthPerSecond + (to - data.sunsetTime) * widthPerSecond, timeBarBox.getTop(), timeBarBox.getBottom()));
                graphNighttimes.add(new Box(graphBox.getLeft() + (data.sunsetTime - from) * widthPerSecond, graphBox.getLeft() + (data.sunsetTime - from) * widthPerSecond + (to - data.sunsetTime) * widthPerSecond, graphBox.getTop(), graphBox.getBottom()));
            }
        } else {  //sunrise during segment, sunset during or after segment
            timeBarNighttimes.add(new Box(timeBarBox.getLeft(), timeBarBox.getLeft() + (data.sunriseTime - from) * widthPerSecond, timeBarBox.getTop(), timeBarBox.getBottom()));
            graphNighttimes.add(new Box(graphBox.getLeft(), graphBox.getLeft() + (data.sunriseTime - from) * widthPerSecond, graphBox.getTop(), graphBox.getBottom()));
            timeBarDaytimes.add(new Box(timeBarBox.getLeft() + (data.sunriseTime - from) * widthPerSecond, timeBarBox.getLeft() + (data.sunriseTime - from) * widthPerSecond + (Math.min(data.sunsetTime, to) - data.sunriseTime) * widthPerSecond, timeBarBox.getTop(), timeBarBox.getBottom()));
            graphDaytimes.add(new Box(graphBox.getLeft() + (data.sunriseTime - from) * widthPerSecond, graphBox.getLeft() + (data.sunriseTime - from) * widthPerSecond + (Math.min(data.sunsetTime, to) - data.sunriseTime) * widthPerSecond, graphBox.getTop(), graphBox.getBottom()));
            if (data.sunsetTime < to) {  //sunset during segment
                timeBarNighttimes.add(new Box(timeBarBox.getLeft() + (data.sunsetTime - from) * widthPerSecond, timeBarBox.getLeft() + (data.sunsetTime - from) * widthPerSecond + (to - data.sunsetTime) * widthPerSecond, timeBarBox.getTop(), timeBarBox.getBottom()));
                graphNighttimes.add(new Box(graphBox.getLeft() + (data.sunsetTime - from) * widthPerSecond, graphBox.getLeft() + (data.sunsetTime - from) * widthPerSecond + (to - data.sunsetTime) * widthPerSecond, graphBox.getTop(), graphBox.getBottom()));
            }
        }

    }

    Point getPoint(String property) {
        Point p;

        switch (property) {
            case "temperatureMax":
                p = (ranges.temperature != null ? new Point(graphBox.getLeft() + (data.temperatureMaxTime - from) * unitsPerSecond, graphBox.getTop() + (ranges.temperature.max - data.temperatureMax) * unitsPerDegree) : null);
                break;
            case "temperatureMin":
                p = (ranges.temperature != null ? new Point(graphBox.getLeft() + (data.temperatureMinTime - from) * unitsPerSecond, graphBox.getTop() + (ranges.temperature.max - data.temperatureMin) * unitsPerDegree) : null);
                break;
            default:
                p = null;
        }

        return p;
    }

    float getCloudCover() {
        return _cloudCover;
    }
}