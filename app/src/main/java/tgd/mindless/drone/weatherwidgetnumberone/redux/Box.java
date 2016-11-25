package tgd.mindless.drone.weatherwidgetnumberone.redux;

class Box {
    float left;
    float right;
    float top;
    float bottom;
    float width;
    float height;
    Point center;

    Box(float left, float right,  float top, float bottom){
        this.left = left;
        this.right = right;
        width = right - left;
        this.top = top;
        this.bottom = bottom;
        height = bottom - top;

        center = new Point(this.left + width / 2, this.top + height / 2);
    }
}