package tgd.mindless.drone.weatherwidgetnumberone.redux.widget;

class Ranges {

    class Range {
        float max;
        float min;
    }

    Range temperature;
    float temperatureMaxTime;

    Ranges(){

    }
}


/*

export interface Range { max: number, min: number }

export class Ranges {
  private _temperature: Range = { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER };
  private _ozone: Range = { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER };
  private _pressure: Range = { min: Number.MAX_SAFE_INTEGER, max: Number.MIN_SAFE_INTEGER };
  private _windSpeed: Range = { min: 0, max: Number.MIN_SAFE_INTEGER };
  private _precipAccumulation: Range = { min: 0, max: Number.MIN_SAFE_INTEGER };

  constructor(db: DataBlock, theme: Theme) {

    let pertinentOptions = [
      { title: 'ozone', range: '_ozone' },
      { title: 'windSpeed', range: '_windSpeed' },
      { title: 'pressure', range: '_pressure' },
      { title: 'precipAccumulation', range: '_precipAccumulation' },
      { title: 'dewPoint', range: '_temperature' },
      { title: 'temperature', range: '_temperature' },
      { title: 'apparentTemperature', range: '_temperature' },
      { title: 'temperatureMax', range: '_temperature' },
      { title: 'temperatureMin', range: '_temperature' },
      { title: 'apparentTemperatureMax', range: '_temperature' },
      { title: 'apparentTemperatureMin', range: '_temperature' },
    ];

    //Remove options that aren't needed.
    pertinentOptions = pertinentOptions.filter(o => theme.options.some(to => to.title === o.title));

    //Update all of the pertient ranges.
    db.data.forEach(d => {
      pertinentOptions.forEach(o => {
        let range = this[o.range];
        if (d[o.title]) {
          let opt = d[o.title];
          if (opt > range.max) {
            range.max = opt;
          }
          if (opt < range.min) {
            range.min = opt;
          }
        }
      });
    });

    //If the option doesn't exist in the theme, set the range object to null.
    ['_temperature', '_ozone', '_pressure', '_windSpeed', '_precipAccumulation'].forEach(r => {
      if (this[r].max === Number.MIN_SAFE_INTEGER) {
        this[r] = null;
      }
    });

    /*
     * Pressure - The pressure scale will be centered (50%) at 1ATM.
     * The scale will increase in .1" increments until the maximum (deviation from 1ATM) is captured.
     * Whole number inches will be displayed and '-' will be displayed for each .1".
     * /
if (this._pressure) {
        const ATM = 1013.25;  //1 ATM === 1013.25 mbar
        const MAX_DEVIATION = Math.max(3, Math.max(Math.abs(this._pressure.max - ATM), Math.abs(this._pressure.min - ATM)));  //Show at least 5 mbar +- 1ATM

        this._pressure.max = ATM + MAX_DEVIATION;
        this._pressure.min = ATM - MAX_DEVIATION;
        }
        }

        get temperature() {
        return this._temperature;
        }

        get ozone() {
        return this._ozone;
        }

        get pressure() {
        return this._pressure
        }

        get windSpeed() {
        return this._windSpeed
        }

        get precipAccumulation() {
        return this._precipAccumulation
        }
        }


 */