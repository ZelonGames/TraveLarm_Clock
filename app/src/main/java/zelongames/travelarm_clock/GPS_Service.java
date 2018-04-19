package zelongames.travelarm_clock;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class GPS_Service extends Service {

    private GPS gps = null;
/*
    public GPS_Service(){
        super("GPS Thread");
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        gps = new GPS(this, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Intent locationUpdates = new Intent(IntentExtras.locationUpdates);
                    locationUpdates.putExtra(IntentExtras.longitude, location.getLongitude());
                    locationUpdates.putExtra(IntentExtras.latitude, location.getLatitude());
                    sendBroadcast(locationUpdates);
                }
            }
        }, true);
    }
/*
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
*/
/*
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("MyIntentService", "onHandle: " + Thread.currentThread().getName());

        gps.startLocationUpdates(this);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        gps.removeLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stop(Context context) {
        Intent gpsService = new Intent(context, GPS_Service.class);
        context.stopService(gpsService);
    }

    public static void start(Context context){
        Intent gpsService = new Intent(context, GPS_Service.class);
        context.startService(gpsService);
    }
}
