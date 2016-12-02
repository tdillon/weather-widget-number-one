package tgd.mindless.drone.weatherwidgetnumberone.redux;

class Box {
    private float _left;
    private float _right;
    private float _top;
    private float _bottom;

    //TODO add setters for left, right, top, and bottom
    //TODO optimize by caching height, width, center

    Box() {
        this(0, 0, 0, 0);
    }

    Box(float left, float right, float top, float bottom) {
        _left = left;
        _right = right;
        _top = top;
        _bottom = bottom;
    }

    void setLeft(float left){
        _left = left;
    }

    float getLeft() {
        return _left;
    }

    float getRight() {
        return _right;
    }

    float getTop() {
        return _top;
    }

    float getBottom() {
        return _bottom;
    }

    float getHeight() {
        return _bottom - _top;
    }

    float getWidth() {
        return _right - _left;
    }

    Point getCenter() {
        return new Point(_left + getWidth() / 2, _top + getHeight() / 2);
    }

}