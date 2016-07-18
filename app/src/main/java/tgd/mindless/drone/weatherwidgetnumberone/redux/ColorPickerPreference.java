package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ColorPickerPreference extends DialogPreference {

    String DEFAULT_VALUE = "TODO";
    String mCurrentValue;
    ImageView ivColor;
    SeekBar mSeekBarR, mSeekBarG, mSeekBarB, mSeekBarAlpha;
    final static String TAG = "ColorPickerPreference";

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.v(TAG, "   constructor");

        setDialogLayoutResource(R.layout.colorpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);


        setDialogIcon(null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        DEFAULT_VALUE = a.getString(index);
        Log.v(TAG, "   onGetDefaultValue: " + DEFAULT_VALUE);
        return DEFAULT_VALUE;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.v(TAG, "   onSetInitialValue " + String.valueOf(defaultValue) + "   " + String.valueOf(restorePersistedValue));
        if (restorePersistedValue) {
            mCurrentValue = this.getPersistedString(DEFAULT_VALUE);
        } else {
            mCurrentValue = (String) defaultValue;
            persistString(mCurrentValue);
        }
        Log.v(TAG, "   onSetInitialValue   mCurrentValue: " + mCurrentValue);

    }

    @Override
    protected void onBindDialogView(View view) {
        Log.v(TAG, "   onBindDialogView");
        ivColor = (ImageView) view.findViewById(R.id.ivColor);
        mSeekBarAlpha = (SeekBar) view.findViewById(R.id.alpha);
        mSeekBarR = (SeekBar) view.findViewById(R.id.r);
        mSeekBarG = (SeekBar) view.findViewById(R.id.g);
        mSeekBarB = (SeekBar) view.findViewById(R.id.b);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ivColor.setBackgroundColor(Color.parseColor(getValue()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        mSeekBarAlpha.setOnSeekBarChangeListener(listener);
        mSeekBarR.setOnSeekBarChangeListener(listener);
        mSeekBarG.setOnSeekBarChangeListener(listener);
        mSeekBarB.setOnSeekBarChangeListener(listener);

        setProgress(mCurrentValue);

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.v(TAG, "   onDialogClosed");

        mCurrentValue = getValue();

        if (positiveResult) {
            persistString(mCurrentValue);
        }
    }

    private void setProgress(String value) {
        //e.g., #ff0099cc
        mSeekBarAlpha.setProgress(Integer.decode('#' + value.substring(1, 3)));
        mSeekBarR.setProgress(Integer.decode('#' + value.substring(3, 5)));
        mSeekBarG.setProgress(Integer.decode('#' + value.substring(5, 7)));
        mSeekBarB.setProgress(Integer.decode('#' + value.substring(7, 9)));
    }

    private String getValue() {
        int a = mSeekBarAlpha.getProgress(), r = mSeekBarR.getProgress(), g = mSeekBarG.getProgress(), b = mSeekBarB.getProgress();
        return '#'
                + ((a < 16) ? "0" : "") + Integer.toHexString(a)
                + ((r < 16) ? "0" : "") + Integer.toHexString(r)
                + ((g < 16) ? "0" : "") + Integer.toHexString(g)
                + ((b < 16) ? "0" : "") + Integer.toHexString(b);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.v(TAG, "   onSaveInstanceState");
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.v(TAG, "   onRestoreInstanceState");
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        setProgress(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        String value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
