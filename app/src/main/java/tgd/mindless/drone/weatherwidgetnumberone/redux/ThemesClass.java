package tgd.mindless.drone.weatherwidgetnumberone.redux;

import com.google.gson.annotations.SerializedName;

class ThemesClass {
    String name;
    ThemeType type;
    float fontSize;
    CloudCoverage cloudCoverage;
    Property[] properties;

    ThemesClass() {
    }


    class CloudCoverage {
        String day;
        String night;
        CloudCoverageLocation location;

        CloudCoverage() {
        }
    }

    class Property {
        String name;
        DotProperties dot;
        SegmentProperties segment;

        Property() {
        }
    }

    enum CloudCoverageLocation {
        @SerializedName("0")
        GRAPH,
        @SerializedName("1")
        TIME_BAR
    }


    enum ThemeType {
        @SerializedName("0")
        Hourly,
        @SerializedName("1")
        Daily
    }

    class DotProperties {
        float size;
        String color;

        DotProperties() {
        }
    }

    class SegmentProperties {
        float width;
        float padding;
        String color;

        SegmentProperties() {
        }
    }
}