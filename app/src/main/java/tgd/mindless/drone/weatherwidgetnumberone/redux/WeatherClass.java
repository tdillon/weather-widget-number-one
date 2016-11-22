package tgd.mindless.drone.weatherwidgetnumberone.redux;

public class WeatherClass {
    float latitude;
    float longitude;
    String timezone;
    int offset;
    DataPoint currently;
    public DataBlock hourly;
    public DataBlock daily;

    public WeatherClass() {
    }

    public class DataBlock {
        public DataPoint[] data;

        public DataBlock() {
        }
    }

    public class DataPoint {
        public long time;
        float cloudCover;
        float precipIntensity;
        float precipIntensityMax;
        long precipIntensityMaxTime;
        float precipProbability;
        long sunriseTime;
        long sunsetTime;
        float temperature;
        public float temperatureMax;
        public long temperatureMaxTime;
        float temperatureMin;
        long temperatureMinTime;

        public DataPoint() {
        }
    }
}
