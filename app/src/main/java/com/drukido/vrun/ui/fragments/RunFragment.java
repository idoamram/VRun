package com.drukido.vrun.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.ui.MainActivity;
import com.drukido.vrun.ui.RunMeasureActivity;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.RunsRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.rey.material.widget.ProgressView;

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

    private List<Run> mRunsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RunsRecyclerAdapter mRunsRecyclerAdapter;
    private Context mContext;
    private ProgressView mProgressView;

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.run_fragment_recyclerView);
        mProgressView = (ProgressView) rootView.findViewById(R.id.run_fragment_progressView);

        showListProgressView();
        getRunsList();

        return rootView;
    }

    private void getRunsList() {
        Run.getAllPastRuns(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<Run>() {
            public void done(List<Run> runsListResult, ParseException e) {
                if (e == null) {
                    Log.d("run", "Retrieved " + runsListResult.size() + " runs");
                    if (runsListResult.size() > 0) {
                        mRunsList = runsListResult;
                        fetchRunsDetails();
                    } else {
                        hideListProgressView();
                    }
                } else {
                    Log.d("run", "Error: " + e.getMessage());
                    hideListProgressView();
                }
            }
        });
    }

    private void fetchRunsDetails() {
        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                for (Run currRun:mRunsList) {
                    try {
                        currRun.fetchIfNeeded();
                        currRun.getCreator().fetchIfNeeded();
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
                    hideListProgressView();
                }
            }
        }.execute();
    }

    private void initializeRecycler() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRunsRecyclerAdapter = new RunsRecyclerAdapter(mRunsList, mContext, false);
        mRunsRecyclerAdapter.setOnItemClickListener(new RunsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getActivity(), RunMeasureActivity.class);
                i.putExtra(Constants.EXTRA_RUN_ID, mRunsRecyclerAdapter.getRunObjectId(position));
                startActivity(i);
            }
        });
        mRecyclerView.setAdapter(mRunsRecyclerAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        hideListProgressView();
    }

    private void showListProgressView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void hideListProgressView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
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
