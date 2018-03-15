package zelongames.travelarm_clock.Activities;

import android.os.Bundle;

import zelongames.travelarm_clock.R;

public class AlarmCollectionActivity extends ToolbarCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_collection);

        initializeToolBar("Alarm Collection", R.menu.menu_toolbar_back, true);
    }
}
