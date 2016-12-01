package tgd.mindless.drone.weatherwidgetnumberone.redux;

import java.util.ArrayList;
import java.util.List;

class Scale {
    ScaleType type;
    ScalePosition position;
    Box box;
    List<ScaleItem> items;

    Scale(ScaleType scaleType, ScalePosition scalePosition, Box box) {
        this.type = scaleType;
        this.position = scalePosition;
        this.box = box;
        items = new ArrayList<>();
    }
}

class ScaleItem {
    String value;
    Point center;

    ScaleItem(String value, Point center) {
        this.value = value;
        this.center = center;
    }
}

enum ScaleType {
    Temperature, WindSpeed, Percentage, Pressure, Ozone, PrecipAccumulation
}

enum ScalePosition {
    Left, Right
}