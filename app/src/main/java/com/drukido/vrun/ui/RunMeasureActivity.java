package com.drukido.vrun.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.drukido.vrun.R;

public class RunMeasureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_measure);

        // Run Duration Edit Text
        EditText duration = (EditText) findViewById(R.id.runMeasure_etxt_Duration);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDurationDialog();
            }
        });

    }

    protected void createDurationDialog() {

    }
}
