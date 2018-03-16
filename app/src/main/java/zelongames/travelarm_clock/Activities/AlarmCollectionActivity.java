package zelongames.travelarm_clock.Activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.ListAdapters.AlarmListAdapter;
import zelongames.travelarm_clock.R;

public class AlarmCollectionActivity extends ToolbarCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_collection);

        initializeToolBar("Alarm Collection", R.menu.menu_toolbar_back, true);

        ListView listView = (ListView) findViewById(R.id.alarmCollection);

        ArrayList<Alarm> names = new ArrayList<>();
        names.add(new Alarm("Skolan", "Tomtebodavägen 3A"));
        names.add(new Alarm("apa", "are"));
        names.add(new Alarm("apa", "are"));
        names.add(new Alarm("apa", "are"));
        names.add(new Alarm("apa", "are"));

        AlarmListAdapter adapter = new AlarmListAdapter(this, R.layout.alarm_collection_list_layout, names);
        listView.setAdapter(adapter);
    }
}
