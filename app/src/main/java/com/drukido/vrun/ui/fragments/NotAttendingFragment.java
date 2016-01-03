package com.drukido.vrun.ui.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.utils.UsersListRecyclerAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotAttendingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mRunId;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private UsersListRecyclerAdapter mAdapter;

    private Context mContext;
    private List<User> mNotAttendingList;

    public NotAttendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runId Parameter 1.
     * @return A new instance of fragment AttendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotAttendingFragment newInstance(String runId) {
        NotAttendingFragment fragment = new NotAttendingFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_RUN_ID, runId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRunId = getArguments().getString(Constants.EXTRA_RUN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_not_attending, container, false);

        mContext = getActivity();

        mSwipeRefreshLayout =
                (SwipeRefreshLayout) rootView.findViewById(R.id.frg_notAttending_swipeRefreshLayout);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.frg_notAttending_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        initializeSwipeRefreshLayout();
        initializeData();

        return rootView;
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(Constants.getSwipeLayoutColors(mContext));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                initializeData();
            }
        });
    }

    private void initializeData() {
        Run currRun = Run.createWithoutData(Run.class, mRunId);
        currRun.fetchInBackground(new GetCallback<Run>() {
            @Override
            public void done(Run run, ParseException e) {
                if (e == null) {
                    if (run.getNotAttending() != null) {
                        mNotAttendingList = run.getNotAttending();
                        fetchUsers();
                    } else {
                        Snackbar.make(mSwipeRefreshLayout, "No one is not attending",
                                Snackbar.LENGTH_INDEFINITE).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Snackbar.make(mSwipeRefreshLayout, "There was an error",
                            Snackbar.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void fetchUsers() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (User user:mNotAttendingList) {
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
                    Snackbar.make(mSwipeRefreshLayout, "There was an error",
                            Snackbar.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void initializeRecycler() {
        if (mNotAttendingList == null || mNotAttendingList.size() == 0) {
            mNotAttendingList = new ArrayList<>();
        }

//        mLinearLayoutManager = new LinearLayoutManager(mContext);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new UsersListRecyclerAdapter(mNotAttendingList, mContext);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
