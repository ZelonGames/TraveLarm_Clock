package zelongames.travelarm_clock.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import zelongames.travelarm_clock.Alarm;
import zelongames.travelarm_clock.R;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

    private Context context;
    private int resource = 0;

    private static class ViewHolder {
        TextView name;
        TextView location;
    }

    public AlarmListAdapter(Context context, int resource, ArrayList<Alarm> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(context);
            convertView = inflator.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.alarmName);
            holder.location = convertView.findViewById(R.id.alarmLocation);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        Alarm alarm = getItem(position);
        if (alarm.hasName()) {
            holder.name.setText(alarm.getName());
            holder.location.setText(alarm.getLocationName());
        } else {
            holder.name.setText("...");
            holder.location.setText(alarm.getLocationName());
        }

        return convertView;
    }


}
