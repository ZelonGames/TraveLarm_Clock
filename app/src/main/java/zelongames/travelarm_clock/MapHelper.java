package zelongames.travelarm_clock;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;

public final class MapHelper {

    public static void createCircleAroundAlarm(GoogleMap gMap, Alarm alarm) {
        CircleOptions circleOptions = new CircleOptions()
                .center(alarm.getLocation())
                .radius(alarm.distanceInMeters)
                .fillColor(R.color.colorPrimaryLight)
                .strokeWidth(0);

        gMap.addCircle(circleOptions);
    }
}
