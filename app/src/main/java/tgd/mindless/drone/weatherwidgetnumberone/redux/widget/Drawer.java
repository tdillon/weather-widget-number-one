package tgd.mindless.drone.weatherwidgetnumberone.redux.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class Drawer {
    Bitmap _bmp;
    Canvas _cvs;
    Positionings _pos;
    Paint paint;

    public Drawer(Positionings pos) {
        _pos = pos;

        _bmp = Bitmap.createBitmap((int) _pos.widget.width, (int) _pos.widget.height, Bitmap.Config.ARGB_8888);
        _cvs = new Canvas(_bmp);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public Bitmap render() {
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
        Log.i("themes", t.name + "  " + t.properties[0].name
        );

        renderWidgetBackground();       //1
        renderGraphBackground();        //2
        renderTimeBackground();         //3
        renderLeftScaleBackground();    //4
        renderRightScaleBackground();   //5
        //this.renderCloudCover();             //6
        this.renderTimeText();               //7
        //this.renderScales();                 //8
        //for (let c of this.theme.options) {
        //    this.renderWeatherProperty(c);     //9
        //}

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
}
