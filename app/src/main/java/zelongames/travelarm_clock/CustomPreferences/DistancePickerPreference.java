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

    public enum MeasureType {
        KM,
        M,
        FT,
    }

    private MeasureType measureType = MeasureType.M;

    public MeasureType getMeasureType() {
        return measureType;
    }

    private String[] values = new String[10];

    private NumberPicker numberPicker = null;
    private NumberPicker measurePicker = null;

    public int getCurrentValue(){
        return currentValue;
    }

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

    private void initializeMeasureTypeValues(MeasureType measureType) {
        if (numberPicker == null)
            numberPicker = getDialog().findViewById(R.id.numberPicker);

        switch (measureType) {
            case KM:
                for (int i = 1; i <= values.length; i++) {
                    values[i - 1] = Integer.toString(i);
                }
                break;
            case M:
            case FT:
                for (int i = 1; i <= values.length; i++) {
                    values[i - 1] = Integer.toString(100 * i);
                }
                break;
        }

        numberPicker = getDialog().findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setValue(currentValue / 100 - 1);
        numberPicker.setDisplayedValues(values);
    }

    private void setupNumberPicker() {
        initializeMeasureTypeValues(measureType);

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
        measurePicker.setMaxValue(MeasureType.values().length - 1);
        measurePicker.setValue(1);
        measureType = MeasureType.values()[measurePicker.getValue()];

        measurePicker.setDisplayedValues(getMeasures());

        measurePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                measureType = MeasureType.values()[newVal];
                initializeMeasureTypeValues(measureType);
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(getConvertedValue(measureType));

        }
    }

    private int getConvertedValue(MeasureType measureType) {
        // Convert anything to meters
        switch (measureType) {
            case KM:
                return currentValue * 1000;
            case M:
                return currentValue;
            case FT:
                return (int) (currentValue * 0.3048);
            default:
                return currentValue;
        }
    }

    private String[] getMeasures() {
        String[] measures = new String[MeasureType.values().length];

        for (int i = 0; i < MeasureType.values().length; i++) {
            measures[i] = MeasureType.values()[i].name();
        }

        return measures;
    }
}
