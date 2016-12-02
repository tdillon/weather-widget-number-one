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

        float getValue(String propertyName) {
            switch (propertyName) {
                case "cloudCover":
                    return cloudCover;
                case "precipIntensity":
                    return precipIntensity;
                case "precipIntensityMax":
                    return precipIntensityMax;
                case "precipProbability":
                    return precipProbability;
                case "temperature":
                    return temperature;
                case "temperatureMax":
                    return temperatureMax;
                case "temperatureMin":
                    return temperatureMin;
                default:  //TODO throw exception?
                    return -1;
            }
        }
    }
}

