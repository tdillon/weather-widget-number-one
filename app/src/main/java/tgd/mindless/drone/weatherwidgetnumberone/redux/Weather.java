package tgd.mindless.drone.weatherwidgetnumberone.redux;

import com.google.gson.annotations.SerializedName;

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

    enum PrecipitationType {
        @SerializedName("rain")
        RAIN,
        @SerializedName("sleet")
        SLEET,
        @SerializedName("snow")
        SNOW
    }

    class DataPoint {
        long time;
        Float cloudCover;
        Float precipIntensity;
        Float precipIntensityMax;
        long precipIntensityMaxTime;
        Float precipProbability;
        long sunriseTime;
        long sunsetTime;
        Float temperature;
        Float apparentTemperature;
        Float temperatureMax;
        long temperatureMaxTime;
        Float temperatureMin;
        long temperatureMinTime;
        Float apparentTemperatureMin;
        long apparentTemperatureMinTime;
        Float apparentTemperatureMax;
        long apparentTemperatureMaxTime;
        Float windSpeed;
        Integer windBearing;
        Float visibility;
        Float ozone;
        Float moonPhase;
        Float humidity;
        Float precipAccumulation;
        Float dewPoint;
        Float pressure;
        PrecipitationType precipType;

        DataPoint() {
        }

        Float getValue(String propertyName) {
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
                default:
                    return null;
            }
        }
    }
}

