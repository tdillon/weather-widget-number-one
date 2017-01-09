package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Color;
import android.util.Log;

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

    private ThemesClass theme;
    private float unitsPerSecond;
    private float unitsPerDegree;
    private float _cloudCover;
    private Integer _windBearing;


    TimeSegment(ThemesClass theme, Weather.DataPoint data, Box graphBox, Box timeBarBox, Ranges ranges) {

        this.theme = theme;
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
        _windBearing = data.windBearing;
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
                p = ((theme.type == ThemesClass.ThemeType.Daily && ranges.temperature != null) ?
                        new Point(
                                graphBox.getLeft() + (data.temperatureMaxTime - from) * unitsPerSecond,
                                graphBox.getTop() + (ranges.temperature.max - data.temperatureMax) * unitsPerDegree
                        ) : null);
                break;
            case "temperatureMin":
                p = ((theme.type == ThemesClass.ThemeType.Daily && ranges.temperature != null) ?
                        new Point(
                                graphBox.getLeft() + (data.temperatureMinTime - from) * unitsPerSecond,
                                graphBox.getTop() + (ranges.temperature.max - data.temperatureMin) * unitsPerDegree
                        ) : null);
                break;
            case "temperature":
                p = ((theme.type == ThemesClass.ThemeType.Hourly && ranges.temperature != null) ?
                        new Point(
                                graphBox.getCenter().x,
                                graphBox.getTop() + (ranges.temperature.max - data.temperature) * unitsPerDegree
                        ) : null);
                break;
            case "apparentTemperature":
                p = ((theme.type == ThemesClass.ThemeType.Hourly && ranges.temperature != null) ?
                        new Point(
                                graphBox.getCenter().x,
                                graphBox.getTop() + (ranges.temperature.max - data.apparentTemperature) * unitsPerDegree
                        ) : null);
                break;
            case "apparentTemperatureMin":
                p = ((theme.type == ThemesClass.ThemeType.Daily && ranges.temperature != null) ?
                        new Point(
                                graphBox.getLeft() + (data.apparentTemperatureMinTime - from) * unitsPerSecond,
                                graphBox.getTop() + (ranges.temperature.max - data.apparentTemperatureMin) * unitsPerDegree
                        ) : null);
                break;
            case "apparentTemperatureMax":
                p = ((theme.type == ThemesClass.ThemeType.Daily && ranges.temperature != null) ?
                        new Point(
                                graphBox.getLeft() + (data.apparentTemperatureMaxTime - from) * unitsPerSecond,
                                graphBox.getTop() + (ranges.temperature.max - data.apparentTemperatureMax) * unitsPerDegree
                        ) : null);
                break;
            case "windSpeed":
                p = ((ranges.windSpeed != null) ?
                        new Point(
                                graphBox.getCenter().x,
                                graphBox.getTop() + (ranges.windSpeed.max - data.windSpeed) * (graphBox.getHeight() / ranges.windSpeed.max)
                        ) : null);
                break;
            case "visibility":
                p = ((data.visibility != 0) ?
                        new Point(
                            graphBox.getCenter().x,
                            graphBox.getTop() + (10 - data.visibility) * graphBox.getHeight() * .1f
                        ) : null);
                break;
            case "ozone":
                p = ((ranges.ozone != null) ?
                        new Point(
                                graphBox.getCenter().x,
                                graphBox.getTop() + (ranges.ozone.max - data.ozone) * (graphBox.getHeight() / (ranges.ozone.max - ranges.ozone.min))
                        ) : null);
                break;
            case "moonPhase":
                p = new Point(
                    graphBox.getCenter().x,
                    graphBox.getTop() + Math.abs(.5f - data.moonPhase) * graphBox.getHeight() * 2
                );
                break;
            case "humidity":
                p = new Point(
                    graphBox.getCenter().x,
                    graphBox.getTop() + (1 - data.humidity) * graphBox.getHeight()
                );
                break;
            case "precipAccumulation":
                p = ((ranges.precipAccumulation != null && data.precipAccumulation != 0) ?
                    new Point(
                        graphBox.getCenter().x,
                        graphBox.getTop() + (ranges.precipAccumulation.max - data.precipAccumulation) * (graphBox.getHeight() / (ranges.precipAccumulation.max - ranges.precipAccumulation.min))
                    ) : null);
                break;
            case "dewPoint":
                p = ((ranges.temperature != null) ?
                    new Point(
                        graphBox.getCenter().x,
                        graphBox.getTop() + (ranges.temperature.max - data.dewPoint) * unitsPerDegree
                    ) : null);
                break;
            case "pressure":
                p = ((ranges.pressure != null) ?
                    new Point(
                        graphBox.getCenter().x,
                        graphBox.getTop() + (ranges.pressure.max - data.pressure) * (graphBox.getHeight() / (ranges.pressure.max - ranges.pressure.min))
                    ) : null);
                break;
            case "precipProbability":
                p = ((data.precipProbability != 0) ?
                    new Point(  //TODO add precipIntensityMax somehow for daily. and hourly?
                        graphBox.getCenter().x,
                        graphBox.getTop() + (1 - data.precipProbability) * graphBox.getHeight()
                    ) : null );
                break;
            default:
                p = null;
        }

        return p;
    }

    float getCloudCover() {
        return _cloudCover;
    }

    Integer getWindBearing() { return _windBearing; }
}