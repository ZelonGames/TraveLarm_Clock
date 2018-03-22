package zelongames.travelarm_clock.CustomPreferences;

import android.content.Context;


import android.content.res.Resources;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import zelongames.travelarm_clock.R;

public class SeekBarPreference extends DialogPreference {

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.seekbar_preference);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);


    }
}
