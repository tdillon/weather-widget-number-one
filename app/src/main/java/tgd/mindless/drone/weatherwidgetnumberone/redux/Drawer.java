package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

class Drawer {
    private Bitmap _bmp;
    private Canvas _cvs;
    private Positionings _pos;
    private Paint paint;
    private ThemesClass _theme;

    Drawer(ThemesClass theme, Positionings pos, int fontSize) {
        _pos = pos;
        _theme = theme;

        _bmp = Bitmap.createBitmap((int) _pos.widget.getWidth(), (int) _pos.widget.getHeight(), Bitmap.Config.ARGB_8888);
        _cvs = new Canvas(_bmp);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(fontSize);
    }

    /**
     * Render Sequence:
     * 1) Widget background (background color w/opacity of entire widget)
     * 2) Graph overlay (background color w/ opacity of graph region)
     * 3) Time overlay (background color w/ opacity of time bar)
     * 4) Left axis overlay (background color w/ opacity of left axis overlay i.e., temp scale)
     * 5) Right axis overlay (background color w/ opacity of right axis overlay i.e., %, MPH, etc., scales)
     * 6) Cloud cover
     * 7) time text
     * 8) scales
     * 9) draw each weather property (dot&segment) for the entire time period //this will effect layering
     */
    Bitmap render() {
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
                paint.setAlpha(Math.round(255 * (1 - ts.getCloudCover())));  //[0-255]
                location = (x == ThemesClass.CloudCoverageLocation.TIME_BAR ? ts.timeBarDaytimes : ts.graphDaytimes);
                for (Box b : location) {
                    _cvs.drawRect(b.getLeft(), b.getTop(), b.getRight(), b.getBottom(), paint);
                }

                paint.setColor(Color.parseColor(_theme.cloudCoverage.night));
                paint.setAlpha(Math.round(255 * (1 - ts.getCloudCover())));  //[0-255]
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

        Rect r = new Rect();
        for (TimeSegment ts : _pos.timeSegments) {
            paint.getTextBounds(ts.timeBarDisplay, 0, ts.timeBarDisplay.length(), r);
            _cvs.drawText(ts.timeBarDisplay, ts.timeBarBox.getCenter().x, ts.timeBarBox.getBottom() - r.bottom, paint);
        }
    }

    private void renderScales() {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect r = new Rect();
        for (Scale s : _pos.scales) {
            for (ScaleItem i : s.items) {
                paint.getTextBounds(i.value, 0, i.value.length(), r);
                _cvs.drawText(i.value, i.center.x, i.center.y - r.exactCenterY(), paint);
            }
        }
    }

    private void renderProperty(ThemesClass.Property p) {
        TimeSegment prevSeg = null;

        for (TimeSegment curSeg : _pos.timeSegments) {
            renderCurrentDot(p, curSeg);
            renderCurrentSegment(p, curSeg, prevSeg);
            prevSeg = curSeg;
        }
    }

