package com.example.dell_5548.eventmusicpestyah_hunyi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.EventManager;
import com.example.dell_5548.eventmusicpestyah_hunyi.DatabaseClasses.DataPackEvent;
import com.example.dell_5548.eventmusicpestyah_hunyi.Validator.MusicEventValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 2;
    private String imageExtension = null;
    private Uri imageUri = null;

    //Defining Keys for Json element
    String M_NODE_EVENT;
    //szemet
    /*String M_EVENT_NAME;
    String M_EVENT_ADDER_ID;
    String M_EVENT_LOCATION_NAME;
    String M_EVENT_TYPE;
    String M_EVENT_DATE;
    String M_EVENT_TIME;
    String M_EVENT_FILE_PATH;
*/

    //Defining Keys for Json element - finished


    private Button createEventButton;
    private Button cancelCreatorButton;
    private Button uploadImageButton;

    private EditText eventNameET;
    private EditText eventTypeET;
    private EditText eventLocationET;
    private EditText eventDateET;
    private EditText eventTimeET;

    private TextView imageNameLabel;

    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageRef;
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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInWithEmailAndPassword("testhun@email.com","testHun");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ConstraintLayout layout = findViewById(R.id.new_event_constraint_layout);

        createEventButton = (Button) findViewById(R.id.createButton);
        cancelCreatorButton = (Button) findViewById(R.id.cancelButton);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);

        //Creating references for EditText views

        eventNameET = (EditText) findViewById(R.id.eventName);
        eventTypeET = (EditText) findViewById(R.id.eventType);
        eventLocationET = (EditText) findViewById(R.id.eventLocation);
        eventDateET = (EditText) findViewById(R.id.eventDate);
        eventTimeET = (EditText) findViewById(R.id.eventTime);


        imageNameLabel = (TextView) findViewById(R.id.uploadedImageName);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });


        //Creating references for EditText views - finished

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                if (createTheEvent()){
                    Toast.makeText(ctx, "Yeees.", Toast.LENGTH_SHORT).show();

                }else{

                }
                progressBar.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.GONE);
    }

    /**<h2>Description:</h2><br>
     * <ul>
     *     <li>This method is the one called when the <b>create</b> button is pressed.</li>
     *     <li>It takes the data from the current layout, and after validation it saves the data to the database.</li>
     *     <li>In case the validation fails, it will be returned, and also will display the first error in a toast.</li>
     * </ul>
     */
    private boolean createTheEvent() {

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
        //Validation of input

        if (
                !myValidator.isValidSimpleString("Event Name",eventName) ||
                        !myValidator.isValidSimpleString("Event Type",eventType) ||
                        !myValidator.isValidSimpleString("Event Location",eventLocation) ||
                        !myValidator.isValidDate(eventDate) ||
                        !myValidator.isValidTime(eventTime)
                ){
            return false;
        }
        String imagePath = "";
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        if (imageUri != null &&
                imageExtension != null)
        {

            String uniqueFileName = UUID.randomUUID().toString().replaceAll("-", "_");
            imagePath =  imagePath.concat("images/eventImages/" + uniqueFileName + "." + imageExtension);

             saveImageToFirebase(imagePath);
        }

        // This is the new method for pushing an event to the FireBase - Database
        EventManager eventManager = new EventManager(mDatabaseRef,M_NODE_EVENT)
                .SetNewEvent(new DataPackEvent
                        .DataPackEventBuilder(eventAddedBy)
                        .Name(eventName)
                        .Type(eventType)
                        .LocationName(eventLocation)
                        .Time(eventTime)
                        .Date(eventDate)
                        .ImagePath(imagePath)
                        .build());

        eventManager.PushDataToFirebase();

        Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();

        return true;
    }


    private void saveImageToFirebase(String imagePath){
        StorageReference eventImageRef = mStorageRef.child(imagePath);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();


        UploadTask uploadTask = eventImageRef.putFile(imageUri,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ctx,"We were unable to upload your file.",Toast.LENGTH_LONG).show();
                Log.i("ERR",e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });

    }


    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>When the Request code is RESULT_LOAD_IMAGE, it fetches the image's data into the imageUri variable.</li>
     *     <li>The image's extension will be stored in the imageExtension variable.</li>
     * </ul>
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE) {

            if (resultCode == RESULT_OK && null != data) {
                imageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();


                if (picturePath != null) {
                    int cut = picturePath.lastIndexOf('.');
                    if (cut != -1) {
                        imageExtension = picturePath.substring(cut + 1);
                        imageNameLabel.setText("1 image loaded");
                    }
                }
            } else {
                Toast.makeText(this, "File was not loaded!", Toast.LENGTH_SHORT).show();
            }


        }

    }
}


