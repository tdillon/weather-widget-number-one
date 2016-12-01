package tgd.mindless.drone.weatherwidgetnumberone.redux;

import java.util.HashMap;
import java.util.Map;

class Ranges {

    class Range {
        float max;
        float min;

        Range(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }

    Range temperature;
    Range ozone;
    Range pressure;
    Range windSpeed;
    Range precipAccumulation;

    Ranges(Weather.DataBlock db, ThemesClass theme) {

        Map<String, Range> pertinentOptions = new HashMap<>();
        pertinentOptions.put("ozone", ozone);
        pertinentOptions.put("windSpeed", windSpeed);
        pertinentOptions.put("pressure", pressure);
        pertinentOptions.put("precipAccumulation", precipAccumulation);
        pertinentOptions.put("dewPoint", temperature);
        pertinentOptions.put("temperature", temperature);
        pertinentOptions.put("apparentTemperature", temperature);
        pertinentOptions.put("temperatureMax", temperature);
        pertinentOptions.put("temperatureMin", temperature);
        pertinentOptions.put("apparentTemperatureMax", temperature);
        pertinentOptions.put("apparentTemperatureMin", temperature);


        for (ThemesClass.Property property : theme.properties) {
            if (!pertinentOptions.containsKey(property.name)){
                continue;  //e.g., moonPhase
            }
            Range r = pertinentOptions.get(property.name);
            if (r == null) {
                r = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);
            }
            for (Weather.DataPoint dataPoint : db.data) {
                float value = dataPoint.getValue(property.name);
                if (value > r.max) {
                    r.max = value;
                }
                if (value < r.min) {
                    r.min = value;
                }
            }
        }
    }
}