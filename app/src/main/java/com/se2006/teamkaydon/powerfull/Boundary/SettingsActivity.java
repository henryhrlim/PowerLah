package com.se2006.teamkaydon.powerfull.Boundary;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.se2006.teamkaydon.powerfull.R;

/**
 * Interface to change user profile related settings such as change email, change password, reset password, delete account and sign out
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class SettingsActivity extends AppCompatActivity {

    private Button btnChangeEmail, btnChangePassword, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;


    /** Instaitiates interface for profile setting elements such as text fields for change password/email, buttons for reset password etc.
     * @param savedInstanceState savedInstanceState a Bundle object containing previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangeEmail = (Button) findViewById(R.id.change_email_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
//        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.sign_out);

        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    /**
     * Updates the user's email to the new email that was entered.
     */
    private void updateEmail() {
        progressBar.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !newEmail.getText().toString().trim().equals("")) {
            user.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else if (newEmail.getText().toString().trim().equals("")) {
            newEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the user's password to the new password that was entered.
     */
    private void updatePassword() {
        progressBar.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !newPassword.getText().toString().trim().equals("")) {
            if (newPassword.getText().toString().trim().length() < 6) {
                newPassword.setError("Password too short, enter minimum 6 characters");
                progressBar.setVisibility(View.GONE);
            } else {
                user.updatePassword(newPassword.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SettingsActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else if (newPassword.getText().toString().trim().equals("")) {
            newPassword.setError("Enter password");
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Deletes the current user's account and logs him out.
     */
    private void deleteAccount() {
        progressBar.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Your account is successfully deleted.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SettingsActivity.this, SignUpActivity.class));
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to delete your account.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    /**
     * Sign out method
     */
    private void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}