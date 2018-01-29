package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.example.dell_5548.eventmusicpestyah_hunyi.Validator.MusicEventValidator;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditEventActivity extends AppCompatActivity {

    private final int EDIT_EVENT_REQUEST_CODE = 0;
    private final String EVENT_UPDATED = "EVENT_UPDATED";
    private final String EVENT_KEY = "EVENT_KEY";
    private final String ERROR_CODE = "ERROR_CODE";
    private static int RESULT_LOAD_IMAGE = 2;
    private String M_NODE_EVENT;
    private String M_NODE_USER_EVENTS;
    private String imageExtension = null;
    private Uri imageUri = null;
    private String eventKey;
    private Context ctx = this;
    private EventModel oldModel;

    //design components
    private EditText mEventName;
    private EditText mEventType;
    private EditText mEventDate;
    private EditText mEventLoc;
    private EditText mEventTime;
    private EditText mEventDesc;
    private ImageView mEventImage;


    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    StorageReference mStorageRef;


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
        mEventName = (EditText) findViewById(R.id.updateEventName);
        mEventType = (EditText) findViewById(R.id.updateEventType);
        mEventDate = (EditText) findViewById(R.id.updateEventDate);
        mEventLoc = (EditText) findViewById(R.id.updateEventLocation);
        mEventTime = (EditText) findViewById(R.id.updateEventTime);
        mEventDesc = (EditText) findViewById(R.id.updateEventDescription);
        mEventImage = (ImageView) findViewById(R.id.updateEventImageView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFirebase();
        setOthers();
        setContainers();


        eventKey = getTextFromBundle(EVENT_KEY,savedInstanceState);
        if (eventKey.equals("")){
            Intent returnIntent = new Intent();
            //If an error occured, an error code is thrown back
            returnIntent.putExtra(ERROR_CODE,1);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }

        Button uploadImageButton = (Button) findViewById(R.id.uploadUpdateImageButton);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });



        DatabaseReference mEventReference = mDatabaseRef.child(M_NODE_EVENT).child(eventKey);
        mEventReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oldModel = dataSnapshot.getValue(EventModel.class);
                mEventName.setText(oldModel.getName());
                mEventType.setText(oldModel.getType());
                mEventLoc.setText(oldModel.getLocationName());
                mEventDate.setText(oldModel.getDate());
                mEventTime.setText(oldModel.getTime());
                mEventDesc.setText(oldModel.getDescription());

                if (oldModel.getImagePath()!= null) {
                    StorageReference imageRef = mStorageRef.child(oldModel.getImagePath());
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



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (updateEvent()){
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EVENT_UPDATED,true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{

            }


                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>From a bundle, or saved instance state, this method will get the text and return it</li>
     *     <li>Only works with strings</li>
     * </ul>
     * @param key
     * @param savedInstanceState
     * @return
     */
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

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>This method takes all the values from the fields, checks their correctness via {@link MusicEventValidator}, and pushes them to database</li>
     *     <li>It also saves the to the user's own events</li>
     *     <li>If the user sets an image, the it will be stored in the firebase storage</li>
     *     <li>If the user replaces an old image, that one is going to be erased from the firebase storage</li>
     * </ul>
     * @return
     */
    private boolean updateEvent() {

        String eventName = mEventName.getText().toString().trim();
        String eventType = mEventType.getText().toString().trim();
        String eventLocation = mEventLoc.getText().toString().trim();
        String eventDate = mEventDate.getText().toString().trim();
        String eventTime = mEventTime.getText().toString().trim();
        String eventDesc = mEventDesc.getText().toString().trim();

        String eventAddedBy = oldModel.getCreatorId();
        if (mFirebaseAuth.getCurrentUser()==null){
            Toast.makeText(this, "User is disconneted!", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Validation of input
        MusicEventValidator validator = new MusicEventValidator(ctx);
        if(!validator.validateEvent(eventName,eventType,eventLocation,eventDate,eventTime,eventDesc)){
            return false;
        }

        String imagePath = "";
        if (imageUri != null &&
                imageExtension != null)
        {

            String uniqueFileName = UUID.randomUUID().toString().replaceAll("-", "_");
            imagePath =  imagePath.concat("images/eventImages/" + uniqueFileName + "." + imageExtension);

            saveImageToFirebase(imagePath);
            StorageReference mImageRef =  mStorageRef.child(oldModel.getImagePath());

            mImageRef.delete();
        }else {
            imagePath = oldModel.getImagePath()==null?"":oldModel.getImagePath();

        }

        // This is the new method for pushing an event to the FireBase - Database
        EventModel eventModel = new EventModel.EventModelBuilder(eventAddedBy)
                        .Name(eventName)
                        .Type(eventType)
                        .LocationName(eventLocation)
                        .Time(eventTime)
                        .Date(eventDate)
                        .ImagePath(imagePath)
                        .Description(eventDesc)
                        .build();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String,Object> updateMap = new HashMap<>();
        updateMap.put("/" + M_NODE_EVENT + "/" + eventKey,eventModel);
        updateMap.put("/" + M_NODE_USER_EVENTS + "/" + eventAddedBy + "/" + eventKey,eventModel);

        databaseReference.updateChildren(updateMap);

        return true;
    }


    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>This function pushes an image from the device to the firebase database</li>
     * </ul>
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
                        Glide.with(ctx)
                                .load(imageUri)
                                .override(250,175)
                                .into(mEventImage);
                    }
                }
            } else {
                Toast.makeText(this, "File was not loaded!", Toast.LENGTH_SHORT).show();
            }


        }

    }

}
