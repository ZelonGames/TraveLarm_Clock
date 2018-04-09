package zelongames.travelarm_clock;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GPS {

    private static final float ZOOM = 15;

    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationCallback locationCallback = null;
    private LocationRequest locationRequest = null;

    public GPS(Context context, LocationCallback locationCallback, boolean startLocationUpdatesImmediately) {
        initializeLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationCallback = locationCallback;

        if (startLocationUpdatesImmediately)
            startLocationUpdates(context);
    }

    public void geoLocate(Context context, GoogleMap gMap, AutoCompleteTextView searchText) {
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d("Exception: ", e.toString());
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            LatLng addressLocation = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(gMap, addressLocation, ZOOM);

            Alarm.currentLocationName = address.getAddressLine(0);
            Alarm.currentLocation = addressLocation;
        } else
            Toast.makeText(context, "Could not find your desired location.", Toast.LENGTH_SHORT).show();
    }

    public void getDeviceLocation(final GoogleMap gMap, final Context context, boolean locationPermissionGranted) {
        try {
            if (locationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(gMap, new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), ZOOM);
                        } else {
                            Toast.makeText(context, "Unable to get your current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    public void moveCamera(GoogleMap gMap, LatLng latLng, float zoom) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void startLocationUpdates(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void initializeLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
