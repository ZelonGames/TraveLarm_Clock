package zelongames.travelarm_clock;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import zelongames.travelarm_clock.Activities.MainActivity;

public class GPS_Service extends Service {
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationCallback locationCallback = null;
    private LocationRequest locationRequest = null;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        updateLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void initializeLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void updateLocation() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Intent locationUpdates = new Intent(IntentExtras.locationUpdates);
                    locationUpdates.putExtra(IntentExtras.longitude, location.getLongitude());
                    locationUpdates.putExtra(IntentExtras.latitude, location.getLatitude());
                    sendBroadcast(locationUpdates);
                }
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