    private void renderCurrentDot(ThemesClass.Property p, TimeSegment curSeg) {
        Point point = curSeg.getPoint(p.name);
        if (point == null) {
            return;
        }

        float dotRadius = p.dot.size / 200 * curSeg.graphBox.getWidth();

        if (p.name.equals("precipProbability")) {
            paint.setColor(DBZ.getPrecipitationColor(curSeg.getPrecipitationType(), curSeg.data.precipIntensity));
        } else {
            paint.setColor(Color.parseColor(p.dot.color));
        }

        if (p.name.equals("windSpeed")) {
            renderWindDot(point, dotRadius, curSeg.getWindBearing());
        } else if (p.dot.type == ThemesClass.DotType.RING) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dotRadius * (p.dot.ringSize == null ? 50 : p.dot.ringSize) / 100f);  //TODO get defaults from somewhere
            _cvs.drawCircle(point.x, point.y, dotRadius - paint.getStrokeWidth() / 2, paint);
            paint.setStyle(Paint.Style.FILL);
        } else {  //DotType.SOLID
            _cvs.drawCircle(point.x, point.y, dotRadius, paint);
        }
    }

    private void renderCurrentSegment(ThemesClass.Property p, TimeSegment curSeg, TimeSegment prevSeg) {
        if (p.segment == null || prevSeg == null || curSeg.getPoint(p.name) == null || prevSeg.getPoint(p.name) == null) {
            return;
        }

        Point prevPoint = prevSeg.getPoint(p.name);
        Point curPoint = curSeg.getPoint(p.name);
        float dotRadius = p.dot.size / 200 * curSeg.graphBox.getWidth();
        double padding = p.segment.padding * dotRadius * 2 / 100;

        if (Math.sqrt(Math.pow(prevPoint.x - curPoint.x, 2) + Math.pow(prevPoint.y - curPoint.y, 2)) <= 2 * (dotRadius + padding)) {
            return;
        }

        Path path = new Path();

        double theta = -Math.atan((prevPoint.y - curPoint.y) / (prevPoint.x - curPoint.x));  //radians
        double thetaDegree = theta * 180 / Math.PI;  //degrees
        float x = (float) (padding * Math.cos(theta));
        float y = (float) (padding * Math.sin(theta));
        float sweepAngle = p.segment.width * 180 / 100;  //degrees
        float startAngle = (float) (-thetaDegree - (sweepAngle / 2));  //degrees

        RectF rectF = new RectF(prevPoint.x - dotRadius + x, prevPoint.y - dotRadius - y, prevPoint.x + dotRadius + x, prevPoint.y + dotRadius - y);
        path.addArc(rectF, startAngle, sweepAngle);
        x = -x;
        y = -y;
        rectF = new RectF(curPoint.x - dotRadius + x, curPoint.y - dotRadius - y, curPoint.x + dotRadius + x, curPoint.y + dotRadius - y);
        path.arcTo(rectF, startAngle + 180, sweepAngle);
        path.close();

        if (p.name.equals("precipProbability")) {
            paint.setShader(new LinearGradient(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, DBZ.getPrecipitationColor(prevSeg.getPrecipitationType(), prevSeg.data.precipIntensity), DBZ.getPrecipitationColor(curSeg.getPrecipitationType(), curSeg.data.precipIntensity), Shader.TileMode.CLAMP));
        } else {
            paint.setColor(Color.parseColor(p.segment.color));
        }
        _cvs.drawPath(path, paint);
        paint.setShader(null);
    }

    private void renderWindDot(Point point, float radius, Integer bearing) {
        final float pointAngleDeg = (bearing + 180) % 360;
        final float pointAngle = (float) (pointAngleDeg * Math.PI / 180.0);
        final int tailAngle = 60;
        final float tailPercent = .6f;
        final float secondAngle = (float) (((pointAngleDeg + 90 + tailAngle) * Math.PI / 180.0) % (2.0 * Math.PI));
        final float thirdAngle = (float) (((pointAngleDeg + 270 - tailAngle) * Math.PI / 180.0) % (2.0 * Math.PI));
        Point[] points = new Point[]{
                new Point(point.x + radius * (float) Math.sin(pointAngle), point.y - radius * (float) Math.cos(pointAngle)),
                new Point(point.x + radius * (float) Math.sin(secondAngle), point.y - radius * (float) Math.cos(secondAngle)),
                new Point(point.x + (radius * tailPercent) * (float) Math.sin(bearing * Math.PI / 180.0), point.y - (radius * tailPercent) * (float) Math.cos(bearing * Math.PI / 180.0)),
                new Point(point.x + radius * (float) Math.sin(thirdAngle), point.y - radius * (float) Math.cos(thirdAngle))
        };

        Path path = new Path();

        path.moveTo(points[0].x, points[0].y);

        for (Point p : points) {
            path.lineTo(p.x, p.y);
        }

        path.close();
        _cvs.drawPath(path, paint);
    }
}
