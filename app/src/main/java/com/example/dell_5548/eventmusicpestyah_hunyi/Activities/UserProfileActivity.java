package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Callable;

public class UserProfileActivity extends AppCompatActivity {

    private Button mLogoutBT;
    private Button mDeleteUserBT;
    private TextView mEmailFieldTV;
    private TextView mNameFieldTV;
    private TextView mLastLoginFieldTV;

    private String mOnStopTextMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLogoutBT = (Button) findViewById(R.id.logoutBT);
        mDeleteUserBT = (Button) findViewById(R.id.delete_userBT);
        mEmailFieldTV = (TextView) findViewById(R.id.user_emailTV);
        mNameFieldTV = (TextView) findViewById(R.id.user_nameTV);
        mLastLoginFieldTV = (TextView) findViewById(R.id.last_loginTV);

        updateUI();

        mLogoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("LOGOUT", "IM IN ONcLICKlISTENER");
                String title = "LogOut";
                String message = "Are You sure about logging out?";
                makeConvictionAboutUserInput(title, message, new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return logOutUser();
                    }
                }, new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return makeToast("was not successfull :(.");
                    }
                });


            }
        });
        mDeleteUserBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("DELETE", "IM IN ONcLICKlISTENER");

                deleteUser();

            }
        });
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
     *     <li>This method update the UI literally, what it means that update the current logged in user with the up to date informations.</li>
     *     <li>If fails, it displays nothing.</li>
     * </ul>
     *
     */
    private void updateUI() {
        FirebaseUser fibUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fibUser == null) return;
        String userEmailStr = fibUser.getEmail();
        String userNameStr = fibUser.getDisplayName();
        long lastLoginStamp = 0;
        try {
            // WTF here? => bc of the old Auth 11.4.0 we cant get the time..............
            // lastLoginStamp = FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
        } catch (Exception e) {

        }
        Date userLastLog = new Date((long) (lastLoginStamp * 1000));
        mEmailFieldTV.setText(userEmailStr);
        mNameFieldTV.setText(userNameStr);
        mLastLoginFieldTV.setText(userLastLog.toString());
    }

    private boolean makeToast(String text) {
        Toast.makeText(this, "LogOut " + text, Toast.LENGTH_SHORT).show();
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
        return result;
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

        boolean result =  AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        // ...
                    }
                }).isSuccessful();

        return result;
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
