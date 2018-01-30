package com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers;

import android.util.Log;

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.UserModel;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by DELL_5548 on 12/30/2017.
 * <p>
 * <h2>Description:</h2><br>
 * <ul>
 * <li>This class is responsible for pushing a new user class to the database({@link FirebaseDatabase}).</li>
 * </ul>
 * <br>
 * <h2>Usage:</h2><br>
 * <ul>
 * <li>Create a new object of {@link UserManager}, add the {@link UserModel} as parameter.</li>
 * <li>If you want to send the newly user created, then simply call on the instance the PushDataToFirebase() method. This method returns the result of the pushing, if it was successful, then returns true, otherwise false.</li>
 * </ul>
 */

public class UserManager {
    private DatabaseReference mDatabaseRef;
    private UserModel mUser;
    private String mUserId;
    private String M_NODE_USER;
    private String M_NODE_USER_PROFILE = "Profile";
    private UserModel mCurrUserInfo;

    private UserManager(DatabaseReference fbDatabase, String NODE_USER) {
        this.mDatabaseRef = fbDatabase;
        this.M_NODE_USER = NODE_USER;
    }

    public UserManager(DatabaseReference fbDatabase, String NODE_USER, String userId) {
        this.mUserId = userId;
        this.mDatabaseRef = fbDatabase;
        this.M_NODE_USER = NODE_USER;
    }

    public UserManager SetNewUser(UserModel newUser) {
        this.mUser = newUser;
        return this;
    }

    public UserModel getCurrUserInfo() {
        return this.mCurrUserInfo;
    }

    public boolean PushDataToFirebase() {

        boolean uploadResult = mDatabaseRef.getDatabase().getReference(M_NODE_USER).child(mUserId).child(M_NODE_USER_PROFILE).setValue(mUser).isSuccessful();

        return uploadResult;
    }

    public UserManager getCurrentUserInfo(FirebaseDatabase mFirebaseInstance) {
        final ArrayList<UserModel> userInfoList = new ArrayList<>();
        mDatabaseRef.getDatabase().getReference(M_NODE_USER).child(mUserId).child(M_NODE_USER_PROFILE).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                userInfoList.add(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        if (userInfoList.isEmpty()) {
            mCurrUserInfo = (new UserModel.UserModelBuilder("", "").build());
            return this;
        }
        mCurrUserInfo = userInfoList.get(0);
        return this;
    }
}
