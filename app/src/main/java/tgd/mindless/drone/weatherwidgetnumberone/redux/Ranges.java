package tgd.mindless.drone.weatherwidgetnumberone.redux;

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

    Ranges(Weather.DataBlock db, ThemesClass theme) {

        for (Weather.DataPoint dp : db.data) {
            if (dp.temperatureMax > temperature.max) {
                temperature.max = dp.temperatureMax;
            }
            if (dp.temperatureMax < temperature.min) {
                temperature.min = dp.temperatureMax;
            }
        }


    }
}