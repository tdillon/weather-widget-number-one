package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class Positionings {
    ThemesClass theme;
    Box widget;
    Box graph;
    Box timeBar;
    Box leftScale;
    Box rightScale;
    Box padding;
    List<TimeSegment> timeSegments;
    private Ranges ranges;
    List<Scale> scales;
    private float timeBarTextHeight;
    private Paint paint;

    //private _theme: Theme, private _data: ForecastIO, clientWidth: number, widgetRatio: number, devicePixelRatio: number, private getTextWidth: (text: string) => number
    //Theme, Data, clientWidth, Ratio, DevicePixelRatio, GetTextWidth()
    //Theme, Data, widgetWidth,WidgetHeight, DevicePixelRatio?, GetTextWidth()?
    Positionings(ThemesClass theme, Weather data, float widgetWidth, float widgetHeight) {
        Weather.DataBlock db = (theme.type == ThemesClass.ThemeType.Daily ? data.daily : data.hourly);
        this.theme = theme;
        ranges = new Ranges(db, theme);
        scales = new ArrayList<>();

        widget = new Box(0, widgetWidth, 0, widgetHeight);

        //paint is used in text size calculations
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(widget.getHeight() * theme.fontSize / 100);

        //calculate the actual height of the time bar text, i.e., not "font size", but actual rendered size
        Rect r = new Rect();
        paint.getTextBounds("SuMoTuWeThFrSa0123456789", 0, 24, r);
        timeBarTextHeight = r.height();


        //TODO For now assume that the biggest dot is on the each perimeter of the graph when calculating padding.
        float biggestDot = Integer.MIN_VALUE,tempDotSize;
        for (ThemesClass.Property p: theme.properties) {
            if ((tempDotSize = widget.getHeight() * p.dot.size / 100) > biggestDot) {
                biggestDot = tempDotSize;
            }
        }

        /*
         * Padding is defined as follows:
         * The distance between the bounds of the "graph" and the nearest of the following: 1) edge of widget 2) border of timeBar, leftScale, or rightScale.
         * Padding is needed to allow dots to be drawn on the graph so that they are not clipped by the bounds of the widget.
         * Dots can be drawn on top of the timeBar, leftScale, or rightScale.
         */

        //padding
        // -> left: (MAX_LEFT_DOT_OVERHANG > leftScale.width ? (MAX_LEFT_DOT_OVERHANG - leftScale.width) : 0)
        // -> right: (MAX_RIGHT_DOT_OVERHANG > rightScale.width ? (MAX_RIGHT_DOT_OVERHANG - rightScale.width) : 0)
        // -> top: MAX_TOP_DOT_OVERHANG
        // -> bottom: (MAX_BOTTOM_DOT_OVERHANG > timeBar.height ? (MAX_BOTTOM_DOT_OVERHANG - timeBar.height) : 0)
        //left scale
        // -> left: 0
        // -> right: text width of the items in the scale
        // -> top: graph.top
        // -> bottom: graph.bottom
        //right scale
        // -> left: text width of the items in the scales
        // -> right: 0
        // -> top: graph.top
        // -> bottom: graph.bottom
        //time bar
        // -> left: graph.left
        // -> right: graph.right
        // -> top: widget.height - timeBarTextHeight
        // -> bottom: 0
        //graph
        // -> left: leftScale.right + padding.left
        // -> right: rightScale.left - padding.right
        // -> top: padding.top
        // -> bottom: timeBar.top - padding.bottom
        /*
         * MAX_(TOP|BOTTOM|LEFT|RIGHT)_DOT_OVERHANG
         * for the (minimum|maximum) temperature (or other properties whose dot is at the (bottom|top) of the graph),
         * the MAX_BOTTOM_DOT_OVERHANG will be the amount the dot extends the bounds of the graph minus the timeBar height (for bottom).
         */


        //TODO calc padding as done in demo
        padding = new Box(0, 0, biggestDot, biggestDot > timeBarTextHeight ? biggestDot : 0);

        //TODO get all scales
        getTemperatureScale(); //TODO can getTemperatureScale just create leftScale since it is the only scale in the left? or does that break my scales abstraction?

        padding.setLeft(biggestDot > scales.get(0).box.getWidth() ? biggestDot : 0);  //TODO take into account position of leftmost dot, may not need padding at all.
        //TODO get padding.right once i implement right scales

        //TODO for now assume this box contains all scales that are 'left'
        leftScale = new Box(
                widget.getLeft(),
                scales.get(0).box.getRight(),
                padding.getTop(),
                widget.getBottom() - timeBarTextHeight  //TODO factor in padding TODO bottom: this.widget.height - Math.max(padding.bottom, this._theme.fontSize)
        );

        //TODO for now assume this box contains all scales that are 'right'
        rightScale = new Box(
                widget.getRight() - 50, //TODO factor text width of scales
                widget.getRight(),
                padding.getTop(),
                leftScale.getBottom()
        );

        timeBar = new Box(
                Math.max(leftScale.getWidth(), padding.getLeft()),
                widget.getWidth() - Math.max(padding.getRight(), rightScale.getWidth()),
                widget.getBottom() - timeBarTextHeight,
                widget.getBottom()
                );


        graph = new Box(
                timeBar.getLeft(),
                timeBar.getRight(),
                padding.getTop(),
                widget.getBottom() - Math.max(timeBar.getHeight(), padding.getBottom())
        );


        timeSegments = new ArrayList<>();

        int i = 0;
        float segWidth = graph.getWidth() / db.data.length;

        for (Weather.DataPoint dp : db.data) {

            //TODO add sunrise/sunset to hourly dp
            /*if (_theme.widgetType === WidgetType.Hourly) {  //add sunrise sunset
                //TODO need to get data[x] for the right day
                let currentDay = _data.daily.data.reduce((prev, curr) => curr.time <= dp.time ? curr : prev);
                dp.sunriseTime = currentDay.sunriseTime;
                dp.sunsetTime = currentDay.sunsetTime;
            }*/

            timeSegments.add(new TimeSegment(
                    theme,
                    dp,
                    new Box(graph.getLeft() + i * segWidth,
                            graph.getLeft() + i * segWidth + segWidth,
                            graph.getTop(),
                            graph.getBottom()),
                    new Box(timeBar.getLeft() + i * segWidth,
                            timeBar.getLeft() + i * segWidth + segWidth,
                            timeBar.getTop(),
                            timeBar.getBottom()),
                    ranges));
            ++i;
        }
    }


    private void getTemperatureScale() {
        if (ranges.temperature != null) {
            float pxPerDeg = (widget.getBottom() - Math.max(timeBarTextHeight, padding.getBottom())) / (ranges.temperature.max - ranges.temperature.min);

            List<Integer> scaleTexts = new ArrayList<>();
            int maxTextWidth = Integer.MIN_VALUE;
            Ranges.Range t = ranges.temperature;
            Rect r = new Rect();

            //TODO see if this math is correct java math !== javascript math
            for (int i = (int) Math.ceil(t.min / 5d) * 5; i <= Math.floor(t.max / 5d) * 5; i += 5) {
                scaleTexts.add(i);
                paint.getTextBounds(Integer.toString(i), 0, Integer.toString(i).length(), r);
                if (r.width() > maxTextWidth) {
                    maxTextWidth = r.width();
                }
            }

            Scale x = new Scale(
                    ScaleType.Temperature,
                    ScalePosition.Left,
                    new Box(
                            0,
                            maxTextWidth,
                            padding.getTop(),
                            widget.getHeight() - Math.max(padding.getBottom(), theme.fontSize)
                    ));

            scales.add(x);

            for (Integer i : scaleTexts) {
                x.items.add(new ScaleItem(i.toString(), new Point(x.box.getCenter().x, x.box.getTop() + (ranges.temperature.max - i) * pxPerDeg)));
            }
        }
    }

}
