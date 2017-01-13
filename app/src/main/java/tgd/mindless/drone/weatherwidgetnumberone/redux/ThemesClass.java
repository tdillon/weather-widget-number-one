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

    enum DotType {
        @SerializedName("0")
        SOLID,
        @SerializedName("1")
        RING
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
        DotType type;  //optional: default is DotType.SOLID
        /**
         * OPTIONAL
         *
         * Only needed if type == DotType.RING
         *
         * ringSize is the percentage of the dot's radius that is visible.
         * - 100 would give a solid dot
         * - 0 would give a transparent dot
         * - 50 would give a dot where the
         *      outer 50% of the radius was solid while the
         *      inner 50% of the radius is transparent
         *
         * TODO should i give/expect a default value if ommitted?
         */
        Float ringSize;  //optional: only needed if type == DotType.RING

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