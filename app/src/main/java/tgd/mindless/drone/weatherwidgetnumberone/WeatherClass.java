package tgd.mindless.drone.weatherwidgetnumberone;

/**
 * Created by Trisha on 7/10/2015.
 */
class WeatherClass {
    Float latitude;
    CurrentClass currently;
    HourlyClass hourly;
    MinutelyClass minutely;
    DailyClass daily;

    public WeatherClass() {
    }

    class CurrentClass {
        float temperature;
        Long time;

        public CurrentClass() {
        }
    }

    class DailyClass {
        DailyClass.DailyDataClass[] data;

        DailyClass() {
        }

        class DailyDataClass {
            Float cloudCover;
            Float precipIntensity;
            Float precipIntensityMax;
            Long precipIntensityMaxTime;
            Float precipProbability;
            Long sunriseTime;
            Long sunsetTime;
            Float temperatureMax;
            Long temperatureMaxTime;
            Float temperatureMin;
            Long temperatureMinTime;
            Long time;

            DailyDataClass() {
            }
        }
    }

    class HourlyClass {
        HourlyClass.HourlyDataClass[] data;

        public HourlyClass() {
        }

        class HourlyDataClass {
            float cloudCover;
            float precipIntensity;
            float precipProbability;
            float temperature;
            long time;

            public HourlyDataClass() {
            }
        }

    }

    class MinutelyClass {
        MinutelyClass.MinutelyDataClass[] data;

        public MinutelyClass() {
        }

        class MinutelyDataClass {
            Long time;

            public MinutelyDataClass() {
            }
        }
    }
}
