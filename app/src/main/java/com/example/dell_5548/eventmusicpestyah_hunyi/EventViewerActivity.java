package com.example.dell_5548.eventmusicpestyah_hunyi;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventViewerActivity extends AppCompatActivity {


    private TextView mEventName;
    private TextView mEventType;
    private TextView mEventDate;
    private TextView mEventLoc;
    private TextView mEventTime;
    private ImageView mEventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);

        mEventName = (TextView) findViewById(R.id.event_viewer_eventName);
        mEventType = (TextView) findViewById(R.id.event_viewer_eventType);
        mEventDate = (TextView) findViewById(R.id.event_viewer_eventDate);
        mEventLoc = (TextView) findViewById(R.id.event_viewer_eventLocation);
        mEventTime = (TextView) findViewById(R.id.event_viewer_eventTime);
        mEventImage = (ImageView) findViewById(R.id.event_viewer_imageView);


        // Setting
        if (savedInstanceState.containsKey("key1"))
            mEventName.setText(savedInstanceState.getString("key1"));
        if (savedInstanceState.containsKey("key2"))
            mEventType.setText(savedInstanceState.getString("key2"));
        if (savedInstanceState.containsKey("key3"))
            mEventDate.setText(savedInstanceState.getString("key3"));
        if (savedInstanceState.containsKey("key4"))
            mEventLoc.setText(savedInstanceState.getString("key4"));
        if (savedInstanceState.containsKey("key5"))
            mEventTime.setText(savedInstanceState.getString("key5"));
        if (savedInstanceState.containsKey("key6"))
            mEventImage.setImageURI(Uri.parse(savedInstanceState.getString("key6")));
    }
}
