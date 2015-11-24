package com.drukido.vrun.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.utils.AttendingsRecyclerAdapter;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.RunsRecyclerAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.rey.material.widget.ProgressView;

import java.util.List;

public class WatchAttendingActivity extends AppCompatActivity {

    private List<ParseUser> mAttendingList;
    private List<ParseUser> mNotAttendingList;

    private ProgressView mProgressViewAttending;
    private ProgressView mProgressViewNotAttending;
    private RecyclerView mAttendingRecyclerView;
    private RecyclerView mNotAttendingRecyclerView;
    private LinearLayoutManager mLinearLayoutManagerAttending;
    private LinearLayoutManager mLinearLayoutManagerNotAttending;
    private AttendingsRecyclerAdapter mAttendingAdapter;
    private AttendingsRecyclerAdapter mNotAttendingAdapter;
    private Context mContext;
    private String mRunId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_attending);

        mContext = this;
        mProgressViewAttending =
                (ProgressView) findViewById(R.id.watchAttending_progressViewAttending);
        mProgressViewNotAttending =
                (ProgressView) findViewById(R.id.watchAttending_progressViewNotAttending);
        mAttendingRecyclerView =
                (RecyclerView) findViewById(R.id.watchAttending_recyclerViewAttending);
        mNotAttendingRecyclerView =
                (RecyclerView) findViewById(R.id.watchAttending_recyclerViewNotAttending);

        Intent intent = getIntent();
        mRunId = intent.getStringExtra(Constants.EXTRA_RUN_ID);

        showAttedingProgressView();
        showNotAttedingProgressView();

        Run currRun = Run.createWithoutData(Run.class, mRunId);
        currRun.fetchInBackground(new GetCallback<Run>() {
            @Override
            public void done(Run run, ParseException e) {
                if(e == null) {
                    if (run.getAttending() != null) {
                        mAttendingList = run.getAttending();
                        fetchAttendingUsers();
                    }
//                    else mAttendingRecyclerView.setVisibility(View.GONE);
                    if (run.getNotAttending() != null) {
                        mNotAttendingList = run.getNotAttending();
                        fetchNotAttendingUsers();
                    }
//                    else mNotAttendingRecyclerView.setVisibility(View.GONE);
                } else {
                    Toast.makeText(WatchAttendingActivity.this, "There was an error",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void fetchAttendingUsers() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (ParseUser user:mAttendingList) {
                    try {
                        user.fetch();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSucceeded) {
                super.onPostExecute(isSucceeded);
                if (isSucceeded) {
                    initializeAttendingRecycler();
                } else {
                    hideAttedingProgressView();
                }
            }
        }.execute();
    }

    private void fetchNotAttendingUsers() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (ParseUser user:mNotAttendingList) {
                    try {
                        user.fetch();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSucceeded) {
                super.onPostExecute(isSucceeded);
                if (isSucceeded) {
                    initializeNotAttendingRecycler();
                } else {
                    hideNotAttedingProgressView();
                }
            }
        }.execute();
    }

    private void initializeAttendingRecycler() {
        if (mAttendingList.size() > 0) {
            mLinearLayoutManagerAttending = new LinearLayoutManager(mContext);
            mAttendingRecyclerView.setLayoutManager(mLinearLayoutManagerAttending);
            mAttendingRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAttendingAdapter = new AttendingsRecyclerAdapter(mAttendingList, mContext);
            mAttendingRecyclerView.setAdapter(mAttendingAdapter);
        } else {
//            mAttendingRecyclerView.setVisibility(View.INVISIBLE);
        }
        hideAttedingProgressView();
    }

    private void initializeNotAttendingRecycler() {
        if (mNotAttendingList.size() > 0) {
            mLinearLayoutManagerNotAttending = new LinearLayoutManager(mContext);
            mNotAttendingRecyclerView.setLayoutManager(mLinearLayoutManagerNotAttending);
            mNotAttendingRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mNotAttendingAdapter = new AttendingsRecyclerAdapter(mNotAttendingList, mContext);
            mNotAttendingRecyclerView.setAdapter(mNotAttendingAdapter);
        } else {
//            mNotAttendingRecyclerView.setVisibility(View.INVISIBLE);
        }
        hideNotAttedingProgressView();
    }

    private void showNotAttedingProgressView() {
        mNotAttendingRecyclerView.setVisibility(View.INVISIBLE);
        mProgressViewNotAttending.setVisibility(View.VISIBLE);
    }

    private void hideNotAttedingProgressView() {
        mNotAttendingRecyclerView.setVisibility(View.VISIBLE);
        mProgressViewNotAttending.setVisibility(View.GONE);
    }

    private void showAttedingProgressView() {
        mAttendingRecyclerView.setVisibility(View.INVISIBLE);
        mProgressViewAttending.setVisibility(View.VISIBLE);
    }

    private void hideAttedingProgressView() {
        mAttendingRecyclerView.setVisibility(View.VISIBLE);
        mProgressViewAttending.setVisibility(View.GONE);
    }
}
