package com.example.dell_5548.eventmusicpestyah_hunyi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
//import com.fire

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int M_SIGN_IN_REQ = 123;
    private static final int M_REGISTER_REQ = 100;
    private Button mLoginBT;
    private Button mRegisterBT;

    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    private GoogleApiClient mGoogleApiClient;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        // AuthUI
        startLoginActivityForUser();

        RelativeLayout layout = findViewById(R.id.registerRL);
        mLoginBT = (Button) findViewById(R.id.loginBT);
        mRegisterBT = (Button) findViewById(R.id.registerBT);

        mLoginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("SIGN", "IM IN ON CLICK LISTENER!");
                startLoginActivityForUser();
            }
        });
        mRegisterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivityForUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

    }

    @Override
    protected void onStop() {
        Log.d("STOPPING", "HERE triying to STOP the app");
        Intent dataToPassBack = new Intent();
        saveUserDatasInIntent(dataToPassBack);
        super.onStop();
    }

    private void saveUserDatasInIntent(Intent datasToPassBack) {
        if (mFirebaseUser == null) return;
        if (mFirebaseUser.getDisplayName() != null)
            datasToPassBack.putExtra("userName", mFirebaseUser.getDisplayName());
        if (mFirebaseUser.getPhotoUrl() != null)
            datasToPassBack.putExtra("userPhoto", mFirebaseUser.getPhotoUrl());
        setResult(RESULT_OK, datasToPassBack);
    }

    private void startRegisterActivityForUser() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(
                registerIntent,
                M_REGISTER_REQ);
    }

    private void startLoginActivityForUser() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                        //.setTheme(R.style.CardView_Dark)
                        .setIsSmartLockEnabled(false)
                        .build(),
                M_SIGN_IN_REQ);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Login failed :(.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Log.d("RESULT-FAIL", "LOGIN FAILED => " + resultCode);
            Toast.makeText(this, data.getStringExtra("activity-type") + " Was Not Successfully made :(.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {

            case (M_SIGN_IN_REQ):
                IdpResponse response = IdpResponse.fromResultIntent(data);
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // ...
                if (response != null)
                Toast.makeText(this, response.getProviderType()+" login succeeded :).", Toast.LENGTH_SHORT).show();
                // if (mFirebaseUser != null)
                // Log.i("GOOGLE", "IM IN GOOGLE-LOGIN:" + mFirebaseUser.getUid() + ";" + mFirebaseUser.getDisplayName() + "; " + mFirebaseUser.getMetadata().getCreationTimestamp() + ";" + mFirebaseUser.getPhotoUrl());

                // Starting the profile activity
                Intent profilePage = new Intent(this, UserProfileActivity.class);
                startActivity(profilePage);
                //onStop();
                break;
            case (M_REGISTER_REQ):
                // if the registering was successfull then we need to log in this user with theese datas
                Log.d("REGISTER-OK", "Registering WAS OK => " + resultCode);

                break;
            default:
                break;
        }
    }
}
/* Progress - BAR
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        */