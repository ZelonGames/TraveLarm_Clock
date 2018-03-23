package zelongames.travelarm_clock.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;;import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.SettingsFragment;


public class SettingsActivity extends ToolbarCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Ringtone currentRingtone = null;
    private Alarm currentAlarm = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_settings);
        initializeToolBar("Alarm Settings", R.menu.menu_toolbar_back, true);

        if (getIntent().getExtras() != null) {
            currentAlarm = getIntent().getExtras().getParcelable(IntentExtras.alarm);
            Toast.makeText(this, currentAlarm.getLocationName(), Toast.LENGTH_SHORT).show();
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.displayPrefs, new SettingsFragment())
                .commit();

        SharedPreferences settings = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        settings.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        final String preferenceStringValue = sharedPreferences.getString(s, "");

        switch (s) {
            case "alarm":
                Uri uri = Uri.parse(preferenceStringValue);
                currentRingtone = RingtoneManager.getRingtone(SettingsActivity.this, uri);
                currentAlarm.setRingTone(currentRingtone);
                break;
        }
    }
}
