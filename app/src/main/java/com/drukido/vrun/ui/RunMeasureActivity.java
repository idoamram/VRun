package com.drukido.vrun.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.drukido.vrun.R;
import com.drukido.vrun.ui.views.MyTimePickerDialog;
import com.drukido.vrun.ui.views.TimePicker;

import java.util.Calendar;

public class RunMeasureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_measure);

        // Run Duration Edit Text
        final EditText duration = (EditText) findViewById(R.id.runMeasure_etxt_Duration);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPicker((EditText)v);
            }
        });

    }

    public void showPicker(final EditText v){
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
}
