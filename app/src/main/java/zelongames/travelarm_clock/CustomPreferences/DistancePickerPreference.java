package zelongames.travelarm_clock.CustomPreferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import zelongames.travelarm_clock.R;

public class DistancePickerPreference extends DialogPreference {

    private static final int DEFAULT_VALUE = 200;
    private int currentValue = DEFAULT_VALUE;

    private Integer currentMeasureType = null;
    private NumberPicker numberPicker = null;
    private NumberPicker measurePicker = null;

    private static final String KM = "km";
    private static final String M = "m";
    private static final String FT = "ft";

    private static final String[] measures = {
            KM,
            M,
            FT,
    };

    public DistancePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.distance_picker_preference);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        setupNumberPicker();
        setupMeasurePicker();
    }

    private void setupNumberPicker() {
        final String[] values = new String[10];

        for (int i = 1; i <= values.length; i++) {
            values[i - 1] = Integer.toString(100 * i);
        }

        numberPicker = getDialog().findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setValue(currentValue / 100 - 1);
        numberPicker.setDisplayedValues(values);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentValue = Integer.parseInt(values[newVal]);
            }
        });
    }

    private void setupMeasurePicker() {
        measurePicker = getDialog().findViewById(R.id.measurePicker);
        measurePicker.setMinValue(0);
        measurePicker.setMaxValue(measures.length - 1);
        measurePicker.setValue(1);
        currentMeasureType = measurePicker.getValue();
        measurePicker.setDisplayedValues(measures);

        measurePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentMeasureType = newVal;
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(getConvertedValue(measures[currentMeasureType]));
        }
    }

    private int getConvertedValue(String measure) {
        // Convert anything to meters
        switch (measure) {
            case KM:
                return currentValue * 1000;
            case M:
                return currentValue;
            case FT:
                return (int)(currentValue * 0.3048);
            default:
                return currentValue;
        }
    }
}
