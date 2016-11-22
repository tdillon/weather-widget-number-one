package tgd.mindless.drone.weatherwidgetnumberone.redux.widget;

import com.google.gson.annotations.SerializedName;

public class ThemesClass {
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
        GRAPH,
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