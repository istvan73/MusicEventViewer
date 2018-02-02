package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Fragments.DatePickerFragment;
import com.example.dell_5548.eventmusicpestyah_hunyi.Fragments.TimePickerDialog;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        android.app.TimePickerDialog.OnTimeSetListener
{

    //    Buttons
    private FloatingActionButton fab;
    private Button uploadImageButton;
    private Button setDateButton;
    private Button setTimeButton;
    private Button setMapButton;

    //    EditTexts
    private EditText mEventName;
    private EditText mEventType;
    private EditText mEventLocation;
    private EditText mEventDescription;

    private TextView mEventDate;
    private TextView mEventTime;

    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    //    Others
    private Context ctx = this;
    private static int RESULT_LOAD_IMAGE = 2;
    private String imageExtension = null;
    private Uri imageUri = null;
    private String M_NODE_EVENT;
    private String M_NODE_USER_EVENTS;
    private MusicEventValidator myValidator;
    private final String NEW_EVENT_CREATED = "NEW_EVENT_CREATED";
    private ProgressBar progressBar;
    private ImageView mEventImage;
    private final int MAP_LOCATION_REQUEST_CODE = 0;


    private void setButtons(){
        uploadImageButton = (Button) findViewById(R.id.createUpdateImageButton);
        setDateButton = (Button) findViewById(R.id.createSetDateButton);
        setTimeButton = (Button) findViewById(R.id.createSetTimeButton);
        setMapButton = (Button) findViewById(R.id.createSetMapButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setEditTexts(){

        //Creating references for EditText views

        mEventName = (EditText) findViewById(R.id.updateEventName);
        mEventType = (EditText) findViewById(R.id.updateEventType);
        mEventLocation = (EditText) findViewById(R.id.updateEventLocation);
        mEventDate = (TextView) findViewById(R.id.createDateTextBox);
        mEventTime = (TextView) findViewById(R.id.createTimeTextBox);
        mEventDescription = (EditText) findViewById(R.id.createEventDescription);
        mEventDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.requestFocus();
            }
        });

        mEventDescription.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
                return false;
            }
        });


        mEventImage = (ImageView) findViewById(R.id.createEventImageView);
    }

    private void setFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void setOthers(){
        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);
        M_NODE_USER_EVENTS = getResources().getString(R.string.M_NODE_USER_EVENTS);
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
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>The basic initialization and setup of the create view</li>ú
     * </ul>
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFirebase();
        setButtons();
        setEditTexts();
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(),"datePicker");
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.DialogFragment newFragment = new TimePickerDialog();
                newFragment.show(getSupportFragmentManager(),"timePicker");
            }
        });

        setMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(ctx, GetLocationActivity.class);
                startActivityForResult(mapIntent, MAP_LOCATION_REQUEST_CODE);
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

        String eventName = mEventName.getText().toString().trim();
        String eventType = mEventType.getText().toString().trim();
        String eventLocation = mEventLocation.getText().toString().trim();
        String eventDate = mEventDate.getText().toString().trim();
        String eventTime = mEventTime.getText().toString().trim();
        String eventDescription = mEventDescription.getText().toString().trim();

        String eventAddedBy = mFirebaseAuth.getCurrentUser().getUid();
        if (mFirebaseAuth.getCurrentUser()==null){
            Toast.makeText(this, "User is disconneted!", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Validation of input

        MusicEventValidator validator = new MusicEventValidator(ctx);
        if(!validator.validateEvent(eventName,
                eventType,
                eventLocation,
                eventDate,
                eventTime,
                eventDescription)){
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
        EventModel eventModel = new EventModel.EventModelBuilder(eventAddedBy)
                .Name(eventName)
                .Type(eventType)
                .LocationName(eventLocation)
                .Time(eventTime)
                .Date(eventDate)
                .ImagePath(imagePath)
                .Description(eventDescription)
                .build();

 /*       EventManager eventManager = new EventManager(mDatabaseRef,M_NODE_EVENT)
                .SetNewEvent(eventModel);

        eventManager.PushDataToFirebase();
*/
        DatabaseReference mEventReference = mDatabaseRef.getDatabase().getReference(M_NODE_EVENT);
        String newKey = mEventReference.push().getKey();
        mEventReference.child(newKey).setValue(eventModel);

        Map<String,Object> userEventMap = new HashMap<>();
        userEventMap.put(newKey,eventModel);

        mDatabaseRef.child(M_NODE_USER_EVENTS).child(eventAddedBy).setValue(userEventMap);

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

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>When the date is set, this method will be called and it will set the selected date to the corresponding {@link TextView} </li>
     *     <li>Some modifications are made to make it better looking</li>
     * </ul>
     *
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month++;
        String monthString = month >= 10?month+"" : "0" + month;
        String dayString = day>=10?day+"":"0"+day;
        mEventDate.setText(year + "/" + monthString + "/" + dayString);
    }

    /**
     *<h2>Description:</h2><br>
     * <ul>
     *     <li>When the time is set, this method will be called and it will set the selected time to the corresponding {@link TextView} </li>
     *     <li>Some modifications are made to make it better looking</li>
     * </ul>
     *
     * @param timePicker
     * @param hour
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String hourString = hour >= 10?hour+"" : "0" + hour;
        String minuteString = minute>=10?minute+"":"0"+minute;
        mEventTime.setText(hourString + ":" + minuteString);
    }
}
