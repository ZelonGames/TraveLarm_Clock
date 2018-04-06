package zelongames.travelarm_clock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.drm.DrmStore;
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

    public static void resetCurrentValues() {
        currentLocationName = null;
        currentLocation = null;
    }

    public Marker marker = null;

    public String ringtoneUriString = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
    private Ringtone ringtone = null;
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

    private boolean isRunning = false;

    public boolean getIsRunning(){
        return isRunning;
    }

    public Alarm(String locationName, LatLng location, Marker marker) {
        this.locationName = locationName;
        this.location = location;
        this.marker = marker;
    }

    private Alarm(Parcel in) {
        this.name = in.readString();
        this.locationName = in.readString();
        this.ringtoneUriString = in.readString();
        this.vibrating = byteToBoolean(in.readByte());
        this.enabled = byteToBoolean(in.readByte());
    }

    public void updateAlarm(final Context context, Location location) {
        if (!enabled || (ringtone != null && ringtone.isPlaying()))
            return;

        final float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), getLocation().latitude, getLocation().longitude, results);
        if (ringtone == null)
            setRingtone(context);
        if (results[0] <= distanceInMeters) {
            isRunning = true;
        }
    }

    public void run(Context context){
        //showDialog(context);
        ringtone.play();
        isRunning = false;
    }

    private void createDialog(Context context) {
        if (dialog != null)
            return;

        AlertDialog.Builder dialogBuilder = DialogHelper.createDialogBuilder(context, "Alarm", "Wake up!");

        dialogBuilder.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ringtone.stop();
                dialog.dismiss();
                enabled = false;
                ringtone = null;
            }
        });

        dialog = dialogBuilder.create();
    }

    public void showDialog(Context context){
        createDialog(context);
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
        parcel.writeString(getName());
        parcel.writeString(getLocationName());
        parcel.writeString(ringtoneUriString);
        parcel.writeByte(booleanToByte(vibrating));
        parcel.writeByte(booleanToByte(enabled));
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
