package com.drukido.vrun.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.drukido.vrun.R;
import com.drukido.vrun.database.DBObject;
import com.drukido.vrun.entities.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
