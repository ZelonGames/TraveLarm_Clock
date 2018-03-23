package zelongames.travelarm_clock.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.IntentExtras;
import zelongames.travelarm_clock.ListAdapters.AlarmListAdapter;
import zelongames.travelarm_clock.R;
import zelongames.travelarm_clock.StorageHelper;

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
}
