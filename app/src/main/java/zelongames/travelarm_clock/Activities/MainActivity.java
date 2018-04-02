package zelongames.travelarm_clock.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.DialogHelper;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.PlaceAutocompleteAdapter;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.StorageHelper;

public class MainActivity extends ToolbarCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -160), new LatLng(71, 136));

    private static final float ZOOM = 15;

    private boolean locationPermissionGranted = false;

    public HashMap<String, Marker> markers = new HashMap<>();

    public Alarm currentAlarm = null;

    private AutoCompleteTextView searchText = null;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter = null;
    private GoogleApiClient googleApiClient = null;
    private GeoDataClient geoDataClient = null;
    private Place place = null;

    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationCallback locationCallback = null;
    private LocationRequest locationRequest = null;

    private GoogleMap gMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolBar("TraveLarm Clock", R.menu.menu_toolbar_main, false);

        searchText = (AutoCompleteTextView) findViewById(R.id.input_search);

        setCurrentAlarm();

        if (isServiceOK()) {
            getLocationPermission();
            initializeLocationRequest();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                    }
                });
            }

            updateLocation();
        }
    }

    private void setCurrentAlarm() {
        if (getIntent().getExtras() != null) {
            currentAlarm = getIntent().getExtras().getParcelable(IntentExtras.alarm);
            if (!currentAlarm.enabled)
                currentAlarm = null;
            else {
                String alarmName = currentAlarm.getName();
                currentAlarm = StorageHelper.alarms.get(alarmName);
            }
        }
    }

    private void updateLocation() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (currentAlarm != null) {
                        currentAlarm.updateAlarm(MainActivity.this, location);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
    }

    private void initializeLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    private void init() {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        geoDataClient = Places.getGeoDataClient(this, null);

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, geoDataClient, LAT_LNG_BOUNDS, null);

        searchText.setAdapter(placeAutocompleteAdapter);
        searchText.setOnItemClickListener(autoCompleteClickListener);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                hideKeyboard();
                addAlarm();
                return false;
            }
        });
    }

    private void addAlarm() {
        geoLocate();

        if (Alarm.currentLocation == null)
            return;

        final MarkerOptions markerOptions = new MarkerOptions()
                .position(Alarm.currentLocation)
                .title(Alarm.currentLocationName);

        DialogHelper.createPositiveNegativeDialog(this, "New Alarm",
                "Are you sure you want to create an alarm at: " + markerOptions.getTitle() + "?",
                "Yes", "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Marker marker = gMap.addMarker(markerOptions);
                        Alarm alarm = new Alarm(markerOptions.getTitle(), markerOptions.getPosition(), marker);
                        StorageHelper.alarms.put(alarm.getName(), alarm);

                        createCircleAroundAlarm(alarm);

                        Toast.makeText(MainActivity.this, "New alarm created at: " + alarm.getLocationName() + ".", Toast.LENGTH_SHORT).show();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

        Alarm.resetCurrentValues();
    }

    private void geoLocate() {
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d("Exception: ", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            LatLng addressLocation = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(addressLocation, ZOOM, address.getAddressLine(0));

            Alarm.currentLocationName = address.getAddressLine(0);
            Alarm.currentLocation = addressLocation;
        } else
            Toast.makeText(this, "Could not find your desired location.", Toast.LENGTH_SHORT).show();
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), ZOOM, "");
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to get your current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    public boolean isServiceOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            errorDialog.show();
        } else
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();

        return false;
    }

    private void getLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                locationPermissionGranted = true;
                initMap();
            } else
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }

                    locationPermissionGranted = true;
                    initMap();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (gMap != null)
            return;

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        gMap = googleMap;
        gMap.setOnMarkerClickListener(this);

        for (Alarm alarm : StorageHelper.alarms.values()) {
            Marker marker = alarm.marker;
            alarm.marker = gMap.addMarker(new MarkerOptions().title(marker.getTitle()).position(marker.getPosition()));
            createCircleAroundAlarm(alarm);
        }

        if (locationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);

            init();
        }
    }

    private void createCircleAroundAlarm(Alarm alarm) {
        CircleOptions circleOptions = new CircleOptions()
                .center(alarm.getLocation())
                .radius(alarm.distanceInMeters)
                .fillColor(R.color.colorPrimaryLight)
                .strokeWidth(0);

        gMap.addCircle(circleOptions);
    }

    private void hideKeyboard() {
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener autoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard();
            addAlarm();
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResults = Places.GeoDataApi.getPlaceById(googleApiClient, placeID);
            placeResults.setResultCallback(updatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
        }
    };

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentAlarm = StorageHelper.alarms.get(marker.getTitle());

        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(IntentExtras.alarm, currentAlarm);
        startActivity(intent);

        return false;
    }
}
