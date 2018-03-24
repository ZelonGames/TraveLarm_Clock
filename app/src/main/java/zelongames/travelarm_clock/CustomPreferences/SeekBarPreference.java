package zelongames.travelarm_clock.CustomPreferences;

import android.app.Dialog;
import android.content.Context;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import zelongames.travelarm_clock.R;

public class SeekBarPreference extends DialogPreference {

    private SeekBar seekBar = null;
    private TextView txtSeekBarValue = null;

    private static final int DEFAULT_VALUE = 75;
    private int currentValue = DEFAULT_VALUE;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.seekbar_preference);

    }

    private void updateTextValue(){
        txtSeekBarValue.setText(seekBar.getProgress() + "%");
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        txtSeekBarValue = getDialog().findViewById(R.id.txtSeekbarValue);
        seekBar = getDialog().findViewById(R.id.seekbar);
        seekBar.setProgress(currentValue);
        updateTextValue();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValue = progress;
                updateTextValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            persistInt(currentValue);
    }
}
