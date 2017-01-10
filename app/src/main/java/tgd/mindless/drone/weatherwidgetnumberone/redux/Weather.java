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
        float apparentTemperature;
        float temperatureMax;
        long temperatureMaxTime;
        float temperatureMin;
        long temperatureMinTime;
        float apparentTemperatureMin;
        long apparentTemperatureMinTime;
        float apparentTemperatureMax;
        long apparentTemperatureMaxTime;
        float windSpeed;
        Integer windBearing;
        float visibility;
        float ozone;
        float moonPhase;
        float humidity;
        float precipAccumulation;
        float dewPoint;
        float pressure;

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
                case "apparentTemperature":
                    return apparentTemperature;
                case "apparentTemperatureMin":
                    return apparentTemperatureMin;
                case "apparentTemperatureMax":
                    return apparentTemperatureMax;
                case "windSpeed":
                    return windSpeed;
                case "visibility":
                    return visibility;
                case "ozone":
                    return ozone;
                case "moonPhase":
                    return moonPhase;
                case "humidity":
                    return humidity;
                case "precipAccumulation":
                    return precipAccumulation;
                case "dewPoint":
                    return dewPoint;
                case "pressure":
                    return pressure;
                default:  //TODO throw exception?
                    return -1;
            }
        }
    }
}

