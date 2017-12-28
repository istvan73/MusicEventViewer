package com.example.dell_5548.eventmusicpestyah_hunyi;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

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
                            onStop();
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
