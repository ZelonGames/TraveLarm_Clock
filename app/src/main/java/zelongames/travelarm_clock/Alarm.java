package zelongames.travelarm_clock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import zelongames.travelarm_clock.Activities.MainActivity;

public class Alarm implements Parcelable {

    public static String currentAlarmName = "";
    public static String currentLocationName = "";
    public static LatLng currentLocation = null;

    public Marker marker = null;

    public String ringtoneUriString = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
    private Ringtone ringtone = null;

    public Ringtone getRingtone(Context context) {
        Uri uri = Uri.parse(ringtoneUriString);
        return ringtone = RingtoneManager.getRingtone(context, uri);
    }

    public void setRingtone(Ringtone ringtone) {
        this.ringtone = ringtone;
    }

    private String name = "";

    public String getName() {
        if (name.equals(""))
            return getLocationName();
        else
            return name;
    }

    public void setName(String name, HashMap<String, Alarm> alarmList) {
        if (alarmList.containsKey(getName()))
            alarmList.put(name, alarmList.remove(getName()));

        this.name = name;
        marker.setTitle(name);
    }

    private String locationName = null;

    public String getLocationName() {
        return locationName;
    }

    private LatLng location = null;

    public LatLng getLocation() {
        return location;
    }

    public int distanceInMeters = 200;
    public boolean vibrating = false;
    public boolean enabled = false;

    public Alarm(String locationName, LatLng location, Marker marker) {
        this.locationName = locationName;
        this.location = location;
        this.marker = marker;
    }

    private Alarm(Parcel in) {
        this.name = in.readString();
        this.locationName = in.readString();
        this.ringtoneUriString = in.readString();
        this.vibrating = in.readByte() == 1 ? true : false;
        this.enabled = in.readByte() == 1 ? true : false;
    }

    public void updateAlarm(final Context context, Location location) {
        if (!enabled)
            return;

        final float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getLocation().latitude, getLocation().longitude, results);
        if (results[0] <= distanceInMeters) {
            getRingtone(context).play();
            showDialog(context);
            enabled = false;
        }
    }

    public void showDialog(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Wake up!").setTitle(getName());

        alertDialogBuilder.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRingtone(context).stop();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertDialogBuilder.create();
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
        parcel.writeString(name);
        parcel.writeString(locationName);
        parcel.writeString(ringtoneUriString);
        parcel.writeByte(vibrating ? (byte) 1 : 0);
        parcel.writeByte(enabled ? (byte) 1 : 0);
    }

    private boolean byteToBoolean(byte value) {
        return value == 1 ? true : false;
    }

    private byte booleanToByte(boolean value) {
        return value ? (byte) 1 : 0;
    }

    public static boolean canAddAlarm() {
        return !currentLocationName.equals("");
    }

    public boolean hasName() {
        return !getName().equals("");
    }
}
