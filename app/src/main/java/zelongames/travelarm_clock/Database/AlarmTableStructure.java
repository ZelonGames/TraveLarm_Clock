package zelongames.travelarm_clock.Database;

import android.provider.BaseColumns;

public final class AlarmTableStructure {
    public static final String TABLE_NAME = "alarm_table";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_LOCATION_NAME = "location_name";
    public static final String COLUMN_RINGTONE_URI = "ringtone_uri";
    public static final String COLUMN_VIBRATING = "vibrating";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_LOCATION_LAT = "location_lat";
    public static final String COLUMN_LOCATION_LNG = "location_lng";

    public static final class Alarm implements BaseColumns{
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ITEM_NAME + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_LOCATION_NAME + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_RINGTONE_URI + " TEXT NOT NULL, " +
                        COLUMN_VIBRATING + " INTEGER NOT NULL, " +
                        COLUMN_DISTANCE + " DOUBLE NOT NULL, " +
                        COLUMN_LOCATION_LAT + " DOUBLE NOT NULL, " +
                        COLUMN_LOCATION_LNG + " DOUBLE NOT NULL" +
                        ")";
    }
}
