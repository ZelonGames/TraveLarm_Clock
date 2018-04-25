package zelongames.travelarm_clock.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.Database.DatabaseHelper;
import zelongames.travelarm_clock.GPS_Service;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.Fragments.SettingsFragment;
import zelongames.travelarm_clock.Helpers.StorageHelper;


public class SettingsActivity extends ToolbarCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Alarm currentAlarm = null;

    public Alarm getCurrentAlarm(){
        return currentAlarm;
    }

    private String ringtoneUriString = null;
    private String alarmName = null;
    private Integer distance = null;

    public String getRingtoneUriString() {
        return ringtoneUriString;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public Integer getDistance() {
        return distance;
    }

    private Boolean vibrating = null;
    private Boolean enabled = null;

    private String alarmPreferenceName = null;

    public String getAlarmPreferenceName() {
        return alarmPreferenceName;
    }

    public String getDistancePreferenceName() {
        return distancePreferenceName;
    }

    public String getRingtonePreferenceName() {
        return ringtonePreferenceName;
    }

    public String getVibratingPreferenceName() {
        return vibratingPreferenceName;
    }

    public String getEnabledPreferenceName() {
        return enabledPreferenceName;
    }

    private String distancePreferenceName = null;
    private String ringtonePreferenceName = null;
    private String vibratingPreferenceName = null;
    private String enabledPreferenceName = null;

    private SharedPreferences alarmPreference = null;
    private SharedPreferences vibratingPreference = null;
    private SharedPreferences enabledPreference = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_settings);
        initializeToolBar(getString(R.string.AlarmSettingsTitle), R.menu.menu_toolbar_back, true);

        initializeSharedPreferenceNames();

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

        initializeSharedPreferences();
    }

    private void initializeSharedPreferenceNames(){
        alarmPreferenceName = getString(R.string.alarmName);
        distancePreferenceName = getString(R.string.distancePicker);
        ringtonePreferenceName = getString(R.string.ringtonePreference);
        vibratingPreferenceName = getString(R.string.vibrating);
        enabledPreferenceName = getString(R.string.enabled);
    }

    private void initializeSharedPreferences(){
        alarmPreference = getSharedPreferences(alarmPreferenceName, MODE_PRIVATE);
        vibratingPreference = getSharedPreferences(vibratingPreferenceName, MODE_PRIVATE);
        enabledPreference = getSharedPreferences(enabledPreferenceName, MODE_PRIVATE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(alarmPreferenceName)) {
            alarmName = sharedPreferences.getString(key, "");
        } else if (key.equals(distancePreferenceName)) {
            distance = sharedPreferences.getInt(key, 200);
        } else if (key.equals(ringtonePreferenceName)) {
            ringtoneUriString = sharedPreferences.getString(key, "");
        } else if (key.equals(vibratingPreferenceName)) {
            vibrating = sharedPreferences.getBoolean(key, true);
        } else if (key.equals(enabledPreferenceName)) {
            enabled = sharedPreferences.getBoolean(key, true);
        }
    }

    public void onApplyChanges(View view) {
        final String oldName = new String(currentAlarm.getName());

        if (alarmName != null)
            currentAlarm.setName(alarmName, StorageHelper.alarms);
        else
            alarmName = currentAlarm.getName();

        if (distance != null)
            currentAlarm.distanceInMeters = currentAlarm.meterTypeDistance = distance;
        else
            distance = currentAlarm.meterTypeDistance = currentAlarm.distanceInMeters;

        if (ringtoneUriString != null)
            currentAlarm.ringtoneUriString = ringtoneUriString;
        else
            ringtoneUriString = currentAlarm.ringtoneUriString;

        if (vibrating != null)
            currentAlarm.vibrating = vibrating;
        if (enabled != null)
            currentAlarm.enabled = enabled;

        DatabaseHelper.editItemFromDatabase(currentAlarm, oldName, alarmName, ringtoneUriString, "", currentAlarm.meterTypeDistance, "", "", MainActivity.getDatabaseHelper().getWritableDatabase());

        GPS_Service.start(SettingsActivity.this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IntentExtras.alarm, currentAlarm);
        startActivity(intent);
    }

}
