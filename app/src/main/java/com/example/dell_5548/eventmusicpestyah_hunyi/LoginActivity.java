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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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
        mLoginBT = (Button) findViewById(R.id.registerBT);
        mEmailET = (EditText) findViewById(R.id.emailET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);

        mLoginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                loginUser();
            }
        });

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
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
