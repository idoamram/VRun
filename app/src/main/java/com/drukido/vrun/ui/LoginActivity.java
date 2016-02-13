package com.drukido.vrun.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText _etxtUserName;
    EditText _etxtPassword;

    RelativeLayout _mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_login);

        _mainLayout = (RelativeLayout) findViewById(R.id.login_relativeLayout_mainLayout);
        _etxtUserName = (EditText) findViewById(R.id.login_etxtUserName);
        _etxtPassword = (EditText) findViewById(R.id.login_etxtPassword);

        Button btnSignUp = (Button) findViewById(R.id.login_btnSignup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSignUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(startSignUpActivity);
            }
        });

        Button btnLogin = (Button) findViewById(R.id.login_btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()) {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setTitle("Login");
                    progressDialog.show();
                    ParseUser.logInInBackground(_etxtUserName.getText().toString(),
                            _etxtPassword.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (e == null) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,
                                                "Hello " + user.getString("firstName") + " " +
                                        user.getString("lastName") + "!", Toast.LENGTH_LONG).show();

                                        SharedPreferences prefs =
                                                getSharedPreferences(Constants.VRUN_PREFS_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = prefs.edit();
                                        prefsEditor.putBoolean(Constants.PREF_IS_USER_LOGGED_IN,
                                                true);
                                        prefsEditor.apply();

                                        startActivity(new Intent(LoginActivity.this,
                                                MainActivity.class));
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Snackbar.make(_mainLayout, "Sorry, something went wrong...",
                                                Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        isUserLoggedIn();
    }

    private void isUserLoggedIn() {
        SharedPreferences prefs =
                getSharedPreferences(Constants.VRUN_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(Constants.PREF_IS_USER_LOGGED_IN, false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private boolean validateFields() {
        if(_etxtUserName.getText().toString().equals("")) {
            Snackbar.make(_mainLayout, "Please type your User name", Snackbar.LENGTH_LONG).show();
            return false;
        } else if(_etxtPassword.getText().toString().equals("")) {
            Snackbar.make(_mainLayout, "Please type your password", Snackbar.LENGTH_LONG).show();
            return false;
        } else return true;
    }
}
