package com.example.dell_5548.eventmusicpestyah_hunyi;

import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mUploadDataBT;
    private Button mLoginBT;
    private EditText mEmailET;
    private EditText mPasswordET;

    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        RelativeLayout layout = findViewById(R.id.registerRL);

        mUploadDataBT = (Button) findViewById(R.id.upload_dataBT);
        
        mFirebaseAuth.signInWithEmailAndPassword("testhun@email.com", "testHun");
        mUploadDataBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setValue() in this way overwrites data at the specified location,
                //  including any child nodes. However, you can still update a child without rewriting the entire object.

                //boolean uploadResult = mDatabaseRef.getDatabase().getReference(M_NODE_EVENT).push().setValue(mEvent).isSuccessful();

                // Get a reference to our posts
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events");


                //Get datasnapshot at your "users" root node
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                ArrayList<Map<String,DataPackEvent>> event = collectAllEvent((Map<String, Object>) dataSnapshot.getValue());

                                // DO ANYTHING WHAT YOU WANT WITH ALL THESE DataPackEvents
                                Log.i("DATA-RETRIEVE", "DATA retrieve is DONE");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
            }
        });

    }

    /** !!!!!!#####___FIRST => YOU HAVE TO FILL THE IMAGE-URL FIELD TOO, TO BE COMPLETE THIS List of Class, after that delete this line.!!!
     *
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method downloads/pulls all the data from the specified Node <b>Events</b>.</li>
     * <li>Setting values as {@link HashMap} values, like {@link String} and {@link DataPackEvent} pairs.
     * </ul>
     * <br>
     * @param events through this instance the method gets all the data from the database, where from we need to extract the data into readable data
     *
     *  @return the method returns all the extracted datas from the {@link FirebaseDatabase}
     * */
    private ArrayList<Map<String,DataPackEvent>> collectAllEvent(Map<String, Object> events) {

        ArrayList<Map<String,DataPackEvent>> eventList = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : events.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get all field and append to list after we built the event
            DataPackEvent event = new DataPackEvent.DataPackEventBuilder((String) singleUser.get("creatorId"))
                    .Name((String) singleUser.get("name"))
                    .Date((String) singleUser.get("date"))
                    .LocationName((String) singleUser.get("locationName"))
                    .Time((String) singleUser.get("time"))
                    .Type((String) singleUser.get("type"))
                    .build();

            Map pack = new HashMap();
            pack.put(entry.getKey(),event);
            eventList.add(pack);
        }

        // System.out.println(eventList.get(0));

        return eventList;
    }

    public void onClick(View view) {
        Log.i("SIGN", "IM IN REGISTER-USER");
        Log.i("SI", "IM IN REGISTER-USER");

        if (view == mLoginBT) {
            loginUser();
        }
    }

    private void loginUser() {
        Log.i("SIGN", "IM IN REGISTER-USER");
        String emailStr = mEmailET.getText().toString().trim();
        String passwordStr = mPasswordET.getText().toString().trim();

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

        mFirebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // successfull registering
                            Toast.makeText(LoginActivity.this, "Success ;)!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed, please try again(later) -.-", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
    }
}
