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
                    paint.setColor(Drawer.getPrecipitationColor(curSeg.data.precipIntensity));
                    break;
                case "precipProbabilityMax":
                    paint.setColor(Drawer.getPrecipitationColor(curSeg.data.precipIntensityMax));
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

    //https://en.wikipedia.org/wiki/DBZ_(meteorology)
    //http://radar.weather.gov/Legend/N0R/DMX_N0R_Legend_0.gif
    final static float DBZ_75_INTENSITY = 96.9f;
    final static int DBZ_75_COLOR = Color.argb(255, 253, 253, 253);
    final static float DBZ_70_INTENSITY = 34f;
    final static int DBZ_70_COLOR = Color.argb(255, 152, 84, 198);
    final static float DBZ_65_INTENSITY = 16.6f;
    final static int DBZ_65_COLOR = Color.argb(255, 248, 0, 253);
    final static float DBZ_60_INTENSITY = 8f;
    final static int DBZ_60_COLOR = Color.argb(255, 188, 0, 0);
    final static float DBZ_55_INTENSITY = 4f;
    final static int DBZ_55_COLOR = Color.argb(255, 212, 0, 0);
    final static float DBZ_50_INTENSITY = 1.9f;
    final static int DBZ_50_COLOR = Color.argb(255, 253, 0, 0);
    final static float DBZ_45_INTENSITY = .92f;
    final static int DBZ_45_COLOR = Color.argb(255, 253, 149, 0);
    final static float DBZ_40_INTENSITY = .45f;
    final static int DBZ_40_COLOR = Color.argb(255, 229, 188, 0);
    final static float DBZ_35_INTENSITY = .22f;
    final static int DBZ_35_COLOR = Color.argb(255, 253, 248, 2);
    final static float DBZ_30_INTENSITY = .1f;
    final static int DBZ_30_COLOR = Color.argb(255, 0, 142, 0);
    final static float DBZ_25_INTENSITY = .05f;
    final static int DBZ_25_COLOR = Color.argb(255, 1, 197, 1);
    final static float DBZ_20_INTENSITY = .02f;
    final static int DBZ_20_COLOR = Color.argb(255, 2, 253, 2);
    final static float DBZ_15_INTENSITY = .01f;
    final static int DBZ_15_COLOR = Color.argb(255, 3, 0, 244);
    final static float DBZ_10_INTENSITY = .006f;
    final static int DBZ_10_COLOR = Color.argb(255, 1, 159, 244);
    final static float DBZ_5_INTENSITY = .003f;
    final static int DBZ_5_COLOR = Color.argb(255, 4, 233, 231);

    static int getPrecipitationColor(float intensity) {
        if (intensity >= DBZ_75_INTENSITY) {
            return DBZ_75_COLOR;
        } else if (intensity >= DBZ_70_INTENSITY) {
            return DBZ_70_COLOR;
        } else if (intensity >= DBZ_65_INTENSITY) {
            return DBZ_65_COLOR;
        } else if (intensity >= DBZ_60_INTENSITY) {
            return DBZ_60_COLOR;
        } else if (intensity >= DBZ_55_INTENSITY) {
            return DBZ_55_COLOR;
        } else if (intensity >= DBZ_50_INTENSITY) {
            return DBZ_50_COLOR;
        } else if (intensity >= DBZ_45_INTENSITY) {
            return DBZ_45_COLOR;
        } else if (intensity >= DBZ_40_INTENSITY) {
            return DBZ_40_COLOR;
        } else if (intensity >= DBZ_35_INTENSITY) {
            return DBZ_35_COLOR;
        } else if (intensity >= DBZ_30_INTENSITY) {
            return DBZ_30_COLOR;
        } else if (intensity >= DBZ_25_INTENSITY) {
            return DBZ_25_COLOR;
        } else if (intensity >= DBZ_20_INTENSITY) {
            return DBZ_20_COLOR;
        } else if (intensity >= DBZ_15_INTENSITY) {
            return DBZ_15_COLOR;
        } else if (intensity >= DBZ_10_INTENSITY) {
            return DBZ_10_COLOR;
        } else if (intensity >= DBZ_5_INTENSITY) {
            return DBZ_5_COLOR;
        } else {
            return Color.TRANSPARENT;
        }
    }
}
