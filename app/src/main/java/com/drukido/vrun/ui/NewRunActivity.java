package com.drukido.vrun.ui;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.drukido.vrun.utils.DateHelper;
import com.drukido.vrun.utils.Duration;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.rey.material.widget.ProgressView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

public class NewRunActivity extends AppCompatActivity
        implements CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener,
        NumberPickerDialogFragment.NumberPickerDialogHandler,
        HmsPickerDialogFragment.HmsPickerDialogHandler{

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final Boolean IS_24_HOURS_FORMAT = true;

    Button btnWhen;
    Button btnDistance;
    Button btnDuration;
    Button btnFinish;
    ProgressView mProgressView;
    LinearLayout mMainLayout;

    String mStrDate = "";
    String mStrTime = "";

    long mDistance;
    String mDuration;
    String mStrRunTime;

    private boolean mIsTimeValid = false;
    private boolean mIsDistanceValid = false;
    private boolean mIsDurationValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);

        mProgressView = (ProgressView) findViewById(R.id.newRun_progressView);
        mMainLayout = (LinearLayout) findViewById(R.id.newRun_mainLinearLayout);
        btnWhen = (Button) findViewById(R.id.newRun_btnWhen);
        btnDistance = (Button) findViewById(R.id.newRun_btnDistance);
        btnDuration = (Button) findViewById(R.id.newRun_btnDuration);
        btnFinish = (Button) findViewById(R.id.newRun_btnFinish);

        btnWhen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DateTime now = new DateTime();
                CalendarDatePickerDialogFragment calendarDatePickerDialogFragment =
                        CalendarDatePickerDialogFragment.newInstance(NewRunActivity.this,
                                now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

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
                if(!mIsTimeValid) {
                    Snackbar.make(mMainLayout, "Please type time", Snackbar.LENGTH_LONG).show();
                } else if(!mIsDurationValid) {
                    Snackbar.make(mMainLayout, "Please type duration", Snackbar.LENGTH_LONG).show();
                } else if (!mIsDistanceValid) {
                    Snackbar.make(mMainLayout, "Please type distance", Snackbar.LENGTH_LONG).show();
                } else {
                    ParseUser currUser = ParseUser.getCurrentUser();
                    ArrayList<ParseUser> attendingUsers = new ArrayList<>();
                    attendingUsers.add(currUser);

                    Run newRun = new Run();
                    newRun.setTargetDistance(mDistance);
                    newRun.setRunTime(DateHelper.stringToDate(mStrRunTime));
                    newRun.setTargetDuration(mDuration);
                    newRun.setCreator(currUser);
                    newRun.setGroup((Group) currUser.getParseObject(Constants.KEY_GROUP));
                    newRun.setAttending(attendingUsers);
                    saveNewRun(newRun, currUser);
                }
            }
        });
    }

    private void saveNewRun(Run newRun, final ParseUser currUser) {
        showProgressBar();
        newRun.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(NewRunActivity.this,
                            getString(R.string.run_saved_successfully),
                            Toast.LENGTH_LONG).show();
                    sendPush(currUser);
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

    private void sendPush(ParseUser currUser) {
        Group userGroup = (Group) currUser.getParseObject(Constants.KEY_GROUP);
        final String message = currUser.getString(Constants.KEY_FIRST_NAME) + " " +
                currUser.getString(Constants.KEY_LAST_NAME) + " has convene a new run on " +
                mStrDate + " at " + mStrTime + "." + " See you!";
        userGroup.fetchInBackground(new GetCallback<Group>() {
            @Override
            public void done(Group group, ParseException e) {
                if(e == null){
                    String groupName = group.getName();
                    ParsePush push = new ParsePush();
                    push.setChannel(groupName);
                    push.setMessage(message);
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(NewRunActivity.this,
                                        "Push notifications sent successfully",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(NewRunActivity.this,
                                        "Push notifications sending was failed...",
                                        Toast.LENGTH_LONG).show();
                                Log.e(Constants.LOG_TAG, e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(NewRunActivity.this,
                            "Push notifications sending was failed...", Toast.LENGTH_LONG).show();
                    Log.e(Constants.LOG_TAG, e.getMessage());
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
    public void onResume() {
        // Reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment =
                (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
        RadialTimePickerDialogFragment radialTimePickerDialogFragment =
                (RadialTimePickerDialogFragment) getSupportFragmentManager()
                        .findFragmentByTag(FRAG_TAG_TIME_PICKER);
        if (radialTimePickerDialogFragment != null) {
            radialTimePickerDialogFragment.setOnTimeSetListener(this);
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
                          int dayOfMonth) {
        monthOfYear++;
        mStrDate = String.valueOf(dayOfMonth + "." + monthOfYear);
        mStrRunTime = String.valueOf(dayOfMonth + "-" + monthOfYear + "-" + year);
        DateTime now = DateTime.now();
        RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment
                .newInstance(NewRunActivity.this,
                        now.getHourOfDay(),
                        now.getMinuteOfHour(),
                        IS_24_HOURS_FORMAT);
        timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if(minute < 10){
            mStrTime = String.valueOf(hourOfDay + ":" + "0" + minute);
        } else {
            mStrTime = String.valueOf(hourOfDay + ":" + minute);
        }
        mStrRunTime = mStrRunTime + " " + mStrTime;
        String when = getString(R.string.when) + "\n" + mStrDate + " At " + mStrTime;
        btnWhen.setText(when);
        btnWhen.setBackgroundColor(getResources().getColor(R.color.colorGreenSuccess));
        mIsTimeValid = true;
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
