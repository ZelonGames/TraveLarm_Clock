package zelongames.travelarm_clock.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;;import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.GPS_Service;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.SettingsFragment;
import zelongames.travelarm_clock.Helpers.StorageHelper;


public class SettingsActivity extends ToolbarCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Alarm currentAlarm = null;

    private String ringtoneUriString = null;
    private String alarmName = null;
    private Integer distance = null;
    private boolean vibrating = true;
    private boolean enabled = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_settings);
        initializeToolBar("Alarm Settings", R.menu.menu_toolbar_back, true);

        if (getIntent().getExtras() != null) {
            currentAlarm = getIntent().getExtras().getParcelable(IntentExtras.alarm);
            String alarmName = currentAlarm.getName();
            currentAlarm = StorageHelper.alarms.get(alarmName);

            Toast.makeText(this, currentAlarm.getName(), Toast.LENGTH_SHORT).show();
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.displayPrefs, new SettingsFragment())
                .commit();

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        settings.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        switch (s) {
            case "alarmName":
                alarmName = sharedPreferences.getString(s, "");
                break;
            case "distancePicker":
                distance = sharedPreferences.getInt(s, 200);
                break;
            case "alarm":
                ringtoneUriString = sharedPreferences.getString(s, "");
                break;
            case "vibrate":
                vibrating = sharedPreferences.getBoolean(s, true);
                break;
            case "enabled":
                enabled = sharedPreferences.getBoolean(s, true);
                break;
            default:
                break;
        }
    }

    public void onApplyChanges(View view) {
        if (alarmName != null)
            currentAlarm.setName(alarmName, StorageHelper.alarms);
        if (distance != null)
            currentAlarm.distanceInMeters = distance;
        if (ringtoneUriString != null)
            currentAlarm.ringtoneUriString = ringtoneUriString;
        currentAlarm.vibrating = vibrating;
        currentAlarm.enabled = enabled;

        GPS_Service.start(SettingsActivity.this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IntentExtras.alarm, currentAlarm);
        startActivity(intent);
    }
}
