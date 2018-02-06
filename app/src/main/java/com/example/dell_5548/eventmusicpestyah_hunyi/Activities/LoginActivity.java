package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private TextView mPassRecoverTV;

    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    private GoogleApiClient mGoogleApiClient;

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li> <u>providers</u> This variable is responsible for listing to the activity the methods what the developer want to be used to log in. </li>
     * <ul>
     */
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
        //startLoginActivityForUser();

        RelativeLayout layout = findViewById(R.id.registerRL);
        mLoginBT = (Button) findViewById(R.id.loginBT);
        mRegisterBT = (Button) findViewById(R.id.registerBT);
        mPassRecoverTV = (TextView) findViewById(R.id.user_password_recoverTV);

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
        mPassRecoverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = getEmailForPassRecover();

                Log.i("EMAIL", "Email settted by da USER:" + email);

                //recoverPasswordForUser(email);
            }
        });
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method asks and returns an email as a String from the user.</li>
     *     <li>Its setted by a Dialog View.</li>
     * </ul>
     *
     * @input there is no needed for input
     * @return String - it is the user inputted {@link String}, perhaps an email.
     */
    private String getEmailForPassRecover() {
        final StringBuilder out = new StringBuilder();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please, add your email here:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Editable value = input.getText();
                StringBuilder append = out.append(value.toString());
                recoverPasswordForUser(append.toString());
            }
        });
        AlertDialog alDialog = alert.create();
        alDialog.show();
        return out.toString();
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method send a request to the Firebase as recovering a password for an earlier inputted email.</li>
     *     <li>If it fails, then a Toast message is showed to the user that it is not possible.</li>
     *     <li>If it doesn`t, then also a Toast message is showed to the user that it was succeeded.</li>
     * </ul>
     *
     * @param email an email {@link String} is needed to be sended the req. to the Firebase
     * @return it has not any returned value
     */
    private void recoverPasswordForUser(String email) {
        Log.d("PASS-RECOVER", "Email senSENDING: "+ email+".");
        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("PASS-RECOVER", "Email sent.");

                            Toast.makeText(LoginActivity.this, "Email sent :).", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("PASS-RECOVER", "Email wasn`t sent.");
                            Toast.makeText(LoginActivity.this, "Email was not sent :(.", Toast.LENGTH_SHORT).show();
                        }
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

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method saves some user information getted from {@link FirebaseUser} instance.</li>
     *     <li>If it fails, then there is nothing to save.</li>
     * </ul>
     *
     * @param datasToPassBack a intent {@link Intent} is needed to be sended to be saved in the {@link Bundle}
     * @return it has not any returned value
     */
    private void saveUserDatasInIntent(Intent datasToPassBack) {
        if (mFirebaseUser == null) return;
        if (mFirebaseUser.getDisplayName() != null)
            datasToPassBack.putExtra("userName", mFirebaseUser.getDisplayName());
        if (mFirebaseUser.getPhotoUrl() != null)
            datasToPassBack.putExtra("userPhoto", mFirebaseUser.getPhotoUrl());
        setResult(RESULT_OK, datasToPassBack);
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method saves RegisterActivity to the request of the user(in this minute he/she is a guest).</li>
     * </ul>
     *
     * @return it has not any returned value
     */
    private void startRegisterActivityForUser() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(
                registerIntent,
                M_REGISTER_REQ);
    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method saves LoginActivityForUser to the request of the user(in this minute he/she is a guest).</li>
     * </ul>
     *
     * @return it has not any returned value
     */
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

    /**
     * <h2>Description:</h2><br>
     * <ul>
     *     <li>This method handles the result of the two Activities, like Signin or Registering.</li>
     *     <li>If they return a fail, we dont do anything but we show a Toast {@link Toast} about it.</li>
     * </ul>
     *
     * @param requestCode it can be M_SIGN_IN_REQ => this is the SignIn req by the user,
     *                          or M_REGISTER_REQ => this is the Register req by the user
     * @param resultCode if the user just closed the Activitys or these Activities just fails somewhere then it cant be OK
     *                  unless it will be: RESULT_OK as (-1) value, and we can do things onwards
     * @param data with it we can find any result from the Activity opened earlier from this Activity
     * @return it has not any returned value
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Log.d("RESULT-FAIL", "LOGIN FAILED => " + resultCode);
            Toast.makeText(this, " Was Not Successfully made :(.", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {

            case (M_SIGN_IN_REQ):
                IdpResponse response = IdpResponse.fromResultIntent(data);
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                // ...
                if (response != null)
                    Toast.makeText(this, response.getProviderType() + " login succeeded :).", Toast.LENGTH_SHORT).show();
                // if (mFirebaseUser != null)
                // Log.i("GOOGLE", "IM IN GOOGLE-LOGIN:" + mFirebaseUser.getUid() + ";" + mFirebaseUser.getDisplayName() + "; " + mFirebaseUser.getMetadata().getCreationTimestamp() + ";" + mFirebaseUser.getPhotoUrl());

                // Starting the profile activity
                finish();
                /*Intent profilePage = new Intent(this, UserProfileActivity.class);
                startActivity(profilePage);*/
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
