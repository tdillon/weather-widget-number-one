package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

class Drawer {
    private Bitmap _bmp;
    private Canvas _cvs;
    private Positionings _pos;
    private Paint paint;
    private ThemesClass _theme;

    Drawer(ThemesClass theme, Positionings pos) {
        _pos = pos;
        _theme = theme;

        _bmp = Bitmap.createBitmap((int) _pos.widget.getWidth(), (int) _pos.widget.getHeight(), Bitmap.Config.ARGB_8888);
        _cvs = new Canvas(_bmp);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(pos.widget.getHeight() * theme.fontSize / 100);
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

        renderWidgetBackground();       //1
        renderGraphBackground();        //2
        renderTimeBackground();         //3
        renderLeftScaleBackground();    //4
        renderRightScaleBackground();   //5
        renderCloudCover();             //6
        renderTimeText();               //7
        renderScales();                 //8
        for (ThemesClass.Property p : _theme.properties) {  //9
            renderProperty(p);
        }

        return _bmp;
    }

    private void renderWidgetBackground() {
        paint.setColor(Color.TRANSPARENT);  //TODO pull from theme

        _cvs.drawRect(_pos.widget.getLeft(), _pos.widget.getTop(), _pos.widget.getRight(), _pos.widget.getBottom(), paint);
    }


    private void renderGraphBackground() {
        paint.setColor(Color.TRANSPARENT);  //TODO pull from theme

        _cvs.drawRect(_pos.graph.getLeft(), _pos.graph.getTop(), _pos.graph.getRight(), _pos.graph.getBottom(), paint);
    }

    private void renderTimeBackground() {
        paint.setColor(Color.TRANSPARENT);  //TODO pull from theme

        _cvs.drawRect(_pos.timeBar.getLeft(), _pos.timeBar.getTop(), _pos.timeBar.getRight(), _pos.timeBar.getBottom(), paint);
    }

    private void renderLeftScaleBackground() {
        paint.setColor(Color.TRANSPARENT);  //TODO pull from theme

        _cvs.drawRect(_pos.leftScale.getLeft(), _pos.leftScale.getTop(), _pos.leftScale.getRight(), _pos.leftScale.getBottom(), paint);
    }

    private void renderRightScaleBackground() {
        paint.setColor(Color.TRANSPARENT);  //TODO pull from theme

        _cvs.drawRect(_pos.rightScale.getLeft(), _pos.rightScale.getTop(), _pos.rightScale.getRight(), _pos.rightScale.getBottom(), paint);
    }

    private void renderCloudCover() {
        if (_theme.cloudCoverage != null) {
            List<Box> location;
            ThemesClass.CloudCoverageLocation x = _theme.cloudCoverage.location;

            for (TimeSegment ts : _pos.timeSegments) {
                paint.setColor(Color.parseColor(_theme.cloudCoverage.day));
                paint.setAlpha(Math.round(255 * ts.getCloudCover()));  //[0-255]
                location = (x == ThemesClass.CloudCoverageLocation.TIME_BAR ? ts.timeBarDaytimes : ts.graphDaytimes);
                for (Box b : location) {
                    _cvs.drawRect(b.getLeft(), b.getTop(), b.getRight(), b.getBottom(), paint);
                }

                paint.setColor(Color.parseColor(_theme.cloudCoverage.night));
                paint.setAlpha(Math.round(255 * ts.getCloudCover()));  //[0-255]
                location = (x == ThemesClass.CloudCoverageLocation.TIME_BAR ? ts.timeBarNighttimes : ts.graphNighttimes);
                for (Box b : location) {
                    _cvs.drawRect(b.getLeft(), b.getTop(), b.getRight(), b.getBottom(), paint);
                }
            }
        }
    }


    private void renderTimeText() {
        paint.setColor(Color.WHITE);  //TODO set colors per theme
        paint.setTextAlign(Paint.Align.CENTER);

        for (TimeSegment ts : _pos.timeSegments) {
            _cvs.drawText(ts.timeBarDisplay, ts.timeBarBox.getCenter().x, ts.timeBarBox.getBottom(), paint);
        }
    }


    private void renderScales() {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect r = new Rect();
        for (Scale s : _pos.scales) {
            for (ScaleItem i : s.items) {
                paint.getTextBounds(i.value, 0, i.value.length(), r);
                _cvs.drawText(i.value, i.center.x, i.center.y + r.height() / 2f, paint);
            }
        }
    }

    private void renderProperty(ThemesClass.Property p) {
        Point point;

        TimeSegment prevSeg = null;
        Point curPoint, prevPoint;
        SegmentGeometry s;
        float dotRadius;

        for (TimeSegment curSeg : _pos.timeSegments) {
            point = curSeg.getPoint(p.name);
            if (point == null) {
                continue;
            }
            paint.setColor(Color.parseColor(p.dot.color));
            dotRadius = p.dot.size / 200 * curSeg.graphBox.getWidth();

            prevPoint = (prevSeg != null ? prevSeg.getPoint(p.name) : null);  //first time segment has no previous
            curPoint = curSeg.getPoint(p.name);

            s = new SegmentGeometry(p.segment, dotRadius, prevPoint == null ? null : prevPoint.x, prevPoint == null ? null : prevPoint.y, curPoint.x, curPoint.y);

            _cvs.drawCircle(point.x, point.y, dotRadius, paint);


            //TODO don't draw segment for precip probability when prevSeg was 0%
            if (prevPoint != null) {//(s.hasSegment()) {
                //TODO precipitation probability gradient
                paint.setColor(Color.parseColor(p.segment.color));
                //this.ctx.arc(s.start.point.x, s.start.point.y, DOT_RADIUS, s.start.from, s.start.to, false);
                //this.ctx.arc(s.end.point.x, s.end.point.y, DOT_RADIUS, s.end.from, s.end.to, false);
                Path path = new Path();
                RectF rectF = new RectF(prevPoint.x - dotRadius - 10, prevPoint.y - dotRadius - 10, prevPoint.x + dotRadius + 10, prevPoint.y + dotRadius + 10);

                path.addArc(rectF, -20, 45);
                path.lineTo(curPoint.x,curPoint.y);
                path.close();
                paint.setColor(Color.parseColor(p.segment.color));
                _cvs.drawPath(path, paint);
            }



            prevSeg = curSeg;
        }

    }
}
