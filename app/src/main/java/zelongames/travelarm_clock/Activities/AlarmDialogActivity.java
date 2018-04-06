package zelongames.travelarm_clock.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import zelongames.travelarm_clock.DialogHelper;

public class AlarmDialogActivity extends ToolbarCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder dialogBuilder = DialogHelper.createDialogBuilder(this, "Alarm", "Wake up!");

        dialogBuilder.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ringtone.stop();
                dialog.dismiss();
                //enabled = false;
                //ringtone = null;
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
