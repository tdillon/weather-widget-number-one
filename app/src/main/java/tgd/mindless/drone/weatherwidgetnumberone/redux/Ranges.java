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

    Range temperature = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);
    Range ozone = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);
    Range pressure = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);
    Range windSpeed = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);
    Range precipAccumulation = new Range(Integer.MAX_VALUE, Integer.MIN_VALUE);

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
            if (!pertinentOptions.containsKey(property.name)) {
                continue;  //e.g., moonPhase
            }
            Range r = pertinentOptions.get(property.name);

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

        if (temperature.max == Integer.MIN_VALUE) temperature = null;
        if (ozone.max == Integer.MIN_VALUE) ozone = null;
        if (pressure.max == Integer.MIN_VALUE) pressure = null;
        if (windSpeed.max == Integer.MIN_VALUE) windSpeed = null;
        if (precipAccumulation.max == Integer.MIN_VALUE) precipAccumulation = null;

        //The precipitation accumulation scale will show a minimum of 1".
        if (precipAccumulation != null && precipAccumulation.max < 1) {
            precipAccumulation.max = 1;
        }

        /*
         * Pressure - The pressure scale will be centered (50%) at 1ATM.
         * The scale will increase in .1" increments until the maximum deviation from 1ATM is captured.
         * Whole number inches will be displayed and '-' will be displayed for each .1".
         */
        if (pressure != null) {
            final float ATM = 1013.25f;  //1 ATM === 1013.25 mbar
            //TODO revisit the MAX_DEVIATION calculation, specifically the Math.max(3, and comment 'at least 5 mbar'
            final float MAX_DEVIATION = Math.max(3, Math.max(Math.abs(pressure.max - ATM), Math.abs(pressure.min - ATM)));  //Show at least 5 mbar +- 1ATM

            pressure.max = ATM + MAX_DEVIATION;
            pressure.min = ATM - MAX_DEVIATION;
        }
    }
}