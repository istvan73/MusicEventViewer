package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Activities.EditEventActivity;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowEventActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventType;
    private TextView mEventDate;
    private TextView mEventLoc;
    private TextView mEventTime;
    private ImageView mEventImage;

    private final int EDIT_EVENT_REQUEST_CODE = 0;
    private final String EVENT_KEY = "EVENT_KEY";
    private final String ERROR_CODE = "ERROR_CODE";
    private String M_NODE_EVENT;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


    private String eventKey;
    private Context ctx = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);



        mEventName = (TextView) findViewById(R.id.showEventName);
        mEventType = (TextView) findViewById(R.id.showEventTypeTextfield);
        mEventDate = (TextView) findViewById(R.id.showEventDateTextfield);
        mEventLoc = (TextView) findViewById(R.id.showEventLocationTextfield);
        mEventTime = (TextView) findViewById(R.id.showEventTimeTextfield);
        mEventImage = (ImageView) findViewById(R.id.showEventImageView);

        FloatingActionButton edit_fab = (FloatingActionButton) findViewById(R.id.show_event_edit_button);

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editEventIntent = new Intent(ctx, EditEventActivity.class);
                startActivityForResult(editEventIntent, EDIT_EVENT_REQUEST_CODE);
            }
        });


        // Setting

        eventKey = getTextFromBundle(EVENT_KEY,savedInstanceState);
        if (eventKey.equals("")){
            Intent returnIntent = new Intent();
            //If an error occured, an error code is thrown back
            returnIntent.putExtra(ERROR_CODE,1);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }

        DatabaseReference mEventReference = FirebaseDatabase.getInstance().getReference().child(M_NODE_EVENT).child(eventKey);
        mEventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventModel eventModel = dataSnapshot.getValue(EventModel.class);
                mEventName.setText(eventModel.getName());
                mEventType.setText(eventModel.getType());
                mEventLoc.setText(eventModel.getLocationName());
                mEventDate.setText(eventModel.getDate());
                mEventTime.setText(eventModel.getTime());
                Toast.makeText(ctx,eventModel.getImagePath(), Toast.LENGTH_LONG).show();

                if (eventModel.getImagePath()!= null) {
                    StorageReference imageRef = mStorageRef.child(eventModel.getImagePath());
                    Glide.with(ctx)
                            .using(new FirebaseImageLoader())
                            .load(imageRef)
                            .placeholder(R.drawable.no_image_available)
                            .override(350,275)
                            .fitCenter()
                            .into(mEventImage);

                }else{
                    Glide.with(ctx)
                            .load(R.drawable.no_image_available)
                            .override(350,275)
                            .fitCenter()
                            .into(mEventImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

}


