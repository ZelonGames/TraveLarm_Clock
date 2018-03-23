package zelongames.travelarm_clock;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import zelongames.travelarm_clock.Activities.SettingsActivity;

public class Alarm implements Parcelable{

    public static String currentAlarmName = "";
    public static String currentLocationName = "";
    public static LatLng currentLocation = null;

    public String ringtoneUriString = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
    private Ringtone ringtone = null;

    public Ringtone getRingtone(Context context){
        Uri uri = Uri.parse(ringtoneUriString);
        return ringtone = RingtoneManager.getRingtone(context, uri);
    }

    public void setRingTone(Ringtone ringtone){
        this.ringtone = ringtone;
    }

    private String name = "";

    public String getName() {
        if (name.equals(""))
            return getLocationName();
        else
            return name;
    }

    private String locationName = null;

    public String getLocationName() {
        return locationName;
    }

    private LatLng location = null;

    public boolean isEnabled() {
        return isEnabled;
    }

    private boolean isEnabled = false;

    public Alarm(String locationName, LatLng location) {
        this.locationName = locationName;
        this.location = location;
    }

    private Alarm(Parcel in){
        this.name = in.readString();
        this.locationName = in.readString();
        this.ringtoneUriString = in.readString();
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
    }

    public static boolean canAddAlarm(){
        return !currentLocationName.equals("");
    }

    public boolean hasName(){
        return !getName().equals("");
    }
}
