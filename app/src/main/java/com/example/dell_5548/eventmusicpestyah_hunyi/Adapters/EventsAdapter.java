package com.example.dell_5548.eventmusicpestyah_hunyi.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pista on 2018.01.02..
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder>
implements Filterable{
    private List<EventModel> eventList;
    private List<EventModel> eventListFiltered;
    private Context ctx;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageRef = storage.getReference();
    private EventAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventType, eventLocation,eventDate,eventTime;
        public ImageView eventImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_card_name);
            eventType = (TextView) itemView.findViewById(R.id.event_card_type);
            eventLocation = (TextView) itemView.findViewById(R.id.event_card_location);
            eventDate = (TextView) itemView.findViewById(R.id.event_card_date);
            eventTime = (TextView) itemView.findViewById(R.id.event_card_time);

            eventImage = (ImageView) itemView.findViewById(R.id.event_card_image_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEventSelected(eventListFiltered.get(getAdapterPosition()));
                }
            });

        }
    }

    public EventsAdapter(final Context ctx, List<EventModel> eventList, EventAdapterListener listener){
        this.eventList = eventList;
        this.eventListFiltered = eventList;
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_row,parent,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, int position) {
        EventModel eventModel = eventListFiltered.get(position);
        holder.eventName.setText(eventModel.getName());
        holder.eventType.setText(eventModel.getType());
        holder.eventDate.setText(eventModel.getDate());
        holder.eventTime.setText(eventModel.getTime());
        holder.eventLocation.setText(eventModel.getLocationName());
        if (eventModel.getImagePath()!= null) {
            StorageReference imageRef = mStorageRef.child(eventModel.getImagePath());
            Glide.with(ctx)
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .placeholder(R.drawable.no_image_available)
                    .override(85,85)
                    .fitCenter()
                    .into(holder.eventImage);

        }else{
            Glide.with(ctx)
                    .load(R.drawable.no_image_available)
                    .override(85,85)
                    .fitCenter()
                    .into(holder.eventImage);
        }
    }

    @Override
    public int getItemCount() {
        return eventListFiltered.size();
    }

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>This method is the one called when the adapter's filter function is.</li>
     *     <li>It returns a newly created {@link Filter}, which will be used to filter items in the recycler view.</li>
     * </ul>
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    eventListFiltered = eventList;
                }else{
                    List<EventModel> filteredList = new ArrayList<>();
                    for(EventModel row : eventList){
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getType().contains(charSequence)){
                            filteredList.add(row);
                        }
                    }
                    eventListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = eventListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventListFiltered = (ArrayList<EventModel>) filterResults.values;
                notifyDataSetChanged();
            }


        };
    }
}

