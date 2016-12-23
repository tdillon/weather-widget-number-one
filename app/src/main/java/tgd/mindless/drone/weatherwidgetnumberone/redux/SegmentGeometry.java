package tgd.mindless.drone.weatherwidgetnumberone.redux;



class SegmentGeometry {

    class EndPoint{
        Point point;
        float from;
        float to;

        EndPoint(Point p, float f, float t) {
            point = p;
            from = f;
            to = t;
        }
    }

    int _length = 1;
    EndPoint _start,_end;

    SegmentGeometry(ThemesClass.SegmentProperties segmentProperties, float dotRadius, Float x1,Float y1, float x2, float y2) {

    }

    boolean hasSegment() {
        return _length > 0;
    }

    EndPoint start() {
        return _start;
    }

    EndPoint end() {
        return this._end;
    }
}

/*
import {EndPoint} from "./EndPoint.interface";
import {ConfigOption, GlobalOptions} from "../Option.interface"

export class SegmentGeometry {
  private _length:number = 0;
  private _start:EndPoint;
  private _end:EndPoint;

  constructor(private config:ConfigOption, private globals:GlobalOptions, private x1:number, private y1:number, private x2:number, private y2:number) {

    if (x1 !== null && y1 !== null && x2 !== null && y2 !== null) {
      let angle = (config.segment.angle.global ? globals.segment.angle : config.segment.angle.value);
      let padding = (config.segment.padding.global ? globals.segment.padding : config.segment.padding.value);
      let radius = (config.dot.radius.global ? globals.dot.radius : config.dot.radius.value);
      let prettyAngle = Math.atan((y1 - y2) / (x1 - x2));
      let prettyAngleDegree = prettyAngle * 180 / Math.PI;
      let prettyShift = (prettyAngle < 0 ? 1 : -1);  //the X/Y values shift depending upon the slope
      prettyAngle = Math.abs(prettyAngle);
      let startAngle = (prettyAngleDegree - angle) * Math.PI / 180;
      let endAngle = startAngle + 2 * (angle * Math.PI / 180);
      let p2StartAngle = Math.PI + startAngle;
      let p2EndAngle = Math.PI + endAngle;
      let prettySegmentBeginX = x1 + padding * Math.cos(prettyAngle);
      let prettySegmentBeginY = y1 - prettyShift * padding * Math.sin(prettyAngle);
      let prettySegmentEndX = x2 - padding * Math.cos(prettyAngle);
      let prettySegmentEndY = y2 + prettyShift * padding * Math.sin(prettyAngle);
      this._length = Math.sqrt(Math.pow(prettySegmentEndY - prettySegmentBeginY, 2) + Math.pow(prettySegmentEndX - prettySegmentBeginX, 2)) - 2 * radius;
      this._start = {point: {x: prettySegmentBeginX, y: prettySegmentBeginY}, from: startAngle, to: endAngle};
      this._end = {point: {x: prettySegmentEndX, y: prettySegmentEndY}, from: p2StartAngle, to: p2EndAngle};
    }
  }

  get hasSegment():boolean {
    return this._length > 0;
  }

  get start() {
    return this._start;
  }

  get end():EndPoint {
    return this._end;
  }
}

 */
