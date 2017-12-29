package com.example.dell_5548.eventmusicpestyah_hunyi;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.Validator.MusicEventValidator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewEventActivity extends AppCompatActivity {

    //Defining Keys for Json element
    String M_NODE_EVENT;
    String M_EVENT_NAME;
    String M_EVENT_ADDER_ID;
    String M_EVENT_LOCATION_NAME;
    String M_EVENT_TYPE;
    String M_EVENT_DATE;
    String M_EVENT_TIME;


    //Defining Keys for Json element - finished


    private Button createEventButton;
    private Button cancelCreatorButton;
    private EditText eventNameET;
    private EditText eventTypeET;
    private EditText eventLocationET;
    private EditText eventDateET;
    private EditText eventTimeET;

    private FirebaseAuth mFirebaseAuth;
    //private StorageReference mStorageRef;
    //private PrivateStorageRefence mPrivStorageRef;
    private DatabaseReference mDatabaseRef;
    private ProgressBar progressBar;

    private Context ctx = this;
    MusicEventValidator myValidator;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        myValidator = new MusicEventValidator(ctx);
        //Setting up Keys for Json element

        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);
        M_EVENT_NAME = getResources().getString(R.string.M_EVENT_NAME);
        M_EVENT_ADDER_ID = getResources().getString(R.string.M_EVENT_ID);
        M_EVENT_LOCATION_NAME = getResources().getString(R.string.M_EVENT_LOCATION_NAME);
        M_EVENT_TYPE = getResources().getString(R.string.M_EVENT_TYPE);
        M_EVENT_DATE = getResources().getString(R.string.M_EVENT_DATE);
        M_EVENT_TIME = getResources().getString(R.string.M_EVENT_TIME);

        //Setting up Keys for Json element - finished

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword("testhun@email.com","testHun");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();



        ConstraintLayout layout = findViewById(R.id.new_event_constraint_layout);

        createEventButton = (Button) findViewById(R.id.createButton);
        cancelCreatorButton = (Button) findViewById(R.id.cancelButton);

        //Creating references for EditText views

        eventNameET = (EditText) findViewById(R.id.eventName);
        eventTypeET = (EditText) findViewById(R.id.eventType);
        eventLocationET = (EditText) findViewById(R.id.eventLocation);
        eventDateET = (EditText) findViewById(R.id.eventDate);
        eventTimeET = (EditText) findViewById(R.id.eventTime);


        //Creating references for EditText views - finished

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                if (createTheEvent()){
                    Toast.makeText(ctx, "Yeees.", Toast.LENGTH_SHORT).show();

                }else{

                }
                // after that, Istvan, you need to give back or just only to close this activity \
                // and also if its possible then say the main activity that he has to refresh itself bc. of new data...
            }
        });

        cancelCreatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar

    }

    /**<h2>Description:</h2><br>
     * <ul>
     *     <li>This method is the one called when the <b>create</b> button is pressed.</li>
     *     <li>It takes the data from the current layout, and after validation it saves the data to the database.</li>
     *     <li>In case the validation fails, it will be returned, and also will display the first error in a toast.</li>
     * </ul>
     */
    private boolean createTheEvent() {
        // Istvan, ive to tell you smth, you have to work in plus, to repair this fnc.
        Log.i("SIGN", "IM IN CREATE EVENT");
        // Here u have to get all the datas from the UI


        String eventName = eventNameET.getText().toString().trim();
        String eventType = eventTypeET.getText().toString().trim();
        String eventLocation = eventLocationET.getText().toString().trim();
        String eventDate = eventDateET.getText().toString().trim();
        String eventTime = eventTimeET.getText().toString().trim();


        String eventAddedBy = mFirebaseAuth.getCurrentUser().getUid();
        if (mFirebaseAuth.getCurrentUser()==null){
            Toast.makeText(this, "User is disconneted!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (
                !myValidator.isValidSimpleString("Event Name",eventName) ||
                        !myValidator.isValidSimpleString("Event Type",eventType) ||
                        !myValidator.isValidSimpleString("Event Location",eventLocation) ||
                        !myValidator.isValidDate(eventDate) ||
                        !myValidator.isValidTime(eventTime)
                ){
            return false;
        }


        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar

        // saving datas to the FIREBASE

        // Creating event with a title and an ID

        // Put the local variables in a hashMap
        Map newEventDatas = new HashMap();
        newEventDatas.put(M_EVENT_NAME, eventName);
        newEventDatas.put(M_EVENT_TYPE, eventType);
        newEventDatas.put(M_EVENT_LOCATION_NAME, eventLocation);
        newEventDatas.put(M_EVENT_TIME, eventTime);
        newEventDatas.put(M_EVENT_DATE, eventDate);
        newEventDatas.put(M_EVENT_ADDER_ID, eventAddedBy);


        // Send this variable collection to the Server =>
        //FirebaseDatabase.getInstance().getReference("Events").getReference
            // if we want that the new event name to be created as a UniqID =>
                // Object eventID = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
                // mDatabaseRef.child(M_NODE_EVENT).setValue(eventID);
                // mDatabaseRef.child(M_NODE_EVENT).getDatabase().getReference(eventID).setValue(newEventDatas);

            // but if we want that the new event to be created as a name =>
        //mDatabaseRef.child(M_NODE_EVENT).child(eventName);
        mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).push().setValue(newEventDatas);
        //events.getDatabase().getReference("")

        /*** Instructions:
         * 1. mDatabaseRef = FirebaseDatabase.getinst.getref event.cid = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
         * 2. child event. child userList
         * 3. firabeseStorage.getinst.getref
         * 4. kep: storageRef.child foto . child uri . getLastPathFragmeent
         * @param
         * @return
         *
         * */

        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Toast.makeText(this, "Event creates successfully!", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
            // To Hide ProgressBar
        return true;
    }
}
