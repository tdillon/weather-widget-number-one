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
import java.util.Objects;

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

            switch (p.name) {
                case "precipProbability":
                    paint.setColor(Drawer.getPrecipitationColor(curSeg.getPrecipitationType(), curSeg.data.precipIntensity));
                    break;
                default:
                    paint.setColor(Color.parseColor(p.dot.color));
            }

            dotRadius = p.dot.size / 200 * curSeg.graphBox.getWidth();

            prevPoint = (prevSeg != null ? prevSeg.getPoint(p.name) : null);  //first time segment has no previous
            curPoint = curSeg.getPoint(p.name);

            s = new SegmentGeometry(p.segment, dotRadius, prevPoint == null ? null : prevPoint.x, prevPoint == null ? null : prevPoint.y, curPoint.x, curPoint.y);

            switch (p.name) {
                case "windSpeed":
                    renderWindDot(point, dotRadius, curSeg.getWindBearing());
                    break;
                default:
                    if (p.dot.type == ThemesClass.DotType.RING) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(dotRadius * (p.dot.ringSize == null ? 50 : p.dot.ringSize) / 100f);  //TODO get defaults from somewhere
                        _cvs.drawCircle(point.x, point.y, dotRadius - paint.getStrokeWidth() / 2, paint);
                        paint.setStyle(Paint.Style.FILL);
                    } else {
                        _cvs.drawCircle(point.x, point.y, dotRadius, paint);
                    }
                    break;
            }

            //TODO don't draw segment for precip probability when prevSeg was 0%
            if (p.segment != null && prevPoint != null) {//(s.hasSegment()) {
                //TODO precipitation probability gradient
                paint.setColor(Color.parseColor(p.segment.color));
                Path path = new Path();

                double padding = p.segment.padding * dotRadius * 2 / 100;
                double theta = -Math.atan((prevPoint.y - curPoint.y) / (prevPoint.x - curPoint.x));  //radians
                double thetaDegree = theta * 180 / Math.PI;  //degrees
                float x = (float) (padding * Math.cos(theta));
                float y = (float) (padding * Math.sin(theta));
                float sweepAngle = p.segment.width * 180 / 100;  //degrees
                float startAngle = (float) (-thetaDegree - (sweepAngle / 2));  //degrees

                RectF rectF = new RectF(
                        prevPoint.x - dotRadius + x,
                        prevPoint.y - dotRadius - y,
                        prevPoint.x + dotRadius + x,
                        prevPoint.y + dotRadius - y
                );

                path.addArc(rectF, startAngle, sweepAngle);

                x = -x;
                y = -y;

                rectF = new RectF(
                        curPoint.x - dotRadius + x,
                        curPoint.y - dotRadius - y,
                        curPoint.x + dotRadius + x,
                        curPoint.y + dotRadius - y
                );

                path.arcTo(rectF, startAngle + 180, sweepAngle);
                path.close();
                paint.setColor(Color.parseColor(p.segment.color));
                _cvs.drawPath(path, paint);
            }


            prevSeg = curSeg;
        }

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

    //Use the color palette from weather underground: http://icons-ak.wunderground.com/data/wximagenew/w/wuproductteam/9.jpg
    //https://www.wunderground.com/blog/wuproductteam/radar-palette-improvements
    //Calc the dBZ per: https://en.wikipedia.org/wiki/DBZ_(meteorology)
    //JavaScript to perform calculation:
    //getRainfallInInches = dbz => (Math.pow(Math.pow(10, (dbz / 10)) / 200, 5 / 8) / 25.4).toFixed(3)
    final static float DBZ_85_INTENSITY = 294.797f;
    final static float DBZ_80_INTENSITY = 143.556f;
    final static float DBZ_75_INTENSITY = 69.907f;
    final static float DBZ_70_INTENSITY = 34.043f;
    final static float DBZ_65_INTENSITY = 16.578f;
    final static float DBZ_60_INTENSITY = 8.73f;
    final static float DBZ_55_INTENSITY = 3.931f;
    final static float DBZ_50_INTENSITY = 1.914f;
    final static float DBZ_45_INTENSITY = .932f;
    final static float DBZ_40_INTENSITY = .454f;
    final static float DBZ_35_INTENSITY = .221f;
    final static float DBZ_30_INTENSITY = .108f;
    final static float DBZ_25_INTENSITY = .052f;
    final static float DBZ_20_INTENSITY = .026f;
    final static float DBZ_15_INTENSITY = .012f;
    final static float DBZ_10_INTENSITY = .006f;
    final static float DBZ_5_INTENSITY = .003f;

    final static int DBZ_85_RAIN_COLOR = Color.argb(255, 255, 255, 255);
    final static int DBZ_80_RAIN_COLOR = Color.argb(255, 100, 0, 99);
    final static int DBZ_75_RAIN_COLOR = Color.argb(255, 130, 2, 138);
    final static int DBZ_70_RAIN_COLOR = Color.argb(255, 169, 0, 203);
    final static int DBZ_65_RAIN_COLOR = Color.argb(255, 235, 0, 139);
    final static int DBZ_60_RAIN_COLOR = Color.argb(255, 178, 0, 0);
    final static int DBZ_55_RAIN_COLOR = Color.argb(255, 232, 0, 0);
    final static int DBZ_50_RAIN_COLOR = Color.argb(255, 245, 99, 0);
    final static int DBZ_45_RAIN_COLOR = Color.argb(255, 255, 138, 42);
    final static int DBZ_40_RAIN_COLOR = Color.argb(255, 248, 184, 0);
    final static int DBZ_35_RAIN_COLOR = Color.argb(255, 255, 236, 1);
    final static int DBZ_30_RAIN_COLOR = Color.argb(255, 0, 94, 41);
    final static int DBZ_25_RAIN_COLOR = Color.argb(255, 0, 108, 46);
    final static int DBZ_20_RAIN_COLOR = Color.argb(255, 0, 137, 55);
    final static int DBZ_15_RAIN_COLOR = Color.argb(255, 0, 165, 67);
    final static int DBZ_10_RAIN_COLOR = Color.argb(255, 0, 198, 85);
    final static int DBZ_5_RAIN_COLOR = Color.argb(255, 0, 225, 129);
    final static int DBZ_0_RAIN_COLOR = Color.argb(255, 0, 252, 173);

    final static int DBZ_85_SLEET_COLOR = Color.argb(255, 120, 15, 100);
    final static int DBZ_80_SLEET_COLOR = Color.argb(255, 130, 10, 100);
    final static int DBZ_75_SLEET_COLOR = Color.argb(255, 142, 6, 104);
    final static int DBZ_70_SLEET_COLOR = Color.argb(255, 155, 22, 115);
    final static int DBZ_65_SLEET_COLOR = Color.argb(255, 161, 39, 124);
    final static int DBZ_60_SLEET_COLOR = Color.argb(255, 171, 49, 134);
    final static int DBZ_55_SLEET_COLOR = Color.argb(255, 182, 64, 138);
    final static int DBZ_50_SLEET_COLOR = Color.argb(255, 189, 78, 147);
    final static int DBZ_45_SLEET_COLOR = Color.argb(255, 197, 91, 157);
    final static int DBZ_40_SLEET_COLOR = Color.argb(255, 205, 105, 165);
    final static int DBZ_35_SLEET_COLOR = Color.argb(255, 212, 118, 171);
    final static int DBZ_30_SLEET_COLOR = Color.argb(255, 219, 131, 181);
    final static int DBZ_25_SLEET_COLOR = Color.argb(255, 226, 145, 188);
    final static int DBZ_20_SLEET_COLOR = Color.argb(255, 233, 158, 197);
    final static int DBZ_15_SLEET_COLOR = Color.argb(255, 241, 171, 205);
    final static int DBZ_10_SLEET_COLOR = Color.argb(255, 248, 185, 214);
    final static int DBZ_5_SLEET_COLOR = Color.argb(255, 254, 199, 222);
    final static int DBZ_0_SLEET_COLOR = Color.argb(255, 255, 213, 230);

    final static int DBZ_85_SNOW_COLOR = Color.argb(255, 0, 0, 80);
    final static int DBZ_80_SNOW_COLOR = Color.argb(255, 1, 1, 75);
    final static int DBZ_75_SNOW_COLOR = Color.argb(255, 2, 0, 86);
    final static int DBZ_70_SNOW_COLOR = Color.argb(255, 7, 18, 98);
    final static int DBZ_65_SNOW_COLOR = Color.argb(255, 13, 35, 111);
    final static int DBZ_60_SNOW_COLOR = Color.argb(255, 20, 52, 125);
    final static int DBZ_55_SNOW_COLOR = Color.argb(255, 26, 71, 138);
    final static int DBZ_50_SNOW_COLOR = Color.argb(255, 30, 88, 152);
    final static int DBZ_45_SNOW_COLOR = Color.argb(255, 37, 106, 165);
    final static int DBZ_40_SNOW_COLOR = Color.argb(255, 43, 122, 178);
    final static int DBZ_35_SNOW_COLOR = Color.argb(255, 49, 141, 192);
    final static int DBZ_30_SNOW_COLOR = Color.argb(255, 55, 158, 203);
    final static int DBZ_25_SNOW_COLOR = Color.argb(255, 55, 174, 214);
    final static int DBZ_20_SNOW_COLOR = Color.argb(255, 55, 190, 220);
    final static int DBZ_15_SNOW_COLOR = Color.argb(255, 56, 206, 230);
    final static int DBZ_10_SNOW_COLOR = Color.argb(255, 57, 222, 238);
    final static int DBZ_5_SNOW_COLOR = Color.argb(255, 57, 238, 247);
    final static int DBZ_0_SNOW_COLOR = Color.argb(255, 58, 254, 255);

    static int getPrecipitationColor(Weather.PrecipitationType type, float intensity) {
        if (intensity >= DBZ_85_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_85_RAIN_COLOR;
                case SLEET:
                    return DBZ_85_SLEET_COLOR;
                case SNOW:
                    return DBZ_85_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_80_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_80_RAIN_COLOR;
                case SLEET:
                    return DBZ_80_SLEET_COLOR;
                case SNOW:
                    return DBZ_80_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_75_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_75_RAIN_COLOR;
                case SLEET:
                    return DBZ_75_SLEET_COLOR;
                case SNOW:
                    return DBZ_75_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_70_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_70_RAIN_COLOR;
                case SLEET:
                    return DBZ_70_SLEET_COLOR;
                case SNOW:
                    return DBZ_70_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_65_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_65_RAIN_COLOR;
                case SLEET:
                    return DBZ_65_SLEET_COLOR;
                case SNOW:
                    return DBZ_65_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_60_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_60_RAIN_COLOR;
                case SLEET:
                    return DBZ_60_SLEET_COLOR;
                case SNOW:
                    return DBZ_60_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_55_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_55_RAIN_COLOR;
                case SLEET:
                    return DBZ_55_SLEET_COLOR;
                case SNOW:
                    return DBZ_55_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_50_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_50_RAIN_COLOR;
                case SLEET:
                    return DBZ_50_SLEET_COLOR;
                case SNOW:
                    return DBZ_50_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_45_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_45_RAIN_COLOR;
                case SLEET:
                    return DBZ_45_SLEET_COLOR;
                case SNOW:
                    return DBZ_45_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_40_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_40_RAIN_COLOR;
                case SLEET:
                    return DBZ_40_SLEET_COLOR;
                case SNOW:
                    return DBZ_40_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_35_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_35_RAIN_COLOR;
                case SLEET:
                    return DBZ_35_SLEET_COLOR;
                case SNOW:
                    return DBZ_35_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_30_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_30_RAIN_COLOR;
                case SLEET:
                    return DBZ_30_SLEET_COLOR;
                case SNOW:
                    return DBZ_30_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_25_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_25_RAIN_COLOR;
                case SLEET:
                    return DBZ_25_SLEET_COLOR;
                case SNOW:
                    return DBZ_25_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_20_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_20_RAIN_COLOR;
                case SLEET:
                    return DBZ_20_SLEET_COLOR;
                case SNOW:
                    return DBZ_20_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_15_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_15_RAIN_COLOR;
                case SLEET:
                    return DBZ_15_SLEET_COLOR;
                case SNOW:
                    return DBZ_15_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_10_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_10_RAIN_COLOR;
                case SLEET:
                    return DBZ_10_SLEET_COLOR;
                case SNOW:
                    return DBZ_10_SNOW_COLOR;
            }
        } else if (intensity >= DBZ_5_INTENSITY) {
            switch (type) {
                case RAIN:
                    return DBZ_5_RAIN_COLOR;
                case SLEET:
                    return DBZ_5_SLEET_COLOR;
                case SNOW:
                    return DBZ_5_SNOW_COLOR;
            }
        } else {
            switch (type) {
                case RAIN:
                    return DBZ_0_RAIN_COLOR;
                case SLEET:
                    return DBZ_0_SLEET_COLOR;
                case SNOW:
                    return DBZ_0_SNOW_COLOR;
            }
        }

        return Color.BLACK;
    }
}
