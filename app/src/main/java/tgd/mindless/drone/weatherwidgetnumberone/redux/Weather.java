package tgd.mindless.drone.weatherwidgetnumberone.redux;

class Weather {
    float latitude;
    float longitude;
    String timezone;
    int offset;
    DataPoint currently;
    DataBlock hourly;
    DataBlock daily;

    Weather() {
    }

    class DataBlock {
        DataPoint[] data;

        DataBlock() {
        }
    }

    class DataPoint {
        long time;
        float cloudCover;
        float precipIntensity;
        float precipIntensityMax;
        long precipIntensityMaxTime;
        float precipProbability;
        long sunriseTime;
        long sunsetTime;
        float temperature;
        float temperatureMax;
        long temperatureMaxTime;
        float temperatureMin;
        long temperatureMinTime;

        DataPoint() {
        }
    }
}

