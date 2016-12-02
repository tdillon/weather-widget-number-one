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

    //private _theme: Theme, private _data: ForecastIO, clientWidth: number, widgetRatio: number, devicePixelRatio: number, private getTextWidth: (text: string) => number
    //Theme, Data, clientWidth, Ratio, DevicePixelRatio, GetTextWidth()
    //Theme, Data, widgetWidth,WidgetHeight, DevicePixelRatio?, GetTextWidth()?
    Positionings(ThemesClass theme, Weather data, float widgetWidth, float widgetHeight) {
        Weather.DataBlock db = (theme.type == ThemesClass.ThemeType.Daily ? data.daily : data.hourly);
        this.theme = theme;
        ranges = new Ranges(db, theme);
        scales = new ArrayList<>();

        widget = new Box(0, widgetWidth, 0, widgetHeight);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(widget.height * theme.fontSize / 100);

        //TODO get timeBar text height
        Rect r = new Rect();
        paint.getTextBounds("SuMoTuWeThFrSa0123456789", 0, 24, r);
        float timeBarTextHeight = r.height();

        //TODO calc padding as done in demo
        padding = new Box(0, 0, 0, 0);

        //TODO for now assume this box contains all scales that are 'left'
        leftScale = new Box(
                widget.left,
                50, //TODO width: this.scales.filter(s => s.position === ScalePosition.Left).reduce((a, b) => { if (b.box.left < a.min) { a.min = b.box.left; } if (b.box.right > a.max) { a.max = b.box.right; } return a; }, { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER, get diff(): number { return this.max - this.min } }).diff,
                padding.top,
                widget.bottom - timeBarTextHeight  //TODO factor in padding TODO bottom: this.widget.height - Math.max(padding.bottom, this._theme.fontSize)
        );

        //TODO for now assume this box contains all scales that are 'right'
        rightScale = new Box(
                widget.right - 50, //TODO factor text width of scales
                widget.right,
                padding.top,
                leftScale.bottom
                /*
                width: this.scales.filter(s => s.position === ScalePosition.Right).reduce((a, b) => { if (b.box.left < a.min) { a.min = b.box.left; } if (b.box.right > a.max) { a.max = b.box.right; } return a; }, { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER, get diff(): number { return this.max - this.min } }).diff,
                right: this.widget.width,
                top: this.leftScale.top,
                bottom: this.leftScale.bottom
                */
        );

        this.timeBar = new Box(
                Math.max(leftScale.width, padding.left),
                widget.width - Math.max(padding.right, rightScale.width),
                widget.bottom - timeBarTextHeight,
                widget.bottom
                /*
                left: Math.max(this.leftScale.width, padding.left),
                right: this.widget.width - Math.max(padding.right, this.rightScale.width),
                top: this.widget.height - this._theme.fontSize,
                bottom: this.widget.height
                */
        );


        graph = new Box(
                timeBar.left,
                timeBar.right,
                padding.top,
                widget.bottom - Math.max(timeBar.height, padding.bottom)
                /*
                left: this.timeBar.left,
                right: this.timeBar.right,
                top: padding.top,
                bottom: this.widget.height - Math.max(this.timeBar.height, padding.bottom)
                */
        );

        //TODO get all scales
        getTemperatureScale();

        timeSegments = new ArrayList<>();

        int i = 0;
        float segWidth = graph.width / db.data.length;

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
                    new Box(graph.left + i * segWidth,
                            graph.left + i * segWidth + segWidth,
                            graph.top,
                            graph.bottom),
                    new Box(timeBar.left + i * segWidth,
                            timeBar.left + i * segWidth + segWidth,
                            timeBar.top,
                            timeBar.bottom),
                    ranges));
            ++i;
        }
    }


    private void getTemperatureScale() {
        if (ranges.temperature != null) {
            float pxPerDeg = graph.height / (ranges.temperature.max - ranges.temperature.min);

            List<Integer> scaleTexts = new ArrayList<>();
            int maxTextWidth = Integer.MIN_VALUE;
            int tempTextWidth;
            Ranges.Range t = ranges.temperature;

            //TODO see if this math is correct java math !== javascript math
            for (int i = (int) Math.ceil(t.min / 5d) * 5; i <= Math.floor(t.max / 5d) * 5; i += 5) {
                scaleTexts.add(i);
                //TODO how do i "getTextWidth"?
                if ((tempTextWidth = 50/*this.getTextWidth(i.toString())*/) > maxTextWidth) {
                    maxTextWidth = tempTextWidth;
                }
            }

            Scale x = new Scale(
                    ScaleType.Temperature,
                    ScalePosition.Left,
                    new Box(
                            0,
                            maxTextWidth,
                            padding.top,
                            widget.height - Math.max(padding.bottom, theme.fontSize)
                    ));

            scales.add(x);

            for (Integer i : scaleTexts) {
                x.items.add(new ScaleItem(i.toString(), new Point(x.box.center.x, x.box.top + (ranges.temperature.max - i) * pxPerDeg)));
            }
        }
    }

}
