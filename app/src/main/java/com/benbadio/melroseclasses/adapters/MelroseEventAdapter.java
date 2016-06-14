package com.benbadio.melroseclasses.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.benbadio.melroseclasses.EventTypes;
import com.benbadio.melroseclasses.R;
import com.benbadio.melroseclasses.models.MelroseEvent;

import java.util.List;

/**
 * Created by benba on 6/9/2016.
 */
public class MelroseEventAdapter extends BindableAdapter<MelroseEvent> {

    public MelroseEventAdapter(Context context, List<MelroseEvent> eventList) {
        super(context, eventList);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.list_item_event, container, false);
    }

    @Override
    public void bindView(MelroseEvent item, int position, View view) {
        TextView title = (TextView) view.findViewById(R.id.event_title);
        title.setText(item.getTitle());
        setTitleColor(item, title);

        TextView description = (TextView) view.findViewById(R.id.event_description);
        description.setText(Html.fromHtml(item.getDescription()));
    }

    private void setTitleColor(MelroseEvent event, TextView title) {
        switch (event.getPrimaryEventType()) {
            case EventTypes.AUDIO:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorAudio));
                break;
            case EventTypes.VIDEO:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorVideo));
                break;
            case EventTypes.PHOTO:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorPhoto));
                break;
            case EventTypes.FABLAB:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorFablab));
                break;
            case EventTypes.SIMULATOR:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorSimulators));
                break;
            case EventTypes.BASIC_TRAINING:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorBasicTraining));
                break;
            case EventTypes.OTHER:
                title.setBackgroundColor(title.getResources().getColor(R.color.colorOther));
                break;

        }
    }


}