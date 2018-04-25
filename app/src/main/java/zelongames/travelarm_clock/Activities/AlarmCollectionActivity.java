package zelongames.travelarm_clock.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.Database.DatabaseHelper;
import zelongames.travelarm_clock.Helpers.DialogHelper;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.ListAdapters.AlarmListAdapter;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.Helpers.StorageHelper;

public class AlarmCollectionActivity extends ToolbarCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_collection);

        initializeToolBar(getString(R.string.AlarmCollectionTitle), R.menu.menu_toolbar_back, true);

        DatabaseHelper.readItemsFromDatabase(MainActivity.getDatabaseHelper().getReadableDatabase());

        final ListView listView = findViewById(R.id.alarmCollection);

        final AlarmListAdapter adapter = new AlarmListAdapter(this, R.layout.alarm_collection_list_layout, hashMapToArrayList(StorageHelper.alarms));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AlarmCollectionActivity.this, SettingsActivity.class);
                Alarm chosenAlarm = StorageHelper.alarms.get(getChosenAlarmName(view));
                intent.putExtra(IntentExtras.alarm, chosenAlarm);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                TextView txtAlarmName = view.findViewById(R.id.alarmName);
                TextView txtLocationName = view.findViewById(R.id.alarmLocation);

                final String alarmDisplayName = txtAlarmName.getText().toString();
                final String location = txtLocationName.getText().toString();

                String message = getString(R.string.RemoveQuestionPart1) + "\"" + location + " (" + alarmDisplayName + ")\"" + getString(R.string.RemoveQuestionPart2);

                Dialog dialog = DialogHelper.createPositiveNegativeDialog(AlarmCollectionActivity.this, alarmDisplayName, message, getString(R.string.Yes), getString(R.string.No),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Alarm.removeAlarm(AlarmCollectionActivity.this, alarmDisplayName, location, MainActivity.getDatabaseHelper());
                                adapter.remove(adapter.getItem(position));
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.show();

                return true;
            }
        });
    }

    private <T> ArrayList<T> hashMapToArrayList(HashMap<String, T> hashMap){
        ArrayList<T> arrayList = new ArrayList<>();

        for (T object : hashMap.values()){
            arrayList.add(object);
        }

        return arrayList;
    }

    private String getChosenAlarmName(View view){
        TextView nameTxtView = view.findViewById(R.id.alarmName);
        String alarmName = nameTxtView.getText().toString();
        if (nameTxtView.getText().equals(getString(R.string.Unnamed))){
            TextView locationNameTxtView = view.findViewById(R.id.alarmLocation);
            alarmName = locationNameTxtView.getText().toString();
        }

        return alarmName;
    }
}
