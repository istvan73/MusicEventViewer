package com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers;

import com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL_5548 on 12/30/2017.
 *
 * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible for pushing a new band class to the database({@link FirebaseDatabase}).</li>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link BandManager}, add the {@link DataPackBand} as parameter.</li>
 * <li>If you want to send the newly band created, then simply call on the instance the PushDataToFirebase() method. This method returns the result of the pushing, if it was successful, then returns true, otherwise false.</li>
 * </ul>
 */

public class BandManager {
    private DatabaseReference mDatabaseRef;
    private DataPackBand mBand;
    private String M_NODE_EVENT;

    public BandManager(DatabaseReference fbDatabase, String NODE_EVENT) {
        this.mDatabaseRef = fbDatabase;
        this.M_NODE_EVENT = NODE_EVENT;
    }

    public BandManager SetNewEvent(DataPackBand newBand){
        this.mBand = newBand;
        return this;
    }

    public boolean PushDataToFirebase() {

        boolean uploadResult = mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).push().setValue(mBand).isSuccessful();

        return uploadResult;
    }
}