/**
 * package com.example.dell_5548.extramodules;

 import android.app.Dialog;
 import android.content.DialogInterface;
 import android.support.v7.app.AlertDialog;
 import android.support.v7.app.AppCompatActivity;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.LinearLayout;

 import java.lang.reflect.Array;
 import java.util.Arrays;
 import java.util.List;

 public class MainActivity extends AppCompatActivity {

 private Button mNewBandBT;
 private Button myTestButton;
 private View.OnClickListener myCustomButtonListener;

 /*
 final EditText bandNameET;
 final EditText bandTypeET;
 final EditText bandRefLinkET;
 final EditText bandMembersET;
 final EditText bandMusicsET;
 // itt vege * /

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Find the EditText fields
    final View myCustomView  = LayoutInflater.from(this).inflate(R.layout.activity_band_dialog, null);

    final EditText bandNameET = (EditText) myCustomView.findViewById(R.id.bandDialog_nameET);
    final EditText bandTypeET = (EditText) myCustomView.findViewById(R.id.bandDialog_typeET);
    final EditText bandRefLinkET = (EditText) myCustomView.findViewById(R.id.bandDialog_refLinkET);
    final EditText bandMembersET = (EditText) myCustomView.findViewById(R.id.bandDialog_membersET);
    final EditText bandMusicsET = (EditText) myCustomView.findViewById(R.id.bandDialog_musicsET);

    myTestButton = (Button) myCustomView.findViewById(R.id.bandDialog_createBT);
    Button.OnClickListener myCustomButtonListener = new Button.OnClickListener(){

        @Override
        public void onClick(View view) {
            Log.i("RESULT", "Result="+ bandNameET.getText().toString());
        }
    };
    myTestButton.setOnClickListener(myCustomButtonListener);

    mNewBandBT = (Button) findViewById(R.id.create_bandBT);
    mNewBandBT.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // Set the dialog title
            builder.setTitle(R.string.bandDialog_app_name);
            builder.setView(R.layout.activity_band_dialog);



                        /*.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                String bandName = bandNameET.getText().toString();
                                String bandType = bandTypeET.getText().toString();
                                String bandRefLink = bandRefLinkET.getText().toString();
                                List<String> bandMembers = Arrays.asList(bandMembersET.getText().toString().split(","));
                                List<String> bandMusics = Arrays.asList(bandMusicsET.getText().toString().split(","));

                                Log.i("TEXT_S", "EditText field values:"
                                        + bandName + "; "
                                        + bandType + "; "
                                        + bandRefLink + "; "
                                        + bandMembers + "; "
                                        + bandMusics);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
// itt vege * /


Dialog myDialog = builder.create();
            myDialog.show();
        }
    });

}




    private Dialog createBandDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog title
        builder.setTitle(R.string.bandDialog_app_name);
        builder.setView(R.layout.activity_band_dialog)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

/*
                        String bandName = bandNameET.getText().toString();
                        String bandType = bandTypeET.getText().toString();
                        String bandRefLink = bandRefLinkET.getText().toString();
                        List<String> bandMembers = Arrays.asList(bandMembersET.getText().toString().split(","));
                        List<String> bandMusics = Arrays.asList(bandMusicsET.getText().toString().split(","));

                        Log.i("TEXT_S", "EditText field values:"
                                + bandName + "; "
                                + bandType + "; "
                                + bandRefLink + "; "
                                + bandMembers + "; "
                                + bandMusics);
// itt vege * /

}
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
*/