package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.UserModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Callable;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.ListIterator;

public class UserProfileActivity extends AppCompatActivity {

    private static final boolean M_NON_EDITABLE = false;
    private static final boolean M_EDITABLE = true;
    private static final java.lang.String M_NODE_USER = "Users";
    private static final String M_NODE_USER_PROFILE = "Profile";
    private static final int RESULT_LOAD_IMAGE = 112;

    private de.hdodenhof.circleimageview.CircleImageView mUserProfilePicIM;
    private ImageView mLogoutIM;
    private ImageView mEditIM;
    private TextView mUserNameTV;
    private TextView mUserDenotTV;
    private TextView mUserFirstNameTV;
    private EditText mUserFirstNameET;
    private TextView mUserLastNameTV;
    private EditText mUserLastNameET;
    private TextView mUserEmailTV;
    private EditText mUserEmailET;
    private TextView mUserMoblieTV;
    private EditText mUserMoblieET;
    private TextView mUserGenderTV;
    private EditText mUserGenderET;
    private TextView mUserRegisteredTV;
    private EditText mUserRegisteredET;

    /* image */
    private Uri imageUri = null;
    private String imageExtension = null;
    private StorageReference mStorageRef;
    /* o */
    private String mOnStopTextMsg = "";
    private boolean mEditModeOn = false;
    private ArrayList mOldParams;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager mMainUserMngr;
    private Drawable mOldProfPic;
    private Uri mOldProfPicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /* Hiding the automatic input appearing bc. of the multiple EditText fields. */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setFirebase();

        mUserProfilePicIM = findViewById(R.id.profile_pic_CIV);
        mOldProfPic = mUserProfilePicIM.getDrawable();

        mLogoutIM = findViewById(R.id.logoutIM);
        mEditIM = findViewById(R.id.edit_profileIM);

        mUserNameTV = findViewById(R.id.user_nameTV);
        mUserDenotTV = findViewById(R.id.user_denotationTV);
        mUserFirstNameTV = (TextView) findViewById(R.id.first_nameTV);
        mUserLastNameTV = (TextView) findViewById(R.id.last_nameTV);
        mUserEmailTV = (TextView) findViewById(R.id.user_emailTV);
        mUserMoblieTV = (TextView) findViewById(R.id.user_mobileTV);
        mUserGenderTV = (TextView) findViewById(R.id.user_genderTV);

        mUserFirstNameET = findViewById(R.id.first_nameET);
        mUserLastNameET = findViewById(R.id.last_nameET);
        mUserEmailET = findViewById(R.id.user_emailET);
        mUserMoblieET = findViewById(R.id.user_mobileET);
        mUserGenderET = findViewById(R.id.user_genderET);
        mUserRegisteredET = findViewById(R.id.user_register_dateET);

        updateUI();
        setEditableFieldsTo(M_NON_EDITABLE);

        mLogoutIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LOGOUT", "IM IN ONcLICKlISTENER");

                String title = "LogOut";
                String message = "Are You sure about logging out?";
                if (mEditModeOn) {
                    title = "Editing Profile";
                    message = "Are You sure about discarding changes?";
                }

