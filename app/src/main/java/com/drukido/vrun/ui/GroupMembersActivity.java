package com.drukido.vrun.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.UsersListRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class GroupMembersActivity extends AppCompatActivity {

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    UsersListRecyclerAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;

    List<User> mUsersList = null;
    User mCurrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.groupMembers_swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.groupMembers_recyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.groupMembers_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeSwipeLayout();
        fetchCurrentUser();
    }

    private void initializeSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Constants.getSwipeLayoutColors(this));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                fetchCurrentUser();
            }
        });
    }

    private void fetchCurrentUser() {
        mCurrUser = (User) ParseUser.getCurrentUser();
        mCurrUser.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    mCurrUser = (User) object;
                    initializeUsersList();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void initializeUsersList() {
        mCurrUser.getAllUsers(false, new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if ((e == null) && (objects != null)) {
                    mUsersList = objects;
                    fetchUsersList();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void fetchUsersList() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (User user:mUsersList) {
                    try {
                        user.fetchIfNeeded();
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
                    initializeRecycler();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void initializeRecycler() {
        if (mUsersList.size() > 0) {
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new UsersListRecyclerAdapter(mUsersList, this);
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.ItemDecoration itemDecoration =
                    new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecoration);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
