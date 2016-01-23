package com.drukido.vrun.ui;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.utils.AppleUsersRecyclerAdapter;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.UsersListRecyclerAdapter;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class AppleUserAttendingActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AppleUsersRecyclerAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;

    List<User> mAppleUsersList = null;
    List<User> mRunAttendingUsers = null;
    List<User> mRunNotAttendingUsers = null;
    Run mCurrRun = null;
    String mRunObjectId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apple_user_attending);

        mRunObjectId = getIntent().getStringExtra(Constants.EXTRA_RUN_ID);

        mRecyclerView = (RecyclerView) findViewById(R.id.appleUserAttending_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.appleUserAttending_swipeRefreshLayout);

        setResult(RESULT_OK);

        findViewById(R.id.apple_attending_btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initializeSwipeLayout();
        getData();
    }

    private void initializeSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Constants.getSwipeLayoutColors(this));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (mRunObjectId != null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    getData();
                }
            }
        });
    }

    private void getData() {
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // Get and fetch run
                    mCurrRun = Run.createWithoutData(Run.class, mRunObjectId);
                    mCurrRun.fetchIfNeeded();

                    // Get the group
                    Group group = mCurrRun.getGroup();
                    group.fetchIfNeeded();

                    // Get all group's apple users
                    mAppleUsersList = group.getAllAppleUsers();

                    // Get all run's attending users
                    mRunAttendingUsers = mCurrRun.getAttending();

                    // Get all run's not attending users
                    mRunNotAttendingUsers = mCurrRun.getNotAttending();

                    // Fetch all users
                    for (User user : mAppleUsersList) {
                        user.fetchIfNeeded();
                    }
                    for (User user : mRunAttendingUsers) {
                        user.fetchIfNeeded();
                    }
                    for (User user : mRunNotAttendingUsers) {
                        user.fetchIfNeeded();
                    }

                    return true;

                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    initializeRecycler();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void initializeRecycler() {
        if (mAppleUsersList == null) {
            mAppleUsersList = new ArrayList<>();
        }
        if (mRunAttendingUsers == null) {
            mRunAttendingUsers = new ArrayList<>();
        }
        if (mRunNotAttendingUsers == null) {
            mRunNotAttendingUsers = new ArrayList<>();
        }

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AppleUsersRecyclerAdapter(AppleUserAttendingActivity.this,
                mCurrRun, mAppleUsersList, mRunAttendingUsers, mRunNotAttendingUsers);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
