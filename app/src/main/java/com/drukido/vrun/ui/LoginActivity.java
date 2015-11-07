package com.drukido.vrun.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.drukido.vrun.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnSignUp = (Button) findViewById(R.id.login_btnSignup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSignupActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(startSignupActivity);
            }
        });
    }
}
