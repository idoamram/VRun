package com.drukido.vrun.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.drukido.vrun.R;

public class SendFeedbackActivity extends AppCompatActivity {

    RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        mMainLayout = (RelativeLayout) findViewById(R.id.sendFeedback_mainRelativeLayout);

        findViewById(R.id.sendFeedback_btn_sendFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editTextSubject = (EditText) findViewById(R.id.sendFeedback_editText_Subject);
                EditText editTextMessage = (EditText) findViewById(R.id.sendFeedback_editText_Message);

                if (!editTextSubject.getText().toString().equals("")) {
                    if (!editTextMessage.getText().toString().equals("")) {
                        Intent gmailIntent = new Intent();
                        gmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                        gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ido.movieditor@gmail.com"});
                        gmailIntent.putExtra(Intent.EXTRA_SUBJECT, editTextSubject.getText().toString());
                        gmailIntent.putExtra(Intent.EXTRA_TEXT, editTextMessage.getText().toString());
                        try {
                            SendFeedbackActivity.this.startActivity(gmailIntent);
                        } catch (ActivityNotFoundException ex) {
                            // handle error
                        }
                    } else {
                        Snackbar.make(mMainLayout, "Please type a message", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(mMainLayout, "Please type a subject", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
