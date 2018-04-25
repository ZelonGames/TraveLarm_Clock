package zelongames.travelarm_clock.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import zelongames.travelarm_clock.Activities.SettingsActivity;
import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.CustomPreferences.DistancePickerPreference;
import zelongames.travelarm_clock.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsActivity activity = null;

    private EditTextPreference alarmNamePreference = null;
    private DistancePickerPreference distancePreference = null;
    private Preference ringtonePreference = null;
    private SwitchPreference vibratingPreference = null;
    private SwitchPreference enabledPreference = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (SettingsActivity) getActivity();

        addPreferencesFromResource(R.xml.pereferences_settings);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.registerOnSharedPreferenceChangeListener(this);

        initializePreferences();
        updateAlarmNamePreferenceSummary(true);
        updateDistancePreferenceSummary(true);
        updateRingtonePreferenceSummary(true);

        activity.getCurrentAlarm().vibrating = vibratingPreference.isChecked();
        activity.getCurrentAlarm().enabled = enabledPreference.isChecked();
    }

    private void initializePreferences() {
        alarmNamePreference = (EditTextPreference)findPreference(activity.getAlarmPreferenceName());
        SharedPreferences.Editor editor = alarmNamePreference.getSharedPreferences().edit();
        editor.clear().apply();
        alarmNamePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
                editor.clear().apply();
                return false;
            }
        });
        distancePreference = (DistancePickerPreference)findPreference(activity.getDistancePreferenceName());
        ringtonePreference = findPreference(activity.getRingtonePreferenceName());
        vibratingPreference = (SwitchPreference)findPreference(activity.getVibratingPreferenceName());
        enabledPreference = (SwitchPreference)findPreference(activity.getEnabledPreferenceName());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(activity.getAlarmPreferenceName())) {
            updateAlarmNamePreferenceSummary(false);
        } else if (key.equals(activity.getDistancePreferenceName())) {
            updateDistancePreferenceSummary(false);
        } else if (key.equals(activity.getRingtonePreferenceName())) {
            updateRingtonePreferenceSummary(false);
        }
    }

    private void updateAlarmNamePreferenceSummary(boolean firstTime) {
        String summary;
        if (firstTime)
            summary = activity.getCurrentAlarm().getDisplayName(getActivity());
        else
            summary = getCurrentPreferenceValue(alarmNamePreference, false).toString();

        alarmNamePreference.setSummary(summary);
    }

    private void updateDistancePreferenceSummary(boolean firstTime) {
        String value;
        if (firstTime)
            value = Integer.toString(activity.getCurrentAlarm().distanceInMeters);
        else
            value = Integer.toString(distancePreference.getCurrentValue());

        activity.getCurrentAlarm().setMeterTypeName(getActivity(), distancePreference.getMeasureType());
        activity.getCurrentAlarm().meterTypeDistance = Integer.parseInt(value);
        distancePreference.setSummary(activity.getCurrentAlarm().meterTypeDistance + " " + activity.getCurrentAlarm().getMeterTypeName());
    }

    private void updateRingtonePreferenceSummary(boolean firstTime) {
        String alarmName;
        if (firstTime)
            alarmName = Alarm.getRingtoneName(getActivity(), activity.getCurrentAlarm().ringtoneUriString);
        else {
            String value = getCurrentPreferenceValue(ringtonePreference, false).toString();
            alarmName = Alarm.getRingtoneName(getActivity(), value);
        }

        ringtonePreference.setSummary(alarmName);
    }

    private Object getCurrentPreferenceValue(Preference preference, boolean isInt) {
        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        return isInt ? sharedPreferences.getInt(preference.getKey(), 0) : sharedPreferences.getString(preference.getKey(), "");
    }
}
