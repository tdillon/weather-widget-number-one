package tgd.mindless.drone.weatherwidgetnumberone.redux;

import java.util.ArrayList;
import java.util.List;

class Positionings {

    Box widget;
    Box graph;
    Box timeBar;
    Box leftScale;
    Box rightScale;
    List<TimeSegment> timeSegments;
    private Ranges ranges;

    //private _theme: Theme, private _data: ForecastIO, clientWidth: number, widgetRatio: number, devicePixelRatio: number, private getTextWidth: (text: string) => number
    //Theme, Data, clientWidth, Ratio, DevicePixelRatio, GetTextWidth()
    //Theme, Data, widgetWidth,WidgetHeight, DevicePixelRatio?, GetTextWidth()?
    Positionings(ThemesClass theme, Weather data, float widgetWidth, float widgetHeight) {
        Weather.DataBlock db = (theme.type == ThemesClass.ThemeType.Daily ? data.daily : data.hourly);

        widget = new Box(0, widgetWidth, 0, widgetHeight);

        //TODO calc padding as done in demo
        int topPadding = 0;
        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingBottom = 0;

        //TODO for now assume this box contains all scales that are 'left'
        leftScale = new Box(
                widget.left,
                50, //TODO width: this.scales.filter(s => s.position === ScalePosition.Left).reduce((a, b) => { if (b.box.left < a.min) { a.min = b.box.left; } if (b.box.right > a.max) { a.max = b.box.right; } return a; }, { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER, get diff(): number { return this.max - this.min } }).diff,
                topPadding,  //top: padding.top,
                widget.bottom * (1 - theme.fontSize / 100)  //TODO factor in padding TODO bottom: this.widget.height - Math.max(padding.bottom, this._theme.fontSize)
        );

        //TODO for now assume this box contains all scales that are 'right'
        rightScale = new Box(
                widget.right - 50,
                widget.right,
                topPadding,
                leftScale.bottom
                /*
                width: this.scales.filter(s => s.position === ScalePosition.Right).reduce((a, b) => { if (b.box.left < a.min) { a.min = b.box.left; } if (b.box.right > a.max) { a.max = b.box.right; } return a; }, { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER, get diff(): number { return this.max - this.min } }).diff,
                right: this.widget.width,
                top: this.leftScale.top,
                bottom: this.leftScale.bottom
                */
        );

        this.timeBar = new Box(
                Math.max(leftScale.width, paddingLeft),
                widget.width - Math.max(paddingRight, rightScale.width),
                widget.bottom * (1 - theme.fontSize / 100),
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
                topPadding,
                widget.bottom - Math.max(timeBar.height, paddingBottom)
                /*
                left: this.timeBar.left,
                right: this.timeBar.right,
                top: padding.top,
                bottom: this.widget.height - Math.max(this.timeBar.height, padding.bottom)
                */
        );

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
}
