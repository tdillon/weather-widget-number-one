package tgd.mindless.drone.weatherwidgetnumberone.redux.widget;

import java.util.Calendar;


public class TimeSegment {
    long from;  //unix epoch begin of timesegment
    long to;  //unix epoch end of timesegment

    Weather.DataPoint data;
    String timeBarDisplay;
    Box timeBarBox;
    Box graphBox;
    Ranges ranges;

    float unitsPerSecond;
    float unitsPerDegree;


    TimeSegment(
            ThemesClass theme,
            Weather.DataPoint data,
            Box graphBox,
            Box timeBarBox,
            Ranges ranges) {
        this.timeBarBox = timeBarBox;
        this.ranges = ranges;
        this.graphBox = graphBox;
        this.data = data;

        from = data.time;

        //SETUP timeBarDisplay
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from * 1000);
        int hour = cal.getTime().getHours();

        if (theme.type == ThemesClass.ThemeType.Hourly) {
            timeBarDisplay = ((hour % 4 == 0) ? (hour == 0 ? new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()] : String.valueOf(cal.getTime().getHours())) : "");
        } else {
            timeBarDisplay = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"}[cal.getTime().getDay()];
        }

    }

    Point getTemperatureMax() {
        return (ranges.temperature != null ? new Point(graphBox.left + (data.temperatureMaxTime - from) * unitsPerSecond, graphBox.top + (ranges.temperature.max - data.temperatureMax) * unitsPerDegree) : null);
    }

    /*get temperatureMax() {
        return (this._ranges.temperature ? {
                x: this.graphBox.left + (this._data.temperatureMaxTime - this.from) * this._unitsPerSecond,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.temperatureMax) * this.r
        } : null);
    }*/

}
/*


        export class TimeSegment {

    from: number;//unix epoch begin of timesegment
    to: number;//unix epoch end of timesegment
    graphDaytimes: Array<Box>;
    graphNighttimes: Array<Box>;
    timeBarDaytimes: Array<Box>;
    timeBarNighttimes: Array<Box>;
    cloudCover: number;
    //TODO other weather properties
    timeBarDisplay: string;
    windBearing: number;
    moonPhase: number;

    private _unitsPerDegree: number;
    private _unitsPerSecond: number;
*/
/**
 * http://radar.weather.gov/Legend/N0R/DMX_N0R_Legend_0.gif
 * https://en.wikipedia.org/wiki/DBZ_(meteorology)
 *//*
    private _dbzs = [
    { intensity: 69.9, color: Color.getColor({ r: 253, g: 253, b: 253 }) },
    { intensity: 34, color: Color.getColor({ r: 152, g: 84, b: 198 }) },
    { intensity: 16.6, color: Color.getColor({ r: 248, g: 0, b: 253 }) },
    { intensity: 8, color: Color.getColor({ r: 188, g: 0, b: 0 }) },
    { intensity: 4, color: Color.getColor({ r: 212, g: 0, b: 0 }) },
    { intensity: 1.9, color: Color.getColor({ r: 253, g: 0, b: 0 }) },
    { intensity: .92, color: Color.getColor({ r: 253, g: 149, b: 0 }) },
    { intensity: .45, color: Color.getColor({ r: 229, g: 188, b: 0 }) },
    { intensity: .22, color: Color.getColor({ r: 253, g: 248, b: 2 }) },
    { intensity: .1, color: Color.getColor({ r: 0, g: 142, b: 0 }) },
    { intensity: .05, color: Color.getColor({ r: 1, g: 197, b: 1 }) },
    { intensity: .02, color: Color.getColor({ r: 2, g: 253, b: 2 }) },
    { intensity: .01, color: Color.getColor({ r: 3, g: 0, b: 244 }) },
    { intensity: .006, color: Color.getColor({ r: 1, g: 159, b: 244 }) },
    { intensity: .003, color: Color.getColor({ r: 4, g: 233, b: 231 }) },
    { intensity: 0, color: Color.getColor({ r: 4, g: 233, b: 231 }) }  //TODO I don't know what to do for this color/intensity.
    ];

    constructor(
            private _theme: Theme,
            private _data: DataPoint,
            public graphBox: Box,
            public timeBarBox: Box,
            private _ranges: Ranges) {

        let secondsPerSegment = (_theme.widgetType === WidgetType.Daily ? 86400 : 3600);

        this._unitsPerDegree = this.graphBox.height / (this._ranges.temperature ? this._ranges.temperature.max - this._ranges.temperature.min : 1);
        this._unitsPerSecond = this.graphBox.width / secondsPerSegment;

        this.from = _data.time;
        this.to = this.from + secondsPerSegment;
        this.cloudCover = _data.cloudCover;
        this.windBearing = _data.windBearing;
        this.moonPhase = _data.moonPhase;

        //SETUP timeBarDisplay
        let day = new Date(this.from * 1000);
        let hour = day.getHours();

        if (_theme.widgetType === WidgetType.Hourly) {
            this.timeBarDisplay = ((hour % 4 === 0) ? (hour === 0 ? ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'][day.getDay()] : hour.toString()) : '');
        } else {
            this.timeBarDisplay = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'][day.getDay()];
        }

        let widthPerSecond = this.graphBox.width / secondsPerSegment;

        this.graphDaytimes = [];
        this.graphNighttimes = [];
        this.timeBarDaytimes = [];
        this.timeBarNighttimes = [];


        if (_data.sunsetTime < this.from || _data.sunriseTime > this.to) {  //sunset before segment, or sunrise after segment
            this.timeBarNighttimes.push(new Box({ left: timeBarBox.left, top: timeBarBox.top, width: timeBarBox.width, height: timeBarBox.height }));
            this.graphNighttimes.push(new Box({ left: graphBox.left, top: graphBox.top, width: graphBox.width, height: graphBox.height }));
        } else if (_data.sunriseTime < this.from) {  //sunrise before segment, sunset during or after segment
            this.timeBarDaytimes.push(new Box({ left: timeBarBox.left, top: timeBarBox.top, width: (Math.min(_data.sunsetTime, this.to) - this.from) * widthPerSecond, height: timeBarBox.height }));
            this.graphDaytimes.push(new Box({ left: graphBox.left, top: graphBox.top, width: (Math.min(_data.sunsetTime, this.to) - this.from) * widthPerSecond, height: graphBox.height }));
            if (_data.sunsetTime < this.to) {  //sunset during segment
                this.timeBarNighttimes.push(new Box({ left: timeBarBox.left + (_data.sunsetTime - this.from) * widthPerSecond, top: timeBarBox.top, width: (this.to - _data.sunsetTime) * widthPerSecond, height: timeBarBox.height }));
                this.graphNighttimes.push(new Box({ left: graphBox.left + (_data.sunsetTime - this.from) * widthPerSecond, top: graphBox.top, width: (this.to - _data.sunsetTime) * widthPerSecond, height: graphBox.height }));
            }
        } else {  //sunrise during segment, sunset during or after segment
            this.timeBarNighttimes.push(new Box({ left: timeBarBox.left, top: timeBarBox.top, width: (_data.sunriseTime - this.from) * widthPerSecond, height: timeBarBox.height }));
            this.graphNighttimes.push(new Box({ left: graphBox.left, top: graphBox.top, width: (_data.sunriseTime - this.from) * widthPerSecond, height: graphBox.height }));
            this.timeBarDaytimes.push(new Box({ left: timeBarBox.left + (_data.sunriseTime - this.from) * widthPerSecond, top: timeBarBox.top, width: (Math.min(_data.sunsetTime, this.to) - _data.sunriseTime) * widthPerSecond, height: timeBarBox.height }));
            this.graphDaytimes.push(new Box({ left: graphBox.left + (_data.sunriseTime - this.from) * widthPerSecond, top: graphBox.top, width: (Math.min(_data.sunsetTime, this.to) - _data.sunriseTime) * widthPerSecond, height: graphBox.height }));
            if (_data.sunsetTime < this.to) {  //sunset during segment
                this.timeBarNighttimes.push(new Box({ left: timeBarBox.left + (_data.sunsetTime - this.from) * widthPerSecond, top: timeBarBox.top, width: (this.to - _data.sunsetTime) * widthPerSecond, height: timeBarBox.height }));
                this.graphNighttimes.push(new Box({ left: graphBox.left + (_data.sunsetTime - this.from) * widthPerSecond, top: graphBox.top, width: (this.to - _data.sunsetTime) * widthPerSecond, height: graphBox.height }));
            }
        }
    }



    get precipIntensityMaxColor(): Color {
        let c = this._dbzs.find(i => i.intensity < this._data.precipIntensityMax).color;
        c.a = this._theme.options.find(o => o.title === 'precipProbability').segment.opacity || 1;
        return c;
    }

    get precipIntensityColor(): Color {
        let c = this._dbzs.find(i => i.intensity < this._data.precipIntensity).color;
        c.a = this._theme.options.find(o => o.title === 'precipProbability').segment.opacity || 1;
        return c;
    }



    get windSpeed(): Point {
        return (this._ranges.windSpeed ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.windSpeed.max - this._data.windSpeed) * (this.graphBox.height / this._ranges.windSpeed.max)
        } : null);
    }


    get moon() {
        return {
                x: this.graphBox.center.x,
                y: this.graphBox.top + Math.abs(.5 - this.moonPhase) * this.graphBox.height * 2
        };
    }

    get apparentTemperatureMax() {
        return (this._ranges.temperature ? {
                x: this.graphBox.left + (this._data.apparentTemperatureMaxTime - this.from) * this._unitsPerSecond,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.apparentTemperatureMax) * this._unitsPerDegree
        } : null);
    }

    get apparentTemperatureMin() {
        return (this._ranges.temperature ? {
                x: this.graphBox.left + (this._data.apparentTemperatureMinTime - this.from) * this._unitsPerSecond,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.apparentTemperatureMin) * this._unitsPerDegree
        } : null);
    }

    get temperatureMax() {
        return (this._ranges.temperature ? {
                x: this.graphBox.left + (this._data.temperatureMaxTime - this.from) * this._unitsPerSecond,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.temperatureMax) * this._unitsPerDegree
        } : null);
    }

    get temperatureMin() {
        return (this._ranges.temperature ? {
                x: this.graphBox.left + (this._data.temperatureMinTime - this.from) * this._unitsPerSecond,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.temperatureMin) * this._unitsPerDegree
        } : null);
    }


    get humidity() {
        return {
                x: this.graphBox.center.x,
                y: this.graphBox.top + (1 - this._data.humidity) * this.graphBox.height
        };
    }

    get visibility() {
        return {
                x: this.graphBox.center.x,
                y: this.graphBox.top + (10 - this._data.visibility) * this.graphBox.height * .1
        };
    }

    get dewPoint() {
        return (this._ranges.temperature ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.dewPoint) * this._unitsPerDegree
        } : null);
    }

    get ozone() {
        return (this._ranges.ozone ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.ozone.max - this._data.ozone) * (this.graphBox.height / (this._ranges.ozone.max - this._ranges.ozone.min))
        } : null);
    }

    get pressure() {
        return (this._ranges.pressure ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.pressure.max - this._data.pressure) * (this.graphBox.height / (this._ranges.pressure.max - this._ranges.pressure.min))
        } : null);
    }

    get apparentTemperature() {
        return (this._ranges.temperature ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.apparentTemperature) * this._unitsPerDegree
        } : null);
    }

    get temperature() {
        return (this._ranges.temperature ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.temperature.max - this._data.temperature) * this._unitsPerDegree
        } : null);
    }

    get precipProbability() {
        return {
                x: (this._data.precipIntensityMaxTime ?
                this.graphBox.left + (this._data.precipIntensityMaxTime - this.from) * this._unitsPerSecond :
                this.graphBox.center.x),
                y: this.graphBox.top + (1 - this._data.precipProbability) * this.graphBox.height
        };
    }

    get precipAccumulation() {
        return (this._ranges.precipAccumulation ? {
                x: this.graphBox.center.x,
        y: this.graphBox.top + (this._ranges.precipAccumulation.max - this._data.precipAccumulation) * (this.graphBox.height / (this._ranges.precipAccumulation.max - this._ranges.precipAccumulation.min))
        } : null);
    }


}
*/