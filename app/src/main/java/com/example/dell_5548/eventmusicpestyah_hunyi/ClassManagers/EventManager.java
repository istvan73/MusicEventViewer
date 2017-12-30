package com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers;

import com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL_5548 on 12/30/2017.
 */

public class EventManager {
    private DatabaseReference mDatabaseRef;
    private DataPackEvent mEvent;
    private String M_NODE_EVENT;

    public EventManager(DatabaseReference fbDatabase, String NODE_EVENT) {
        this.mDatabaseRef = fbDatabase;
        this.M_NODE_EVENT = NODE_EVENT;
    }

    public EventManager SetNewEvent(DataPackEvent newEvent){
        this.mEvent = newEvent;
        return this;
    }

    public boolean PushDataToFirebase() {

        boolean uploadResult = mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).push().setValue(mEvent).isSuccessful();

        return uploadResult;
    }
}
