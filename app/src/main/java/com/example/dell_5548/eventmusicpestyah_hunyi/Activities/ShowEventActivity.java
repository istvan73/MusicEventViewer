package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Activities.EditEventActivity;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.example.dell_5548.eventmusicpestyah_hunyi.Validator.MusicEventValidator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ShowEventActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventType;
    private TextView mEventDate;
    private TextView mEventLoc;
    private TextView mEventTime;
    private TextView mDescription;
    private ImageView mEventImage;

    private final int EDIT_EVENT_REQUEST_CODE = 0;
    private final String EVENT_KEY = "EVENT_KEY";
    private final String ERROR_CODE = "ERROR_CODE";
    private String M_NODE_EVENT;
    private String M_NODE_USER_EVENTS;

    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mEventReference;

    private String eventKey;
    private Context ctx = this;
    private EventModel eventModel;
    private ValueEventListener myVEL;
    private boolean isUserSubscribed = false;


    private void setFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void setOthers(){
        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);
        M_NODE_USER_EVENTS = getResources().getString(R.string.M_NODE_USER_EVENTS);
    }

    private void setContainers(){
        mEventName = (TextView) findViewById(R.id.showEventName);
        mEventType = (TextView) findViewById(R.id.showEventTypeTextfield);
        mEventDate = (TextView) findViewById(R.id.showEventDateTextfield);
        mEventLoc = (TextView) findViewById(R.id.showEventLocationTextfield);
        mEventTime = (TextView) findViewById(R.id.showEventTimeTextfield);
        mDescription = (TextView) findViewById(R.id.showEventDescription);
        mEventImage = (ImageView) findViewById(R.id.showEventImageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        setOthers();
        setFirebase();
        setContainers();

        FloatingActionButton edit_fab = (FloatingActionButton) findViewById(R.id.show_event_edit_button);
        FloatingActionButton delete_fab = (FloatingActionButton) findViewById(R.id.show_event_delete_button);
        FloatingActionButton subscribe_fab = (FloatingActionButton) findViewById(R.id.show_event_subscribe_button);
        // Setting

        eventKey = getTextFromBundle(EVENT_KEY,savedInstanceState);
        if (eventKey.equals("")){
            Intent returnIntent = new Intent();
            //If an error occured, an error code is thrown back
            returnIntent.putExtra(ERROR_CODE,1);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }

        final DatabaseReference mEventReference = FirebaseDatabase.getInstance().getReference().child(M_NODE_EVENT).child(eventKey);

        myVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventModel = dataSnapshot.getValue(EventModel.class);
                eventModel.setKey(dataSnapshot.getKey());
                mEventName.setText(eventModel.getName());
                mEventType.setText(eventModel.getType());
                mEventLoc.setText(eventModel.getLocationName());
                mEventDate.setText(eventModel.getDate());
                mEventTime.setText(eventModel.getTime());
                mDescription.setText(eventModel.getDescription());

                if (eventModel.getImagePath()!= null) {
                    StorageReference imageRef = mStorageRef.child(eventModel.getImagePath());
                    Glide.with(ctx)
                            .using(new FirebaseImageLoader())
                            .load(imageRef)
                            .placeholder(R.drawable.no_image_available)
                            .override(400,325)
                            .fitCenter()
                            .into(mEventImage);

                }else{
                    Glide.with(ctx)
                            .load(R.drawable.no_image_available)
                            .override(400,325)
                            .fitCenter()
                            .into(mEventImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mEventReference.addValueEventListener(myVEL);

//Floating action button: setting on click listener

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirebaseAuth.getCurrentUser() == null){
                    Toast.makeText(ctx,"You need to log in first!",Toast.LENGTH_SHORT).show();
                } else if (!mFirebaseAuth.getCurrentUser().getUid().equals(eventModel.getCreatorId())){
                    Toast.makeText(ctx,"Only the event's creator may update it!",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent editEventIntent = new Intent(ctx, EditEventActivity.class);
                    editEventIntent.putExtra(EVENT_KEY, eventKey);
                    startActivityForResult(editEventIntent, EDIT_EVENT_REQUEST_CODE);
                }
            }
        });

        delete_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirebaseAuth.getCurrentUser() == null){
                    Toast.makeText(ctx,"You need to log in first!",Toast.LENGTH_SHORT).show();
                } else if (!mFirebaseAuth.getCurrentUser().getUid().equals(eventModel.getCreatorId())){
                    Toast.makeText(ctx,"Only the event's creator may delete it!",Toast.LENGTH_SHORT).show();
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setMessage("Do you really want to delete this event?")
                            .setTitle("Deleting event");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setPositiveButton("Deltete it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mEventReference.removeEventListener(myVEL);
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("/" + M_NODE_EVENT + "/" + eventKey, null);
                            updateMap.put("/" + M_NODE_USER_EVENTS + "/" + eventModel.getCreatorId() + "/" + eventKey, null);
                            if (eventModel.getImagePath() != null) {
                                StorageReference mImageRef = mStorageRef.child(eventModel.getImagePath());
                                mImageRef.delete();

                            }

                            mDatabaseRef.updateChildren(updateMap);


                            dialogInterface.dismiss();
                            finish();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        subscribe_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                if (mFirebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ctx, "You need to be logged in first!", Toast.LENGTH_SHORT).show();
                } else if (StarterActivity.isEarlierThanNow(eventModel.getDate(), eventModel.getTime())) {
                    Toast.makeText(ctx, "The event is already over!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isUserSubscribed) {
                        builder.setMessage("Would you like to unsubscribe to this event?")
                                .setTitle("Unsubscribe event");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        builder.setPositiveButton("Unsubscribe!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (mFirebaseAuth.getCurrentUser() == null) {
                                    Toast.makeText(ctx, "You need to be logged in first!", Toast.LENGTH_SHORT).show();
                                } else {
                                    String unsubscriberUser = mFirebaseAuth.getCurrentUser().getUid();
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("/" + M_NODE_USER_EVENTS + "/" + unsubscriberUser + "/" + eventKey, null);

                                    mDatabaseRef.updateChildren(updateMap);
                                    dialogInterface.dismiss();
                                    Toast.makeText(ctx, "You have succesfully unubscribed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        builder.setMessage("Would you like to subscribe to this event?")
                                .setTitle("Subscribe event");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        builder.setPositiveButton("Subscribe!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                String eventAddedBy = mFirebaseAuth.getCurrentUser().getUid();
                                mDatabaseRef.child(M_NODE_USER_EVENTS).child(eventAddedBy).child(eventModel.getKey()).setValue(eventModel);
                                dialogInterface.dismiss();
                                Toast.makeText(ctx, "You have succesfully subscribed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        if (mFirebaseAuth.getCurrentUser() != null){
            String userId = mFirebaseAuth.getCurrentUser().getUid();

            DatabaseReference mUserEventReference = mDatabaseRef.child(M_NODE_USER_EVENTS).child(userId);
            mUserEventReference.orderByKey().equalTo(eventKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null || dataSnapshot.getChildren()==null){
                        isUserSubscribed = false;
                    } else {
                        isUserSubscribed = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }

    private String getTextFromBundle(String key, Bundle savedInstanceState){
        String textToReturn;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                textToReturn = "";
            } else {
                textToReturn = extras.getString(key);
            }
        } else {
            textToReturn = savedInstanceState.getString(key);
        }
        return textToReturn;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString(EVENT_KEY,eventModel.getKey());
    }
}


