package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.content.Intent;
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

import com.example.dell_5548.eventmusicpestyah_hunyi.Models.UserModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String M_NODE_USER = "Users";
    private Button mRegisterBT;
    private EditText mEmailET;
    private EditText mNameET;
    private EditText mPasswordET;

    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();

        RelativeLayout layout = findViewById(R.id.registerRL);
        mRegisterBT = (Button) findViewById(R.id.registerBT);
        mEmailET = (EditText) findViewById(R.id.emailET);
        mNameET = (EditText) findViewById(R.id.nameET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);

        mRegisterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                registerUser();
            }
        });

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
    }

    @Override
    protected void onStop() {
        Log.d("asd", "asd");
        Intent datasToPassBack = new Intent();
        saveUserDatasInIntent(datasToPassBack);
        super.onStop();
    }

    private void saveUserDatasInIntent(Intent datasToPassBack) {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if(firebaseUser.getDisplayName() != null)
            datasToPassBack.putExtra("userName", firebaseUser.getDisplayName());
        if(firebaseUser.getPhotoUrl() != null)
            datasToPassBack.putExtra("userPhoto", firebaseUser.getPhotoUrl());
        setResult(RESULT_OK, datasToPassBack);
    }


    public void onClick(View view) {
        Log.i("SIGN", "IM IN REGISTER-USER");
        Log.i("SI", "IM IN REGISTER-USER");

        if (view == mRegisterBT) {
            registerUser();
        }
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method register a guest to be a user.</li>
     *     <li>The really first thing that it does, it is the checking of fields if they are filled or not.</li>
     *     <li>If any of the fields are not filled then it will fail, and does not make the registering but alerting with a message the user in every case that, there are some problem with these fields.</li>
     *     <li>If the registering was successful, then naturally it shows that with a Toast {@link Toast}.</li>
     * </ul>
     *
     * @return it has not any returned value
     */
    private void registerUser() {
        Log.i("SIGN", "IM IN REGISTER-USER");
        String emailStr = mEmailET.getText().toString().trim();
        String nameStr = mNameET.getText().toString();
        String passwordStr = mPasswordET.getText().toString().trim();

        Log.i("SIGN", "Email: " + emailStr + "|pass:" + passwordStr);
        if (TextUtils.isEmpty(emailStr)) {
            // the email field is empty
            Toast.makeText(this, "Please fill in the Email field.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nameStr)) {
            // the email field is empty
            Toast.makeText(this, "Please fill in the Password field.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            // the email field is empty
            Toast.makeText(this, "Please fill in the Password field.", Toast.LENGTH_SHORT).show();
            return;
        }
        // everything it's okay

        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar

        mFirebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // successfull registering
                            Toast.makeText(RegisterActivity.this, "Success ;)!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailET.getText().toString(),mPasswordET.getText().toString());
                            String name = mNameET.getText().toString(),
                                    userId = FirebaseAuth.getInstance().getUid(),
                                    email = mEmailET.getText().toString();
                            // Registering to the database with infos
                            String mobile = "-",
                                    gender = "-";
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            UserModel newUser = new UserModel.UserModelBuilder(name, "-")
                                    .Email(email)
                                    .Mobile(mobile)
                                    .Gender(gender)
                                    .build();
                            com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager usrMngr = new com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.UserManager(
                                    db, M_NODE_USER, userId)
                                    .SetNewUser(newUser);
                            usrMngr.PushDataToFirebase();

                            Intent profilePage = new Intent(getApplicationContext(), UserProfileActivity.class);
                            startActivity(profilePage);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Could not register, please try again(later) -.-", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // To Hide ProgressBar
    }
}
