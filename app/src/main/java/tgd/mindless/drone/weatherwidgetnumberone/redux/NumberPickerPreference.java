package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    int DEFAULT_VALUE = 0;
    int mCurrentValue;
    NumberPicker mNumberPicker;
    final static String TAG = "NumberPickerPreference";

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.v(TAG,"   constructor");

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);



        setDialogIcon(null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        DEFAULT_VALUE = a.getInt(index, DEFAULT_VALUE);
        Log.v(TAG,"   onGetDefaultValue: " + String.valueOf(DEFAULT_VALUE));
        return DEFAULT_VALUE;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        Log.v(TAG,"   onSetInitialValue " + String.valueOf(defaultValue) + "   " + String.valueOf(restorePersistedValue));
        if (restorePersistedValue) {
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
        Log.v(TAG,"   onSetInitialValue   mCurrentValue: " + String.valueOf(mCurrentValue));

    }

    @Override
    protected void onBindDialogView(View view) {
        Log.v(TAG,"   onBindDialogView");
        mNumberPicker = (NumberPicker)view.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(20);
        mNumberPicker.setValue(mCurrentValue);
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.v(TAG,"   onDialogClosed");
        mCurrentValue = mNumberPicker.getValue();
        if (positiveResult) {
            persistInt(mCurrentValue);
        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Log.v(TAG,"   onSaveInstanceState");
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = mNumberPicker.getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.v(TAG,"   onRestoreInstanceState");
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState)state;
        super.onRestoreInstanceState(myState.getSuperState());

        mNumberPicker.setValue(myState.value);
    }

    private static class SavedState extends BaseSavedState {
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
