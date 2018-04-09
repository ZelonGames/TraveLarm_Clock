package zelongames.travelarm_clock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.ListAdapters.AlarmListAdapter;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.Helpers.StorageHelper;

public class AlarmCollectionActivity extends ToolbarCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_collection);

        initializeToolBar("Alarm Collection", R.menu.menu_toolbar_back, true);

        ListView listView = (ListView) findViewById(R.id.alarmCollection);

        AlarmListAdapter adapter = new AlarmListAdapter(this, R.layout.alarm_collection_list_layout, hashMapToArrayList(StorageHelper.alarms));
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
        if (nameTxtView.getText().equals(Alarm.UN_NAMED_TAG)){
            TextView locationNameTxtView = view.findViewById(R.id.alarmLocation);
            alarmName = locationNameTxtView.getText().toString();
        }

        return alarmName;
    }
}
