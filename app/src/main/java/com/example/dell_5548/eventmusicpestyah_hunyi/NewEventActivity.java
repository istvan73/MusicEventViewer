package com.example.dell_5548.eventmusicpestyah_hunyi;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {

    private static final String M_NODE_EVENT = "Events";
    private static final String M_EVENT_NAME = "name";
    private static final String M_EVENT_ID = "ID";
    private static final String M_EVENT_LOCATION_NAME = "location_name";
    private static final String M_EVENT_TYPE = "type";
    private static final String M_EVENT_TIME = "time";

    private Button mCreateBT;
    private Button mCloseBT;
    private EditText mEmailET;
    private EditText mPasswordET;

    private FirebaseAuth mFirebaseAuth;
    //private StorageReference mStorageRef;
    //private PrivateStorageRefence mPrivStorageRef;
    private DatabaseReference mDatabaseRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newevent);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword("testhun@email.com","testHun");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        RelativeLayout layout = findViewById(R.id.registerRL);
        mCreateBT = (Button) findViewById(R.id.createBT);
        mEmailET = (EditText) findViewById(R.id.emailET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);

        mCreateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                createTheEvent();
                // after that, Istvan, you need to give back or just only to close this activity \
                //  and also if its possible then say the main activity that he has to refresh itself bc. of new data...
            }
        });

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
    }

    private void createTheEvent() {
        // Istvan, ive to tell you smth, you have to work in plus, to repair this fnc.
        Log.i("SIGN", "IM IN CREATE EVENT");
        // Here u have to get all the datas from the UI
        String emailStr = mEmailET.getText().toString().trim();
        String passwordStr = mPasswordET.getText().toString();
        String eventName = "Piano opera";

        Log.i("SIGN", "Email: " + emailStr + "|pass:" + passwordStr);

        if (TextUtils.isEmpty(emailStr)) {
            // the email field is empty
            Toast.makeText(this, "Please fill in the Email field.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            // the email field is empty
            Toast.makeText(this, "Please fill in the Password field.", Toast.LENGTH_SHORT).show();
            return;
        }
        // everything it's okay

        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar

        // saving datas to the FIREBASE
        String userID = mFirebaseAuth.getCurrentUser().getUid();
        // Creating event with a title and an ID

        // Put the local variables in a hashMap
        Map newEventDatas = new HashMap();
        newEventDatas.put(M_EVENT_NAME, eventName);
        newEventDatas.put(M_EVENT_TYPE, "piano");
        newEventDatas.put(M_EVENT_LOCATION_NAME, "tudor");
        newEventDatas.put(M_EVENT_TIME, "15:15:000");
        newEventDatas.put(M_EVENT_ID, "UNIQ_ID");
        // Send this variable collection to the Server =>
        //FirebaseDatabase.getInstance().getReference("Events").getReference
            // if we want that the new event name to be created as a UniqID =>
                // Object eventID = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
                // mDatabaseRef.child(M_NODE_EVENT).setValue(eventID);
                // mDatabaseRef.child(M_NODE_EVENT).getDatabase().getReference(eventID).setValue(newEventDatas);

            // but if we want that the new event to be created as a name =>
        //mDatabaseRef.child(M_NODE_EVENT).child(eventName);
        mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).child(eventName).setValue(newEventDatas);
        //events.getDatabase().getReference("")

        /*** Instructions:
         * 1. mDatabaseRef = FirebaseDatabase.getinst.getref event.cid = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
         * 2. child event. child userList
         * 3. firabeseStorage.getinst.getref
         * 4. kep: storageRef.child foto . child uri . getLastPathFragmeent
         * */

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
    }
}
