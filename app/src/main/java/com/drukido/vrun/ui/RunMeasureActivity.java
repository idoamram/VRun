package com.drukido.vrun.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.ui.views.MyTimePickerDialog;
import com.drukido.vrun.ui.views.TimePicker;
import com.drukido.vrun.utils.DateHelper;
import com.drukido.vrun.utils.Duration;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rey.material.widget.ProgressView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

public class RunMeasureActivity extends AppCompatActivity
        implements NumberPickerDialogFragment.NumberPickerDialogHandler,
        HmsPickerDialogFragment.HmsPickerDialogHandler {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final Boolean IS_24_HOURS_FORMAT = true;

    Button btnDistance;
    Button btnDuration;
    Button btnFinish;
    ProgressView mProgressView;
    LinearLayout mMainLayout;

    long mDistance;
    String mDuration;
    String mRunId;
    boolean mIsDistanceValid = false;
    boolean mIsDurationValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_measure);

        Intent intent = getIntent();
        mRunId = intent.getStringExtra(Constants.EXTRA_RUN_ID);

        mProgressView = (ProgressView) findViewById(R.id.runMeasure_progressView);
        mMainLayout = (LinearLayout) findViewById(R.id.runMeasure_mainLinearLayout);
        btnDistance = (Button) findViewById(R.id.runMeasure_btnDistance);
        btnDuration = (Button) findViewById(R.id.runMeasure_btnDuration);
        btnFinish = (Button) findViewById(R.id.runMeasure_btnFinish);

        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumberPickerBuilder numberPickerBuilder = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                numberPickerBuilder.show();
            }
        });

        btnDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hmsPickerBuilder = new HmsPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment);
                hmsPickerBuilder.show();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIsDurationValid) {
                    Snackbar.make(mMainLayout, "Please type duration", Snackbar.LENGTH_LONG).show();
                } else if (!mIsDistanceValid) {
                    Snackbar.make(mMainLayout, "Please type distance", Snackbar.LENGTH_LONG).show();
                } else {
                    Run currRun = ParseObject.createWithoutData(Run.class, mRunId);
                    currRun.setDistance(mDistance);
                    currRun.setDuration(mDuration);
                    saveRun(currRun);
                }
            }
        });
    }

    private void saveRun(Run currRun) {
        showProgressBar();
        currRun.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RunMeasureActivity.this,
                            getString(R.string.run_saved_successfully),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    hideProgressBar();
                    btnFinish.setBackgroundColor(getResources().
                            getColor(R.color.colorRedFailed));
                    btnFinish.setText(getString(R.string.error_click_to_retry));
                }
            }
        });
    }

    private void showProgressBar() {
        mMainLayout.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mMainLayout.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
                                  double fullNumber) {
        if(isNegative){
            Snackbar.make(mMainLayout, "Distance must be positive", Snackbar.LENGTH_LONG).show();
        }
        else {
            String distance = getString(R.string.how_much_in_meters) + "\n" +String.valueOf(number) +
                    " (" + String.valueOf((((double)(number))/1000)) + " KM)";
            mDistance = number;
            btnDistance.setText(distance);
            btnDistance.setBackgroundColor(getResources().getColor(R.color.colorGreenSuccess));
            mIsDistanceValid = true;
        }
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        Duration duration = new Duration();
        duration.setHours(hours);
        duration.setMinutes(minutes);
        duration.setSeconds(seconds);
        mDuration = duration.toString();
        String strDuration = getString(R.string.for_how_long) + "\n" + duration.toString();
        btnDuration.setText(strDuration);
        btnDuration.setBackgroundColor(getResources().getColor(R.color.colorGreenSuccess));
        mIsDurationValid = true;
    }
}
