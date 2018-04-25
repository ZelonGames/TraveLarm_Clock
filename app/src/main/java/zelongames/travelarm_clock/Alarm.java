package zelongames.travelarm_clock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import zelongames.travelarm_clock.CustomPreferences.DistancePickerPreference;
import zelongames.travelarm_clock.Database.DatabaseHelper;
import zelongames.travelarm_clock.Helpers.DialogHelper;
import zelongames.travelarm_clock.Helpers.StorageHelper;
import zelongames.travelarm_clock.Helpers.ViewHelper;

public final class Alarm implements Parcelable {

    public static String currentAlarmName = "";
    public static String currentLocationName = "";
    public static LatLng currentLocation = null;

    public static void resetCurrentValues() {
        currentLocationName = null;
        currentLocation = null;
    }

    private Vibrator vibrator = null;

    private AudioManager audioManager = null;

    public String ringtoneUriString = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
    private Ringtone ringtone = null;

    public String getRingtoneName(Context context) {
        return ringtone == null ? "" : ringtone.getTitle(context);
    }

    public static String getRingtoneName(Context context, String ringtoneURI) {
        Uri uri = Uri.parse(ringtoneURI);
        Ringtone dummyRingtone = RingtoneManager.getRingtone(context, uri);
        return dummyRingtone.getTitle(context);
    }

    public Ringtone setRingtone(Context context) {
        Uri uri = Uri.parse(ringtoneUriString);
        return ringtone = RingtoneManager.getRingtone(context, uri);
    }

    private Dialog dialog = null;

    private String name = "";

    public String getName() {
        if (name.equals(""))
            return getLocationName();
        else
            return name;
    }

    public String getDisplayName(Context context) {
        return hasCustomName() ? getName() : context.getString(R.string.Unnamed);
    }

    public void setName(String name, HashMap<String, Alarm> alarmList) {
        if (alarmList.containsKey(getName()))
            alarmList.put(name, alarmList.remove(getName()));

        this.name = name;
    }

    private String locationName = null;

    public String getLocationName() {
        return locationName;
    }

    private String meterTypeName = "";

    public String getMeterTypeName() {
        return meterTypeName;
    }

    public void setMeterTypeName(Context context, DistancePickerPreference.MeasureType measureType) {
        switch (measureType) {
            case KM:
                meterTypeName = context.getString((R.string.Kilometers));
                break;
            case M:
                meterTypeName = context.getString((R.string.Meters));
                break;
            case FT:
                meterTypeName = context.getString(R.string.Feet);
                break;
            default:
                meterTypeName = measureType.name();
                break;
        }
    }

    private LatLng location = null;

    public LatLng getLocation() {
        return location;
    }

    private Float currentDistance = null;

    public Float getCurrentDistance() {
        return currentDistance;
    }

    public int distanceInMeters = 200;
    public int meterTypeDistance = distanceInMeters;
    public boolean vibrating = true;
    public boolean enabled = false;

    private boolean isRunning = false;

    public boolean getIsRunning() {
        return isRunning;
    }

    public Alarm() {
    }

    public Alarm(String locationName, LatLng location) {
        this.locationName = locationName;
        this.location = location;
    }

    public Alarm(String name, String locationName, String ringtoneURI, LatLng location, boolean vibrating, int distance) {
        this.name = name;
        this.locationName = locationName;
        this.ringtoneUriString = ringtoneURI;
        this.location = location;
        this.vibrating = vibrating;
        this.distanceInMeters = distance;
    }

    private Alarm(Parcel in) {
        this.location = new LatLng(in.readDouble(), in.readDouble());
        this.name = in.readString();
        this.locationName = in.readString();
        this.ringtoneUriString = in.readString();
        this.meterTypeName = in.readString();
        this.meterTypeDistance = in.readInt();
        this.distanceInMeters = in.readInt();
        this.vibrating = byteToBoolean(in.readByte());
        this.enabled = byteToBoolean(in.readByte());
        this.isRunning = byteToBoolean(in.readByte());
    }

    public void updateAlarm(Context context, Location location) {
        if (!enabled || isRunning)
            return;

        final float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getLocation().latitude, getLocation().longitude, results);
        currentDistance = results[0];

        if (results[0] <= distanceInMeters) {
            if (ringtone == null)
                setRingtone(context);
            isRunning = true;
        }

        if (getIsRunning())
            start(context);
    }

    public void stop(Context context) {
        isRunning = false;
        if (ringtone != null)
            ringtone.stop();
        if (vibrator != null)
            vibrator.cancel();
        enabled = false;
        ringtone = null;
        currentDistance = null;

        GPS_Service.stop(context);
    }

    public void start(Context context) {
        if (isRunning) {
            showDialog(context);
            vibrate(context);
            ringtone.play();
        }
    }

    private void vibrate(Context context) {
        if (!vibrating)
            return;

        if (vibrator == null)
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 600, 400};
        vibrator.vibrate(pattern, 0);
    }

    private void showDialog(final Context context) {
        AlertDialog.Builder dialogBuilder = DialogHelper.createDialogBuilder(context, getName(), context.getString(R.string.WakeUp));

        final AppCompatActivity activity = (AppCompatActivity) context;

        dialogBuilder.setNeutralButton(context.getString(R.string.Stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stop(context);
                LinearLayout alarmInfoBar = activity.findViewById(R.id.AlarmInfoBar);
                RelativeLayout searchLocationBar = activity.findViewById(R.id.SearchLocationBar);
                ViewHelper.switchBetweenViews(alarmInfoBar, searchLocationBar);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel parcel) {
            return new Alarm(parcel);
        }

        @Override
        public Alarm[] newArray(int i) {
            return new Alarm[i];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(location.latitude);
        parcel.writeDouble(location.longitude);
        parcel.writeString(getName());
        parcel.writeString(getLocationName());
        parcel.writeString(ringtoneUriString);
        parcel.writeString(meterTypeName);
        parcel.writeInt(meterTypeDistance);
        parcel.writeInt(distanceInMeters);
        parcel.writeByte(booleanToByte(vibrating));
        parcel.writeByte(booleanToByte(enabled));
        parcel.writeByte(booleanToByte(isRunning));
    }

    private boolean byteToBoolean(byte value) {
        return value == 1 ? true : false;
    }

    private byte booleanToByte(boolean value) {
        return value ? (byte) 1 : 0;
    }

    public boolean hasCustomName() {
        return !getName().equals(getLocationName());
    }

    public static void removeAlarm(Context context, String displayName, String locationName, DatabaseHelper db) {
        String alarmName = displayName ==  context.getString(R.string.Unnamed) ? locationName : displayName;
        DatabaseHelper.deleteItemFromDatabase(StorageHelper.alarms.get(alarmName), db.getWritableDatabase());
    }
}
