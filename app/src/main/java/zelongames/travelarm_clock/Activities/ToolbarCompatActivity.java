package zelongames.travelarm_clock.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import zelongames.travelarm_clock.R;

public class ToolbarCompatActivity extends AppCompatActivity {

    private int menuID = 0;
    protected Toolbar toolbar = null;

    protected void initializeToolBar(String title, int menuID){
        this.menuID = menuID;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuID, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.alarm:
                Intent alarmCollectionIntent = new Intent(this, AlarmCollectionActivity.class);
                startActivity(alarmCollectionIntent);
                break;
            case R.id.back:
                Intent previousIntent = new Intent(this, AlarmCollectionActivity.class);
                startActivity(previousIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
