package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.gson.Gson;

class Drawer {
    private Bitmap _bmp;
    private Canvas _cvs;
    private Positionings _pos;
    private Paint paint;
    private ThemesClass _theme;

    Drawer(ThemesClass theme, Positionings pos) {
        _pos = pos;
        _theme = theme;

        _bmp = Bitmap.createBitmap((int) _pos.widget.width, (int) _pos.widget.height, Bitmap.Config.ARGB_8888);
        _cvs = new Canvas(_bmp);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    Bitmap render() {
        /*
         * Render Sequence:
         *  1) Widget background (background color w/opacity of entire widget)
         *  2) Graph overlay (background color w/ opacity of graph region)
         *  3) Time overlay (background color w/ opacity of time bar)
         *  4) Left axis overlay (background color w/ opacity of left axis overlay i.e., temp scale)
         *  5) Right axis overlay (background color w/ opacity of right axis overlay i.e., %, MPH, etc., scales)
         *  6) Cloud cover
         *  7) time text
         *  8) scales
         *  9) draw each weather property (dot&segment) for the entire time period //this will effect layering
         */

        Gson g = new Gson();
        ThemesClass t = g.fromJson("{  \"name\": \"Default\",  \"type\": 1,  \"cloudCoverage\": {    \"day\": -16776961,    \"night\": -16777216,    \"location\": 0  },  \"properties\": [    {      \"name\": \"temperatureMax\",      \"dot\": {        \"color\": -1,        \"size\": 5      },      \"segment\": {        \"color\": -1,        \"width\": 50,        \"padding\": 2      }    }  ]}", ThemesClass.class);
        Log.i("themes", t.name + "  " + t.properties[0].name);

        renderWidgetBackground();       //1
        renderGraphBackground();        //2
        renderTimeBackground();         //3
        renderLeftScaleBackground();    //4
        renderRightScaleBackground();   //5
        //this.renderCloudCover();             //6
        renderTimeText();               //7
        renderScales();                 //8
        for (ThemesClass.Property p : _theme.properties) {  //9
            renderProperty(p);
        }

        return _bmp;
    }

    private void renderWidgetBackground() {
        paint.setColor(Color.RED);  //TODO pull from theme

        _cvs.drawRect(_pos.widget.left, _pos.widget.top, _pos.widget.right, _pos.widget.bottom, paint);
    }


    private void renderGraphBackground() {
        paint.setColor(Color.BLUE);  //TODO pull from theme

        _cvs.drawRect(_pos.graph.left, _pos.graph.top, _pos.graph.right, _pos.graph.bottom, paint);
    }

    private void renderTimeBackground() {
        paint.setColor(Color.GREEN);  //TODO pull from theme

        _cvs.drawRect(_pos.timeBar.left, _pos.timeBar.top, _pos.timeBar.right, _pos.timeBar.bottom, paint);
    }

    private void renderLeftScaleBackground() {
        paint.setColor(Color.YELLOW);  //TODO pull from theme

        _cvs.drawRect(_pos.leftScale.left, _pos.leftScale.top, _pos.leftScale.right, _pos.leftScale.bottom, paint);
    }

    private void renderRightScaleBackground() {
        paint.setColor(Color.GRAY);  //TODO pull from theme

        _cvs.drawRect(_pos.rightScale.left, _pos.rightScale.top, _pos.rightScale.right, _pos.rightScale.bottom, paint);
    }

    //TODO cloud cover render here


    private void renderTimeText() {
        paint.setColor(Color.WHITE);  //TODO set colors per theme
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(_pos.timeBar.height);

        for (TimeSegment ts : _pos.timeSegments) {
            _cvs.drawText(ts.timeBarDisplay, ts.timeBarBox.center.x, ts.timeBarBox.bottom, paint);
        }
    }


    private void renderScales() {
        paint.setColor(Color.WHITE);  //TODO set colors per theme
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(_pos.timeBar.height);

        //textBaseline = 'middle'

        for (Scale s : _pos.scales) {
            for (ScaleItem i : s.items) {
                _cvs.drawText(i.value, i.center.x, i.center.y, paint);
            }
        }
    }

    private void renderProperty(ThemesClass.Property p) {

        for (TimeSegment curSeg : _pos.timeSegments) {
            curSeg.getTemperatureMax();
            paint.setColor(p.dot.color);
            _cvs.drawCircle(curSeg.getTemperatureMax().x, curSeg.getTemperatureMax().y, p.dot.size / 100 * _pos.widget.height, paint);
        }

//        let prevSeg: TimeSegment;
//
//        for (let curSeg of this._pos.timeSegments) {
//
//            if (!(c.title === 'precipProbability' && curSeg.precipProbability.y === curSeg.graphBox.bottom)) {
//                let prevProp = (prevSeg ? prevSeg[c.title] : null);  //first time segmetn has no previous
//                let curProp = curSeg[c.title];
//
//                let s = new SegmentGeometry(c, this.theme.globals, (prevSeg ? prevProp.x : null), (prevSeg ? prevProp.y : null), curProp.x, curProp.y);
//
//                switch (c.title) {
//                    case 'moon':
//                        DotDrawer.moon(this.ctx, curProp.x, curProp.y, (c.dot.radius.global ? this.theme.globals.dot.radius : c.dot.radius.value), (c.dot.color.global ? this.theme.globals.dot.color : c.dot.color.value), curSeg.moonPhase);
//                        break;
//                    case 'windSpeed':
//                        DotDrawer.wind(this.ctx, curProp.x, curProp.y, (c.dot.radius.global ? this.theme.globals.dot.radius : c.dot.radius.value), (c.dot.color.global ? this.theme.globals.dot.color.rgba : c.dot.color.value.rgba), curSeg.windBearing);
//                        break;
//                    default:
//                        DotDrawer.simple(this.ctx, curProp.x, curProp.y, (c.dot.radius.global ? this.theme.globals.dot.radius : c.dot.radius.value), (c.title === 'precipProbability') ? (this.theme.widgetType === WidgetType.Hourly ? curSeg.precipIntensityColor.rgb : curSeg.precipIntensityMaxColor.rgb) : (c.dot.color.global ? this.theme.globals.dot.color.rgba : c.dot.color.value.rgba));
//                }
//
//                //Don't draw segment for precipProbability when prevSeg was 0%.
//                if ((c.segment.show.global ? this.theme.globals.segment.show : c.segment.show.value) && s.hasSegment && !(c.title === 'precipProbability' && prevSeg.precipProbability.y === prevSeg.graphBox.bottom)) {
//                    if (c.title === 'precipProbability') {  //gradient
//                        let gradient = this.ctx.createLinearGradient(prevProp.x, prevProp.y, curProp.x, curProp.y);
//                        gradient.addColorStop(0, this.theme.widgetType === WidgetType.Hourly ? prevSeg.precipIntensityColor.rgba : prevSeg.precipIntensityMaxColor.rgba);
//                        gradient.addColorStop(1, this.theme.widgetType === WidgetType.Hourly ? curSeg.precipIntensityColor.rgba : curSeg.precipIntensityMaxColor.rgba);
//                        this.ctx.fillStyle = gradient;
//                    } else {
//                        this.ctx.fillStyle = (c.segment.color.global ? this.theme.globals.segment.color.rgba : c.segment.color.value.rgba);
//                    }
//                    this.ctx.beginPath();
//                    this.ctx.arc(s.start.point.x, s.start.point.y, (c.dot.radius.global ? this.theme.globals.dot.radius : c.dot.radius.value), s.start.from, s.start.to, false);
//                    this.ctx.arc(s.end.point.x, s.end.point.y, (c.dot.radius.global ? this.theme.globals.dot.radius : c.dot.radius.value), s.end.from, s.end.to, false);
//                    this.ctx.fill();
//                    this.ctx.closePath();
//                }
//            }
//
//            prevSeg = curSeg;
//        }
    }
}
