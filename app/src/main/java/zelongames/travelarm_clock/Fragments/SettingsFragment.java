package zelongames.travelarm_clock.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import zelongames.travelarm_clock.Activities.SettingsActivity;
import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.CustomPreferences.DistancePickerPreference;
import zelongames.travelarm_clock.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsActivity activity = null;

    private Preference alarmNamePreference = null;
    private DistancePickerPreference distancePreference = null;
    private Preference ringtonePreference = null;

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
    }

    private void initializePreferences() {
        alarmNamePreference = findPreference(activity.getAlarmPreferenceName());
        distancePreference = (DistancePickerPreference)findPreference(activity.getDistancePreferenceName());
        ringtonePreference = findPreference(activity.getRingtonePreferenceName());
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
            value = Integer.toString(activity.getCurrentAlarm().meterTypeDistance);
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
