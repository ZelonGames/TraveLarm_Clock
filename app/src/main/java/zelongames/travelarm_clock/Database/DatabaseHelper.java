package zelongames.travelarm_clock.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.Helpers.StorageHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlarmTableStructure.Alarm.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void addItemToDatabase(Alarm alarm, SQLiteDatabase db){
        ContentValues values = new ContentValues();

        StorageHelper.alarms.put(alarm.getName(), alarm);

        values.put(AlarmTableStructure.COLUMN_ITEM_NAME, alarm.getName());
        values.put(AlarmTableStructure.COLUMN_LOCATION_NAME, alarm.getLocationName());
        values.put(AlarmTableStructure.COLUMN_RINGTONE_URI, alarm.ringtoneUriString);
        values.put(AlarmTableStructure.COLUMN_VIBRATING, alarm.vibrating);
        values.put(AlarmTableStructure.COLUMN_DISTANCE, alarm.distanceInMeters);
        values.put(AlarmTableStructure.COLUMN_LOCATION_LAT, alarm.getLocation().latitude);
        values.put(AlarmTableStructure.COLUMN_LOCATION_LNG, alarm.getLocation().longitude);

        db.insert(AlarmTableStructure.TABLE_NAME, null, values);
    }

    public static void editItemFromDatabase(Alarm alarm, String oldName, String newName, String newRingtoneURI, String newVibrating, int newDistance, String newLat, String newLng, SQLiteDatabase db){
        // Update distance
        String query = "UPDATE " + AlarmTableStructure.TABLE_NAME +
                " SET " + AlarmTableStructure.COLUMN_DISTANCE + " = '" + newDistance + "' " +
                "WHERE " + AlarmTableStructure.COLUMN_ITEM_NAME + " = '" + oldName + "'";
        db.execSQL(query);

        // Update ringtone
        query = "UPDATE " + AlarmTableStructure.TABLE_NAME +
                " SET " + AlarmTableStructure.COLUMN_RINGTONE_URI + " = '" + newRingtoneURI + "' " +
                "WHERE " + AlarmTableStructure.COLUMN_ITEM_NAME + " = '" + oldName + "'";
        db.execSQL(query);

        // Update name
        query = "UPDATE " + AlarmTableStructure.TABLE_NAME +
                " SET " + AlarmTableStructure.COLUMN_ITEM_NAME + " = '" + newName + "' " +
                "WHERE " + AlarmTableStructure.COLUMN_ITEM_NAME + " = '" + oldName + "'";
        db.execSQL(query);
    }

    public static void deleteItemFromDatabase(Alarm alarm, SQLiteDatabase db){
        String query = "DELETE FROM " + AlarmTableStructure.TABLE_NAME + " WHERE " +
                AlarmTableStructure.COLUMN_ITEM_NAME + " = '" + alarm.getName() + "'";
        StorageHelper.alarms.remove(alarm.getName());
        db.execSQL(query);
    }

    public static void readItemsFromDatabase(SQLiteDatabase db){
        String[] itemColumns = {
                AlarmTableStructure.COLUMN_ITEM_NAME,
                AlarmTableStructure.COLUMN_LOCATION_NAME,
                AlarmTableStructure.COLUMN_RINGTONE_URI,
                AlarmTableStructure.COLUMN_VIBRATING,
                AlarmTableStructure.COLUMN_DISTANCE,
                AlarmTableStructure.COLUMN_LOCATION_LAT,
                AlarmTableStructure.COLUMN_LOCATION_LNG,
        };

        Cursor cursor = db.query(AlarmTableStructure.TABLE_NAME, itemColumns, null, null, null, null,
                AlarmTableStructure.COLUMN_ITEM_NAME + " DESC");

        final int itemNamePos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_ITEM_NAME);
        final int itemLocationNamePos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_LOCATION_NAME);
        final int itemRingtoneURIPos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_RINGTONE_URI);
        final int itemVibratingPos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_VIBRATING);
        final int itemDistancePos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_DISTANCE);
        final int itemLocationLatPos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_LOCATION_LAT);
        final int itemLocationLngPos = cursor.getColumnIndex(AlarmTableStructure.COLUMN_LOCATION_LNG);

        StorageHelper.alarms.clear();
        while (cursor.moveToNext()){
            final String name = cursor.getString(itemNamePos);
            final String locationName = cursor.getString(itemLocationNamePos);
            final String ringtoneURI = cursor.getString(itemRingtoneURIPos);
            final boolean vibrating = cursor.getInt(itemVibratingPos) == 1;
            final int distance = cursor.getInt(itemDistancePos);
            final double lat = cursor.getDouble(itemLocationLatPos);
            final double lng = cursor.getDouble(itemLocationLngPos);
            final LatLng location = new LatLng(lat, lng);

            Alarm alarm = new Alarm(name, locationName, ringtoneURI, location, vibrating, distance);
            StorageHelper.alarms.put(alarm.getName(), alarm);
        }
        cursor.close();
    }
}