                makeConvictionAboutUserInput(title, message, new Callable() {
                    @Override
                    public Object call() throws Exception {
                        if (mEditModeOn)
                            return discardChanges();
                        else return logOutUser();
                    }
                }, new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return makeToast("Returned.");
                    }
                });


            }
        });
        mEditIM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("EDIT MODE ON", "IM IN ONcLICKlISTENER" + mEditModeOn);

                //deleteUser();
                if (!mEditModeOn) {
                    imageExtension = null;
                    imageUri = null;
                    Toast.makeText(UserProfileActivity.this, "Edit mode selected...", Toast.LENGTH_SHORT).show();
                    saveCurrentEditTextFields();
                    setEditableFieldsTo(M_EDITABLE);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Edit mode disabled...", Toast.LENGTH_SHORT).show();
                    setEditableFieldsTo(M_NON_EDITABLE);
                    saveCurrentEditTextFields();

                    saveDataToDatabase();

                }
                mEditModeOn = !mEditModeOn;
                changeIconBetweenEditnSave(mEditModeOn);

                Log.i("EDIT MODE ON", "IM IN ONcLICKlISTENER" + mEditModeOn);
            }
        });

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method saves the information to the firebase database.</li>
     * </ul>
     */
    private void saveDataToDatabase() {
        ListIterator userDetailIter = mOldParams.listIterator();

        String userId = mFirebaseAuth.getCurrentUser().getUid();

        String imagePath = "-";
        if (imageUri != null &&
                imageExtension != null) {
            String uniqueFileName = UUID.randomUUID().toString().replaceAll("-", "_");
            imagePath = "";
            imagePath = imagePath.concat("images/eventImages/" + uniqueFileName + "." + imageExtension);

            saveImageToFirebase(imagePath);
            Log.i("UPLOADING-STORAGE", "IMAGE:" + imagePath);
        }

        com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr = null;
        if (!imagePath.equals("-")) {
            usrMngr = new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(mDatabaseRef, "Users", userId)
                            .SetNewUser(new UserModel.UserModelBuilder(userDetailIter.next().toString(), userDetailIter.next().toString())
                                    .Email(userDetailIter.next().toString())
                                    .Gender(userDetailIter.next().toString())
                                    .Mobile(userDetailIter.next().toString())
                                    .Registered(userDetailIter.next().toString())
                                    .ProfilePic(imagePath)
                                    .build());
        } else {
            imagePath = mOldProfPicUrl.getPath();
            usrMngr =
                    new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(mDatabaseRef, "Users", userId)
                            .SetNewUser(new UserModel.UserModelBuilder(userDetailIter.next().toString(), userDetailIter.next().toString())
                                    .Email(userDetailIter.next().toString())
                                    .Gender(userDetailIter.next().toString())
                                    .Mobile(userDetailIter.next().toString())
                                    .Registered(userDetailIter.next().toString())
                                    .ProfilePic(imagePath)
                                    .build());
        }
//                    Log.i("DATA","UserID:"+ userId+ "\n\tID?:"+ mFirebaseAuth.getUid()+ "\n\t"+ mFirebaseAuth.getCurrentUser().getUid());

        if (!usrMngr.PushDataToFirebase()) {
            Toast.makeText(UserProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UserProfileActivity.this, "Unsuccess!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void closeVirtualKeyboard() {
        // Check if no view has focus:
        View currView = UserProfileActivity.this.getCurrentFocus();
        if (currView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currView.getWindowToken(), 0);
        }
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method saves the information from the screen to be recoverable latterly.</li>
     * </ul>
     */
    private void saveCurrentEditTextFields() {
        String userFirstName = mUserFirstNameET.getText().toString(),
                userLastName = mUserLastNameET.getText().toString(),
                userEmail = mUserEmailET.getText().toString(),
                userMobile = mUserMoblieET.getText().toString(),
                userGender = mUserGenderET.getText().toString(),
                userRegistration = mUserRegisteredET.getText().toString();
        Drawable photo = mUserProfilePicIM.getDrawable();
        mOldParams = new ArrayList();
        mOldParams.add(userFirstName);
        mOldParams.add(userLastName);
        mOldParams.add(userEmail);
        mOldParams.add(userMobile);
        mOldParams.add(userGender);
        mOldParams.add(userRegistration);

        mOldProfPic = photo;
        setMainBoardInfos(userFirstName + " " + userLastName, userEmail, mOldProfPic, null, null);
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method makes update on the middle UI to the fact that this screen is capable not just to show information about the user, but to edit them without any new activity.</li>
     * </ul>
     * <h2>Parameters</h2>
     * <ul>
     * <li>@param <i>editModeOn</i> - it says that is the edit mode on/ff(true/false)</li>
     * </ul>
     */
    private boolean discardChanges() {
        setOldParamsToEditTextFields();
        mEditModeOn = !mEditModeOn;
        changeIconBetweenEditnSave(mEditModeOn);
        setEditableFieldsTo(M_NON_EDITABLE);

        return true;
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method recovers the earlier data(user information) and put them on the screen.</li>
     * </ul>
     */
    private void setOldParamsToEditTextFields() {
        ListIterator<String> oldParamIter = mOldParams.listIterator();
        if (oldParamIter.hasNext())
            mUserFirstNameET.setText(oldParamIter.next());
        if (oldParamIter.hasNext())
            mUserLastNameET.setText(oldParamIter.next());
        if (oldParamIter.hasNext())
            mUserEmailET.setText(oldParamIter.next());
        if (oldParamIter.hasNext())
            mUserMoblieET.setText(oldParamIter.next());
        if (oldParamIter.hasNext())
            mUserGenderET.setText(oldParamIter.next());
        mUserProfilePicIM.setImageDrawable(mOldProfPic);
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method makes update on the middle UI to the fact that this screen is capable not just to show information about the user, but to edit them without any new activity.</li>
     * </ul>
     * <h2>Parameters</h2>
     * <ul>
     * <li>@param <i>editModeOn</i> - it says that is the edit mode on/ff(true/false)</li>
     * </ul>
     */
    private void changeIconBetweenEditnSave(boolean editModeOn) {
        if (editModeOn) {
            Drawable menuSave = getResources().getDrawable(android.R.drawable.ic_menu_save);
            Drawable menuDiscard = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
            mEditIM.setImageDrawable(menuSave);
            mLogoutIM.setImageDrawable(menuDiscard);

            mUserProfilePicIM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);

                    Log.i("IMAGE_UPLOAD", "SELECTING IMAGE WAS DONE");
                }
            });

            // mEditIM.setImageIcon(android.R.drawable.ic_menu_save);
        } else {
            Drawable menuEdit = getResources().getDrawable(android.R.drawable.ic_menu_edit);
            Drawable menuDiscard = getResources().getDrawable(android.R.drawable.ic_lock_power_off);
            mEditIM.setImageDrawable(menuEdit);
            mLogoutIM.setImageDrawable(menuDiscard);

            mUserProfilePicIM.setOnClickListener(null);
        }
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method makes updatable the user information.</li>
     * </ul>
     * <h2>Parameters</h2>
     * <ul>
     * <li>@param <i>editable</i> - it says that if the screen is editable or not(true/false)</li>
     * </ul>
     */
    private void setEditableFieldsTo(boolean editable) {
        mUserFirstNameET.setEnabled(editable);
        mUserLastNameET.setEnabled(editable);
        mUserEmailET.setEnabled(editable);
        mUserMoblieET.setEnabled(editable);
        mUserGenderET.setEnabled(editable);
        mUserRegisteredET.setEnabled(editable);
    }

    @Override
    protected void onStop() {
        Log.d("asd", "asd");
        Intent datasToPassBack = new Intent();
        saveUserDatasInIntent(datasToPassBack);
        super.onStop();
    }

    private void saveUserDatasInIntent(Intent datasToPassBack) {
        datasToPassBack.putExtra("activity-type", mOnStopTextMsg);
        setResult(RESULT_OK, datasToPassBack);
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method update the UI literally, what it means that update the current logged in user with the up to date information, of course that from the database.</li>
     * <li>If fails, it displays nothing.</li>
     * </ul>
     */
    private void updateUI() {

        // Gettin` the information from EditText fields...
        String userName = (mUserFirstNameET.getText().toString()) + " " + (mUserLastNameET.getText().toString()),
                userEmail = mUserEmailET.getText().toString(),
                userMobile = mUserMoblieET.getText().toString(),
                userGender = mUserGenderET.getText().toString(),
                userRegistration = mUserRegisteredET.getText().toString();
        final ArrayList<Uri> userPhoto = new ArrayList<>();
        Log.i("INFOS:", "Fields: " + userName + ", " + userEmail + ", " + userMobile + ", " + userGender + ", " + userRegistration + ".!.");

        String userId = mFirebaseAuth.getUid();
        com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr =
                new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(mDatabaseRef, M_NODE_USER, userId);

        final ArrayList<UserModel> userList = new ArrayList<>();
        mDatabaseRef.child(M_NODE_USER).child(userId).child(M_NODE_USER_PROFILE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                UserModel user = snapshot.getValue(UserModel.class);
                userList.add(user);

                String name, email, photo = "";
                mOldParams = new ArrayList();
                if (user != null) {
                    mOldParams.add(user.getFirstName());
                    mOldParams.add(user.getLastName());
                    mOldParams.add(user.getEmail());
                    mOldParams.add(user.getMobile());
                    mOldParams.add(user.getGender());
                    mOldParams.add(user.getRegistered());
                    mOldParams.add(user.getUserProfilePic());

                    name = user.getFirstName() + " " + user.getLastName();
                    email = user.getEmail();
                    photo = user.getUserProfilePic();
                    if (photo != null && !photo.equals("-")) {
                        final Uri uri = Uri.parse(photo.toString());
                        userPhoto.add(uri);
                        mOldProfPicUrl = uri;
                    }

//                    if (uri != null) {
//                        userPhoto.add(uri);
//                        StorageReference imageRef = mStorageRef.child(uri.getPath());
//                        Glide.with(UserProfileActivity.this)
//                                .using(new FirebaseImageLoader())
//                                .load(imageRef)
//                                .placeholder(R.drawable.no_image_available)
//                                .override(400, 325)
//                                .fitCenter()
//                                .into(mUserProfilePicIM);
//                    } else {
//                        Glide.with(UserProfileActivity.this)
//                                .load(R.drawable.examp_photo)
//                                .override(400, 325)
//                                .fitCenter()
//                                .into(mUserProfilePicIM);
//                    }


                } else {
                    // creating one
                    String userId = mFirebaseAuth.getUid();
                    name = mFirebaseAuth.getCurrentUser().getDisplayName();
                    email = mFirebaseAuth.getCurrentUser().getEmail();
                    Uri uri = mFirebaseAuth.getCurrentUser().getPhotoUrl();
                    try {
                        userPhoto.add(uri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        mOldProfPic = Drawable.createFromStream(inputStream, uri.toString());
                    } catch (FileNotFoundException e) {
                        mOldProfPic = getResources().getDrawable(R.drawable.examp_photo);
                    }
                    String imagePath = "-";

                    if (uri != null) {
                        imagePath = uri.getPath();
                    }

                    String mobile = "-",
                            gender = "-";

                    UserModel newUser = new UserModel.UserModelBuilder(name, "-")
                            .Email(email)
                            .Mobile(mobile)
                            .Gender(gender)
                            .ProfilePic(imagePath)
                            .build();
                    com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr = new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(
                            mDatabaseRef, M_NODE_USER, userId)
                            .SetNewUser(newUser);
                    usrMngr.PushDataToFirebase();
                }

                setOldParamsToEditTextFields();
                // Setting up the top fields

                String pathToPic = null;
                Uri uri = null;

                if (!userPhoto.isEmpty()) {
                    pathToPic = userPhoto.get(0).getPath();
                    uri = userPhoto.get(0);
                }

                setMainBoardInfos(name, email, mOldProfPic, pathToPic, uri);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //closeVirtualKeyboard();

    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method makes update on the upper UI with the given information.</li>
     * <li>If fails, it displays nothing.</li>
     * </ul>
     * <h2>Parameters</h2>
     * <ul>
     * <li>@param <i>name</i> - it gives the name(firstname+lastname)</li>
     * <li>@param <i>email</i> - it gives the user mail address</li>
     * <li>@param <i>photo</i> - it gives the user`s new photo url</li>
     * </ul>
     */
    private void setMainBoardInfos(String name, String email, Drawable photo, String photoUri, Uri uri) {
        name.replaceFirst("-", "");
        if (name.endsWith("-")) {
            int indexLastNull = name.lastIndexOf("-");
            name = name.substring(0, indexLastNull);
        }
        mUserNameTV.setText(name);
        mUserDenotTV.setText(email);
        //mUserPic...
        if (photoUri != null) {
            StorageReference imageRef = mStorageRef.child(uri.getPath());
            Glide.with(UserProfileActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .fitCenter()
                    .into(mUserProfilePicIM);
            mOldProfPic = mUserProfilePicIM.getDrawable();
        } else {
            mUserProfilePicIM.setImageDrawable(mOldProfPic);
        }

    }

    private boolean makeToast(String text) {
        Toast.makeText(this, "Operation " + text, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * <h2>Description:</h2>
     * <ul>
     * <li>It is called when the user clicks on the LogOut button.</li>
     * <li>Here simply we search a user based on the logged in user, and sign out him.</li>
     * <li>This function does not need either parameters or does not return anything.</li>
     * </ul>
     *
     * @return it returns the result of the log out method as {@link Boolean}
     */
    private Boolean logOutUser() {
        Log.i("LOGOUT", "IM IN FUNC.");

        boolean result = AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        // ...

                    }
                }).isSuccessful();
        mOnStopTextMsg = "LogOut";
        finish();
        return true;
    }

    /**
     * <h2>Description:</h2>
     * <ul>
     * <li>It is called when the user clicks on the DELETE_USER button.</li>
     * <li>Here simply we search a user based on the logged in user, and delete him from the auth service, and from the database too.</li>
     * <li>This function does not need either parameters or does not return anything.</li>
     * </ul>
     *
     * @return it returns the result of the delete method as {@link Boolean}
     */
    private boolean deleteUser() {
        Log.i("DELETE", "IM IN DELETE FUNC.");

//        boolean result =  AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
//                        // ...
//                    }
//                }).isSuccessful();

        return true;
    }

    /**
     * <h2>Description:</h2>
     * <ul>
     * <li>It is called when the user clicks on the DELETE_USER button.</li>
     * <li>Here simply we search a user based on the logged in user, and delete him from the auth service, and from the database too.</li>
     * </ul>
     * <h2>Description:</h2>
     * <ul>
     * <li>@param <i>title</i>          is about to give a title to the Dialog</li>
     * <li>@param <i>message</i>        is about what to show on the Dialog</li>
     * <li>@param <i>callOnPositive</i> is about what to call on OK button of the Dialog</li>
     * <li>@param <i>callOnNegative</i> is about what to call on Cancel button of the Dialog</li>
     * <li>Does not return anything.</li>
     * </ul>
     */
    private void makeConvictionAboutUserInput(String title, String message, final Callable callOnPositive, final Callable callOnNegative) {
        AlertDialog.Builder alDialog = new AlertDialog.Builder(this);
        alDialog.setTitle(title);
        alDialog.setMessage(message);
        alDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    callOnPositive.call();
                    makeToast("Succeded! :)");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    callOnNegative.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        alDialog.show();
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>When the Request code is RESULT_LOAD_IMAGE, it fetches the image's data into the imageUri variable.</li>
     * <li>The image's extension will be stored in the imageExtension variable.</li>
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
                        Glide.with(UserProfileActivity.this)
                                .load(imageUri)
                                .override(250, 175)
                                .into(mUserProfilePicIM);
                    }
                }
            } else {
                Toast.makeText(this, "File was not loaded!", Toast.LENGTH_SHORT).show();
            }


        }

    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>This method is the one responsible for uploading the event's image to the Firebase Cloud Storage.</li>
     * <li>If the upload is not successful, it will signal to the user via a {@link Toast}</li>
     * </ul>
     *
     * @param imagePath
     */
    private void saveImageToFirebase(String imagePath) {
        StorageReference eventImageRef = mStorageRef.child(imagePath);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();


        UploadTask uploadTask = eventImageRef.putFile(imageUri, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserProfileActivity.this, "We were unable to upload your file.", Toast.LENGTH_LONG).show();
                Log.i("ERR", e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

            }
        });
    }

}
