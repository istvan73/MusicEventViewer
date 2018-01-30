package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.*;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.UserModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.Callable;

public class UserProfileActivity extends AppCompatActivity {

    private static final boolean M_NON_EDITABLE = false;
    private static final boolean M_EDITABLE = true;
    private static final java.lang.String M_NODE_USER = "Users";
    private static final String M_NODE_USER_PROFILE = "Profile";
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

    private String mOnStopTextMsg = "";
    private boolean mEditModeOn = false;
    private ArrayList mOldParams;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager mMainUserMngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /* Hiding the automatic input appearing bc. of the multiple EditText fields. */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setFirebase();

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
                String message = "Are You sure about logging out? \n get the fuck out here";
                if (mEditModeOn) {
                    title = "Editing Profile";
                    message = "Are You sure about discarding changes? \n get the fuck out here";
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
        com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr =
                new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(mDatabaseRef, "Users", userId)
                        .SetNewUser(new UserModel.UserModelBuilder(userDetailIter.next().toString(), userDetailIter.next().toString())
                                .Email(userDetailIter.next().toString())
                                .Gender(userDetailIter.next().toString())
                                .Mobile(userDetailIter.next().toString())
                                .Registered(userDetailIter.next().toString())
                                .build());

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
        //mStorageRef = FirebaseStorage.getInstance().getReference();
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
        mOldParams = new ArrayList();
        mOldParams.add(userFirstName);
        mOldParams.add(userLastName);
        mOldParams.add(userEmail);
        mOldParams.add(userMobile);
        mOldParams.add(userGender);
        mOldParams.add(userRegistration);

        setMainBoardInfos(userFirstName + " " + userLastName, userEmail, "");
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
            // mEditIM.setImageIcon(android.R.drawable.ic_menu_save);
        } else {
            Drawable menuEdit = getResources().getDrawable(android.R.drawable.ic_menu_edit);
            Drawable menuDiscard = getResources().getDrawable(android.R.drawable.ic_lock_power_off);
            mEditIM.setImageDrawable(menuEdit);
            mLogoutIM.setImageDrawable(menuDiscard);
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

                    name = user.getFirstName() + " " + user.getLastName();
                    email = user.getEmail();
                    photo = "";
                } else {
                    // creating one
                    String userId = mFirebaseAuth.getUid();
                    name = mFirebaseAuth.getCurrentUser().getDisplayName();
                    email = mFirebaseAuth.getCurrentUser().getEmail();
                    Uri uri = mFirebaseAuth.getCurrentUser().getPhotoUrl();
                    

                    String mobile = "-",
                            gender = "-";

                    UserModel newUser = new UserModel.UserModelBuilder(name, "-")
                            .Email(email)
                            .Mobile(mobile)
                            .Gender(gender)
                            .build();
                    com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr = new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(
                            mDatabaseRef, M_NODE_USER, userId)
                            .SetNewUser(newUser);
                    usrMngr.PushDataToFirebase();
                }
                setOldParamsToEditTextFields();
                // Setting up the top fields
                setMainBoardInfos(name, email, photo);
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
    private void setMainBoardInfos(String name, String email, String photo) {
        name.replaceFirst("-", "");
        if (name.endsWith("-")) {
            int indexLastNull = name.lastIndexOf("-");
            name = name.substring(0, indexLastNull);
        }
        mUserNameTV.setText(name);
        mUserDenotTV.setText(email);
        //mUserPic...
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
}
