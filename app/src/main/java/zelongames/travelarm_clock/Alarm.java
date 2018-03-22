package zelongames.travelarm_clock;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Alarm implements Parcelable{

    public static String currentAlarmName = "";
    public static String currentLocationName = "";
    public static LatLng currentLocation = null;

    private String name = "";

    public String getName() {
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
    }

    public static boolean canAddAlarm(){
        return !currentLocationName.equals("");
    }

    public boolean hasName(){
        return !getName().equals("");
    }
}
