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
        int day;
        int night;
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
        int color;

        DotProperties() {
        }
    }

    class SegmentProperties {
        float width;
        float padding;
        int color;

        SegmentProperties() {
        }
    }
}