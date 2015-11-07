package com.drukido.vrun.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.ui.views.MyTimePickerDialog;
import com.drukido.vrun.ui.views.TimePicker;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;

public class RunMeasureActivity extends AppCompatActivity {

    String mRunId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_measure);

        Intent intent = getIntent();
        mRunId = intent.getStringExtra(Constants.EXTRA_RUN_ID);

        // Run Duration Edit Text
        final EditText duration = (EditText) findViewById(R.id.runMeasure_etxt_Duration);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPicker((EditText)v);
            }
        });

        // Complete Run Button
        final Button btnComplete =(Button) findViewById(R.id.runMeasure_btn_completeRun);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteRun();
            }
        });

    }

    protected void showPicker(final EditText v){
        Calendar now = Calendar.getInstance();
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute, int seconds) {
                // TODO Auto-generated method stub
                v.setText("Time" + String.format("%02d", hourOfDay) +
                        ":" + String.format("%02d", minute) +
                        ":" + String.format("%02d", seconds));
            }
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true);
        mTimePicker.show();
    }

    protected void CompleteRun() {
        final EditText duration = (EditText) findViewById(R.id.runMeasure_etxt_Duration);
        final EditText distance = (EditText) findViewById(R.id.runMeasure_etxt_Distance);

        if ((duration.getText().equals("")) && distance.getText().equals("")) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
        }
        else {
            Run run = ParseObject.createWithoutData(Run.class, mRunId);
            run.setCreator(ParseUser.getCurrentUser());
            run.setDistance(Long.parseLong(distance.getText().toString()));
            run.setDuration(duration.getText().toString());

            run.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(RunMeasureActivity.this, "Couldn't complete the run.", Toast.LENGTH_LONG);
                    }
                    else {
                        Toast.makeText(RunMeasureActivity.this, "Run Completed.", Toast.LENGTH_LONG);
                        RunMeasureActivity.this.finish();
                    }
                }
            });
        }
    }
}
