package com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers;

import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by DELL_5548 on 12/30/2017.
 */

public class EventManager {
    private DatabaseReference mDatabaseRef;
    private EventModel mEvent;
    private String M_NODE_EVENT;

    public EventManager(DatabaseReference fbDatabase, String NODE_EVENT) {
        this.mDatabaseRef = fbDatabase;
        this.M_NODE_EVENT = NODE_EVENT;
    }

    public ArrayList<EventModel> listEvents(){
        final ArrayList<EventModel> eventList = new ArrayList<>();
        mDatabaseRef.child(M_NODE_EVENT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child:children) {
                    EventModel value = child.getValue(EventModel.class);
                    eventList.add(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return eventList;
    }

    public EventManager SetNewEvent(EventModel newEvent){
        this.mEvent = newEvent;
        return this;
    }

    public boolean PushDataToFirebase() {

        boolean uploadResult = mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).push().setValue(mEvent).isSuccessful();

        return uploadResult;
    }


}
