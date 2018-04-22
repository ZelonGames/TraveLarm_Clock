package zelongames.travelarm_clock.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.Database.DatabaseHelper;
import zelongames.travelarm_clock.Helpers.DialogHelper;
import zelongames.travelarm_clock.GPS;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.Helpers.MapHelper;
import zelongames.travelarm_clock.PlaceAutocompleteAdapter;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.Helpers.StorageHelper;
import zelongames.travelarm_clock.Helpers.ViewHelper;

public class MainActivity extends ToolbarCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -160), new LatLng(71, 136));

    private boolean locationPermissionGranted = false;

    public Alarm currentAlarm = null;

    private static DatabaseHelper databaseHelper = null;

    public static DatabaseHelper getDatabaseHelper(){
        return databaseHelper;
    }

    private BroadcastReceiver broadcastReceiver = null;
    private AutoCompleteTextView searchText = null;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter = null;
    private GoogleApiClient googleApiClient = null;
    private GeoDataClient geoDataClient = null;

    private GPS gps = null;

    private GoogleMap gMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolBar("TraveLarm Clock", R.menu.menu_toolbar_main, false);

        setupDatabase();

        searchText = findViewById(R.id.input_search);

        setCurrentAlarm(getIntent());

        if (isServiceOK()) {
            getLocationPermission();
            gps = new GPS(this, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (gMap == null)
                        return;

                    for (Location location : locationResult.getLocations()) {
                        gps.moveCamera(gMap, new LatLng(location.getLatitude(), location.getLongitude()), GPS.ZOOM);
                    }
                }
            }, false);
        }
        //AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void setupDatabase(){
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        DatabaseHelper.readItemsFromDatabase(readableDatabase);
    }

    private void setCurrentAlarm(Intent intent) {

        if (intent.getExtras() != null) {
            final String keyName = intent.getExtras().keySet().toArray()[0].toString();

            if (keyName.equals(IntentExtras.alarm))
                currentAlarm = intent.getExtras().getParcelable(keyName);

            if (!currentAlarm.enabled)
                currentAlarm = null;

            if (currentAlarm != null && currentAlarm.enabled) {
                RelativeLayout searchLocationBar = findViewById(R.id.SearchLocationBar);
                LinearLayout alarmInfoBar = findViewById(R.id.AlarmInfoBar);
                ViewHelper.switchBetweenViews(searchLocationBar, alarmInfoBar);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        reloadAlarmMarkers();
    }

    public void onCancelAlarm(View view) {
        LinearLayout alarmInfoBar = findViewById(R.id.AlarmInfoBar);
        RelativeLayout searchLocationBar = findViewById(R.id.SearchLocationBar);
        ViewHelper.switchBetweenViews(alarmInfoBar, searchLocationBar);

        if (currentAlarm != null) {
            currentAlarm.stop(MainActivity.this);
            currentAlarm = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView txtDistanceLeft = findViewById(R.id.txtDistanceLeft);
        final TextView txtDestination = findViewById(R.id.txtDestination);

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (currentAlarm != null) {
                        double recievedLongitude = (double) intent.getExtras().get(IntentExtras.longitude);
                        double recievedLatitude = (double) intent.getExtras().get(IntentExtras.latitude);

                        Location location = new Location("location");
                        location.setLongitude(recievedLongitude);
                        location.setLatitude(recievedLatitude);
                        currentAlarm.updateAlarm(MainActivity.this, location);

                        txtDistanceLeft.setText(getString(R.string.DistanceLeft) + currentAlarm.getCurrentDistance() + "m");
                        String alarmName = currentAlarm.hasCustomName() ? " (" + currentAlarm.getDisplayName(MainActivity.this) + ")" : "";
                        txtDestination.setText(getString(R.string.Destination) + currentAlarm.getLocationName() + alarmName);
                    }
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter(IntentExtras.locationUpdates));

        gps.startLocationUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
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
        gps.geoLocate(this, gMap, searchText);

        if (Alarm.currentLocation == null)
            return;

        showAddAlarmDialog();

        Alarm.resetCurrentValues();
    }

    private void showAddAlarmDialog() {
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(Alarm.currentLocation)
                .title(Alarm.currentLocationName);

        DialogHelper.createPositiveNegativeDialog(this, getString(R.string.NewAlarm),
                getString(R.string.AlarmMessage) + markerOptions.getTitle() + "?",
                getString(R.string.Yes), getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Marker marker = gMap.addMarker(markerOptions);
                        Alarm alarm = new Alarm(markerOptions.getTitle(), markerOptions.getPosition());
                        StorageHelper.alarms.put(alarm.getName(), alarm);
                        DatabaseHelper.addItemToDatabase(alarm, MainActivity.databaseHelper.getWritableDatabase());

                        MapHelper.createCircleAroundAlarm(gMap, alarm);

                        currentAlarm = StorageHelper.alarms.get(alarm.getName());

                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.putExtra(IntentExtras.alarm, currentAlarm);
                        startActivity(intent);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
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
            Toast.makeText(this, getString(R.string.RequestsFailed), Toast.LENGTH_SHORT).show();

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
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (gMap != null)
            return;

        gMap = googleMap;
        gMap.setOnMarkerClickListener(this);

        reloadAlarmMarkers();

        if (locationPermissionGranted) {
            gps.getDeviceLocation(gMap, this, locationPermissionGranted);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);

            init();
        }
    }

    private void reloadAlarmMarkers() {
        if (gMap == null)
            return;

        gMap.clear();

        for (Alarm alarm : StorageHelper.alarms.values()) {
            gMap.addMarker(new MarkerOptions().title(alarm.getName()).position(alarm.getLocation()));
            MapHelper.createCircleAroundAlarm(gMap, alarm);
        }
    }

    private void hideKeyboard() {
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
