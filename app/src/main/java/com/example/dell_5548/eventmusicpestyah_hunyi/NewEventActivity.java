package com.example.dell_5548.eventmusicpestyah_hunyi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.UUID;


public class NewEventActivity extends AppCompatActivity {

//    Buttons
    private Button createEventButton;
    private Button cancelCreatorButton;
    private Button uploadImageButton;

//    EditTexts
    private EditText eventNameET;
    private EditText eventTypeET;
    private EditText eventLocationET;
    private EditText eventDateET;
    private EditText eventTimeET;

//    Labels
    private TextView imageNameLabel;

//    Firebase
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


//    Others
    private Context ctx = this;
    private static int RESULT_LOAD_IMAGE = 2;
    private String imageExtension = null;
    private Uri imageUri = null;
    String M_NODE_EVENT;
    MusicEventValidator myValidator;
    private final String NEW_EVENT_CREATED = "NEW_EVENT_CREATED";
    private ProgressBar progressBar;

    private void setButtons(){
        createEventButton = (Button) findViewById(R.id.createButton);
        cancelCreatorButton = (Button) findViewById(R.id.cancelButton);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);

    }

    private void setEditTexts(){

        //Creating references for EditText views

        eventNameET = (EditText) findViewById(R.id.eventName);
        eventTypeET = (EditText) findViewById(R.id.eventType);
        eventLocationET = (EditText) findViewById(R.id.eventLocation);
        eventDateET = (EditText) findViewById(R.id.eventDate);
        eventTimeET = (EditText) findViewById(R.id.eventTime);
    }

    private void setLabels(){
        imageNameLabel = (TextView) findViewById(R.id.uploadedImageName);
    }

    private void setFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void setOthers(){
        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);
        myValidator = new MusicEventValidator(ctx);
    }

    private void setupProgressBar(){
        ConstraintLayout layout = findViewById(R.id.new_event_constraint_layout);
        progressBar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);
    }


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        setFirebase();
        setButtons();
        setEditTexts();
        setLabels();
        setOthers();

        setupProgressBar();


        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createTheEvent()){
                    progressBar.setVisibility(View.GONE);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(NEW_EVENT_CREATED,true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        cancelCreatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnItent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnItent);
                finish();
            }
        });

    }


    /**
     * <h2>Description:</h2><br>
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

        if(!validateEvent(eventName,eventType,eventLocation,eventDate,eventTime)){
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

        return true;
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method is the one responsible for validating the user's input.</li>
     *     <li>It uses the {@link MusicEventValidator}.</li>
     *     <li>In case the validation fails, it will return false, and also will display the first error in a toast.</li>
     * </ul>
     *
     * @param eventName
     * @param eventType
     * @param eventLocation
     * @param eventDate
     * @param eventTime
     * @return
     */
    public boolean validateEvent(String eventName, String eventType, String eventLocation, String eventDate, String eventTime){
        if (
                !myValidator.isValidSimpleString("Event Name",eventName) ||
                        !myValidator.isValidSimpleString("Event Type",eventType) ||
                        !myValidator.isValidSimpleString("Event Location",eventLocation) ||
                        !myValidator.isValidDate(eventDate) ||
                        !myValidator.isValidTime(eventTime)
                ){
            return false;
        }
        return true;
    }


    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method is the one responsible for uploading the event's image to the Firebase Cloud Storage.</li>
     *     <li>If the upload is not successful, it will signal to the user via a {@link Toast}</li>
     * </ul>
     *
     * @param imagePath
     */
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
