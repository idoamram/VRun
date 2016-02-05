package com.drukido.vrun.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.User;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    public static final String EXTRA_IS_IPHONE_USER_SIGNUP = "isIPhoneUserSignUp";
    public static final int SIGNUP_REQUEST_CODE = 11;

    CheckBox _checkBoxiPhoneUser;
    AutoCompleteTextView _autoTxtGroupName;
    EditText _etxtUserName;
    EditText _etxtFirstName;
    EditText _etxtLastName;
    EditText _etxtEmail;
    EditText _etxtPassword;
    EditText _etxtPasswordRepeat;
    EditText _etxtPhoneNumber;
    Button btnSignUp;

    ProgressDialog mProgressDialog;

    LinearLayout _mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _mainLayout = (LinearLayout) findViewById(R.id.signup_mainLayout);
        _checkBoxiPhoneUser = (CheckBox) findViewById(R.id.signup_checkBox_iphoneUser);
        _autoTxtGroupName = (AutoCompleteTextView) findViewById(R.id.signup_txtGroupName);
        _etxtUserName = (EditText) findViewById(R.id.signup_txtUsername);
        _etxtFirstName = (EditText) findViewById(R.id.signup_txtFirstname);
        _etxtLastName = (EditText) findViewById(R.id.signup_txtLastname);
        _etxtEmail = (EditText) findViewById(R.id.signup_txtEmail);
        _etxtPassword = (EditText) findViewById(R.id.signup_txtPassword);
        _etxtPasswordRepeat = (EditText) findViewById(R.id.signup_txtPasswordRepeat);
        _etxtPhoneNumber = (EditText) findViewById(R.id.signup_txtPhoneNumber);

        btnSignUp = (Button) findViewById(R.id.signup_btnSignup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(validateFields()) {

                    mProgressDialog = new ProgressDialog(SignUpActivity.this);
                    mProgressDialog.setTitle("Signing up...");
                    mProgressDialog.setMessage("Please wait");
                    mProgressDialog.show();

                    final User newUser = new User();
                    newUser.setUsername(_etxtUserName.getText().toString());
                    newUser.setEmail(_etxtEmail.getText().toString());
                    newUser.setPassword(_etxtPassword.getText().toString());
                    newUser.setFirstName(_etxtFirstName.getText().toString());
                    newUser.setLastName(_etxtLastName.getText().toString());
                    newUser.setName(_etxtFirstName.getText().toString() + " " +
                            _etxtLastName.getText().toString());
                    newUser.setPhoneNumber(_etxtPhoneNumber.getText().toString());
                    newUser.setGroup(Group.createWithoutData(Group.class,
                            Constants.VRUN_GROUP_OBJECT_ID));
                    newUser.setIsIphoneUser(_checkBoxiPhoneUser.isChecked());

                    signUp(newUser);
                }
            }
        });
    }

    private void signUp(final User newUser) {
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                mProgressDialog.dismiss();

                if (e == null) {
                    Toast.makeText(SignUpActivity.this, "Welcome " +
                                    newUser.getName() + "!",
                            Toast.LENGTH_LONG).show();
                    btnSignUp.setText(getString(R.string.welcome));
                    btnSignUp.setBackgroundColor(getResources()
                            .getColor(R.color.colorGreenSuccess));
                    
                    login();
                } else {
                    Snackbar.make(_mainLayout, "Sorry, something went wrong...",
                            Snackbar.LENGTH_LONG).show();
                    btnSignUp.setText(getString(R.string.failed));
                    btnSignUp.setBackgroundColor(getResources()
                            .getColor(R.color.colorRedFailed));
                }
            }
        });
    }

    private void login() {
        ParseUser.logInInBackground(_etxtUserName.getText().toString(),
                _etxtPassword.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Toast.makeText(SignUpActivity.this,
                                    "Hello " + user.getString("firstName") + " " +
                                            user.getString("lastName") + "!", Toast.LENGTH_LONG).show();

                            SharedPreferences prefs =
                                    getSharedPreferences(Constants.VRUN_PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.putBoolean(Constants.PREF_IS_USER_LOGGED_IN,
                                    true);
                            prefsEditor.apply();

                            startActivity(new Intent(SignUpActivity.this,
                                    MainActivity.class));
                            finish();
                        } else {
                            Snackbar.make(_mainLayout, "Sorry, something went wrong...",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateFields() {
        if(_etxtUserName.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type a User name", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtFirstName.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type your Fist name", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtLastName.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type your Last name", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtEmail.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type your Email", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtPassword.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type a password", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtPasswordRepeat.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please repeat your password", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (!(_etxtPassword.getText().toString()
                .equals(_etxtPasswordRepeat.getText().toString()))) {
            Snackbar.make(_mainLayout, "Please type match passwords", Snackbar.LENGTH_LONG).show();
            return false;
        } else if (_etxtPhoneNumber.getText().toString().equals(Constants.EMPTY_STRING)) {
            Snackbar.make(_mainLayout, "Please type a Phone number", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
