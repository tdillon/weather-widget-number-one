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
            long precipIntensityMaxTime;
            float precipProbability;
            Long sunriseTime;
            Long sunsetTime;
            float temperatureMax;
            long temperatureMaxTime;
            float temperatureMin;
            long temperatureMinTime;
            long time;

            public DailyDataClass() {
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
