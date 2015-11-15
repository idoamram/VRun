package com.drukido.vrun.ui;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    EditText _etxtUserName;
    EditText _etxtFirstName;
    EditText _etxtLastName;
    EditText _etxtEmail;
    EditText _etxtPassword;
    EditText _etxtPasswordRepeat;
    EditText _etxtPhoneNumber;
    Button btnSignUp;

    LinearLayout _mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _mainLayout = (LinearLayout) findViewById(R.id.signup_mainLayout);
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
                    final ParseUser newUser = new ParseUser();
                    newUser.setUsername(_etxtUserName.getText().toString());
                    newUser.setEmail(_etxtEmail.getText().toString());
                    newUser.setPassword(_etxtPassword.getText().toString());
                    newUser.put("firstName", _etxtFirstName.getText().toString());
                    newUser.put("lastName", _etxtLastName.getText().toString());
                    newUser.put("name", _etxtFirstName.getText().toString() + " " +
                    _etxtLastName.getText().toString());
                    newUser.put("phoneNumber", _etxtPhoneNumber.getText().toString());
                    newUser.put("group",
                            ParseObject.createWithoutData("Group", Constants.VRUN_GROUP_OBJECT_ID));

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(SignUpActivity.this, "Welcome " +
                                newUser.get("firstName") + " " + newUser.get("lastName") + "!",
                                        Toast.LENGTH_LONG).show();
                                btnSignUp.setText(getString(R.string.welcome));
                                btnSignUp.setBackgroundColor(Color.parseColor("#8BC34A"));
                            } else {
                                Snackbar.make(_mainLayout, "Sorry, something went wrong...",
                                        Snackbar.LENGTH_LONG).show();
                                btnSignUp.setText(getString(R.string.failed));
                                btnSignUp.setBackgroundColor(Color.parseColor("#F44336"));
                            }
                        }
                    });
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
