package com.se2006.teamkaydon.powerfull.Boundary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.se2006.teamkaydon.powerfull.Control.FirebaseManager;
import com.se2006.teamkaydon.powerfull.R;

/**
 * Setup UI for login page and calls methods to access Firebase Authentication to log user in
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;

    /**
     * Creates login ui at the start of Login Activity Instance
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FirebaseDAO firebase = new FirebaseManager();

        // If user is already logged in then start MapsActivity instead.
        if (firebase.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
            finish();
        }

        // Set the view now.
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(firebase);
            }
        });
    }

    /**
     * Logs in the user.
     *
     * @param firebase an instance of FirebaseManager created at the start of LoginActivity
     */
    private void login(FirebaseDAO firebase) {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        firebase.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
