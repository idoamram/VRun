package com.drukido.vrun.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.ui.MainActivity;
import com.drukido.vrun.ui.NewRunActivity;
import com.drukido.vrun.ui.RunMeasureActivity;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.RunsRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RunFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunFragment extends Fragment {

    private static final String COMING_RUNS = "Coming";
    private static final String PAST_RUNS = "Past";

    private List<Run> mComingRunsList;
    private List<Run> mPastRunsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RunsRecyclerAdapter mPastRunsRecyclerAdapter;
    private RunsRecyclerAdapter mComingRunsRecyclerAdapter;
    private Context mContext;
    private ProgressView mProgressView;
//    private Spinner mSpinner;
    private SwipeRefreshLayout mSwipeLayout;
    private Button mBtnComing;
    private Button mBtnPast;
    private boolean mIsComingSelected = true;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunFragment newInstance(String param1, String param2) {
        RunFragment fragment = new RunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);

        mContext = getActivity();
        mProgressView = (ProgressView) rootView.findViewById(R.id.run_fragment_progressView);
//        mSpinner = (Spinner) rootView.findViewById(R.id.run_fragment_spinnerRunType);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.run_fragment_swipeLayout);
        mBtnComing = (Button) rootView.findViewById(R.id.run_fragment_btnComing);
        mBtnPast = (Button) rootView.findViewById(R.id.run_fragment_btnPast);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.run_fragment_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        rootView.findViewById(R.id.run_fragment_fabBtnNewRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NewRunActivity.class);
                startActivity(i);
            }
        });

        initializeTabButtons();
//        initializeSpinner();
        initializeSwipeRefreshLayout();
        showListProgressView();
        getRunsList(COMING_RUNS);

        return rootView;
    }

    private void initializeTabButtons() {
        mBtnComing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsComingSelected) {
                    mBtnComing.setBackgroundResource(R.color.colorAccent);
                    mBtnPast.setBackgroundResource(R.color.colorPrimaryDark);
                    mIsComingSelected = true;
                    mSwipeLayout.setRefreshing(true);
                    if (mComingRunsList == null) {
                        getRunsList(COMING_RUNS);
                    }
                    else {
                        mSwipeLayout.setRefreshing(true);
                        initializeRecycler(COMING_RUNS);
                    }
                }
            }
        });

        mBtnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsComingSelected) {
                    mBtnPast.setBackgroundResource(R.color.colorAccent);
                    mBtnComing.setBackgroundResource(R.color.colorPrimaryDark);
                    mIsComingSelected = false;
                    mSwipeLayout.setRefreshing(true);
                    if (mPastRunsList == null) {
                        getRunsList(PAST_RUNS);
                    }
                    else {
                        mSwipeLayout.setRefreshing(true);
                        initializeRecycler(PAST_RUNS);
                    }
                }
            }
        });
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorGreenSuccess),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorRedFailed));
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                getRunsList(getSelectedTab());
            }
        });
    }

    private String getSelectedTab() {
        if(mIsComingSelected) {
            return COMING_RUNS;
        } else {
            return PAST_RUNS;
        }
    }

//    private void initializeSpinner() {
//        List<String> list = new ArrayList<>();
//        list.add(COMING_RUNS);
//        list.add(PAST_RUNS);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(mContext,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(dataAdapter);
//        mSpinner.setSelection(0);
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                switch ((String) mSpinner.getSelectedItem()) {
//                    case COMING_RUNS:
//                        mSwipeLayout.setRefreshing(true);
//                        if (mComingRunsList == null) {
//                            getRunsList(COMING_RUNS);
//                        }
//                        else {
//                            mSwipeLayout.setRefreshing(true);
//                            initializeRecycler(COMING_RUNS);
//                        }
//                        break;
//                    case PAST_RUNS:
//                        mSwipeLayout.setRefreshing(true);
//                        if (mPastRunsList == null) {
//                            getRunsList(PAST_RUNS);
//                        }
//                        else {
//                            mSwipeLayout.setRefreshing(true);
//                            initializeRecycler(PAST_RUNS);
//                        }
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }

    private void getRunsList(final String runType) {
        switch (runType) {
            case COMING_RUNS:
                Run.getAllComingRuns(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<Run>() {
                    public void done(List<Run> runsListResult, ParseException e) {
                        if (e == null) {
                            Log.d("run", "Retrieved " + runsListResult.size() + " runs");
                            if (runsListResult.size() > 0) {
                                mComingRunsList = runsListResult;
                                fetchRunsDetails(COMING_RUNS);
                            } else {
                                initializeRecycler(runType);
                            }
                        } else {
                            Log.d("run", "Error: " + e.getMessage());
                            initializeRecycler(runType);
                        }
                    }
                });
                break;
            case PAST_RUNS:
                Run.getAllPastRuns(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<Run>() {
                    public void done(List<Run> runsListResult, ParseException e) {
                        if (e == null) {
                            Log.d("run", "Retrieved " + runsListResult.size() + " runs");
                            if (runsListResult.size() > 0) {
                                mPastRunsList = runsListResult;
                                fetchRunsDetails(PAST_RUNS);
                            } else {
                                hideListProgressView();
                            }
                        } else {
                            Log.d("run", "Error: " + e.getMessage());
                            hideListProgressView();
                        }
                    }
                });
                break;
        }
    }

    private void fetchRunsDetails(final String runType) {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                switch (runType) {
                    case COMING_RUNS:
                        for (Run currRun:mComingRunsList) {
                            try {
                                currRun = currRun.fetch();
                                currRun.setCreator((User) currRun.getCreator().fetch());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        break;
                    case PAST_RUNS:
                        for (Run currRun:mPastRunsList) {
                            try {
                                currRun = currRun.fetch();
                                currRun.setCreator((User) currRun.getCreator().fetch());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        break;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSucceeded) {
                super.onPostExecute(isSucceeded);
                if (isSucceeded) {
                    initializeRecycler(runType);
                } else {
                    hideListProgressView();
                }
            }
        }.execute();
    }

    private void initializeRecycler(String runType) {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        switch (runType) {
            case COMING_RUNS:
                if(mComingRunsList == null) {
                    mComingRunsList = new ArrayList<>();
                }

                mComingRunsRecyclerAdapter = new RunsRecyclerAdapter(mComingRunsList, mContext, true, false);
                mRecyclerView.setAdapter(mComingRunsRecyclerAdapter);
                break;
            case PAST_RUNS:
                mPastRunsRecyclerAdapter = new RunsRecyclerAdapter(mPastRunsList, mContext, false, true);
                mPastRunsRecyclerAdapter.setOnItemClickListener(new RunsRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(getActivity(), RunMeasureActivity.class);
                        i.putExtra(Constants.EXTRA_RUN_ID,
                                mPastRunsRecyclerAdapter.getRunObjectId(position));
                        startActivity(i);
                    }
                });
                mRecyclerView.setAdapter(mPastRunsRecyclerAdapter);
                break;
        }

//        mRunsRecyclerAdapter = new RunsRecyclerAdapter(mRunsList, mContext, false);
//        mRunsRecyclerAdapter.setOnItemClickListener(new RunsRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent i = new Intent(getActivity(), RunMeasureActivity.class);
//                i.putExtra(Constants.EXTRA_RUN_ID, mRunsRecyclerAdapter.getRunObjectId(position));
//                startActivity(i);
//            }
//        });
//        mRecyclerView.setAdapter(mRunsRecyclerAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        hideListProgressView();
    }

    private void showListProgressView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        mSwipeLayout.setRefreshing(true);
    }

    private void hideListProgressView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
        mSwipeLayout.setRefreshing(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
