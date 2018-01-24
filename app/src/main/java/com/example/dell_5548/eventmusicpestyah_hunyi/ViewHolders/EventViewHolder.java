package com.example.dell_5548.eventmusicpestyah_hunyi.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;

/**
 * Created by Pista on 2018.01.23..
 */

public class EventViewHolder extends RecyclerView.ViewHolder {
    public TextView eventName, eventType, eventLocation,eventDate,eventTime;
    public ImageView eventImage;

    public EventViewHolder(View itemView) {
        super(itemView);

        eventName = (TextView) itemView.findViewById(R.id.event_card_name);
        eventType = (TextView) itemView.findViewById(R.id.event_card_type);
        eventLocation = (TextView) itemView.findViewById(R.id.event_card_location);
        eventDate = (TextView) itemView.findViewById(R.id.event_card_date);
        eventTime = (TextView) itemView.findViewById(R.id.event_card_time);

        eventImage = (ImageView) itemView.findViewById(R.id.event_card_image_view);

        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEventSelected(eventListFiltered.get(getAdapterPosition()));
            }
        });
*/

    }
}
