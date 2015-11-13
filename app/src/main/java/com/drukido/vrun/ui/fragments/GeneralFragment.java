package com.drukido.vrun.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.utils.DividerItemDecoration;
import com.drukido.vrun.utils.RunsRecyclerAdapter;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class GeneralFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Run> mRunsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RunsRecyclerAdapter mRunsRecyclerAdapter;
    private Context mContext;
    private DonutProgress mDonutProgress;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GeneralFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        mContext = getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.general_recyclerView);
        mDonutProgress = (DonutProgress) rootView.findViewById(R.id.general_donutProgress);
        initializeArcProgress();
        getRunsList();
        return rootView;
    }

    private void initializeArcProgress() {
        ParseUser user = ParseUser.getCurrentUser();
        Group userGroup = (Group) user.getParseObject("group");
        userGroup.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    long targetDistance = ((Group)object).getTargetDistance();
                    long bestDistance = ((Group)object).getBestDistance();
                    double progress = (bestDistance % targetDistance) / 100;
                    if (progress > 100) {
                        progress = 100;
                    }

                    mDonutProgress.setProgress((int) progress);
                    mDonutProgress.setTextColor(Color.WHITE);
                }
            }
        });
    }

    private void getRunsList() {
        ParseQuery<Run> query = ParseQuery.getQuery(Run.class);
//        query.whereEqualTo(Run.KEY_GROUP,
//                ParseObject.createWithoutData("Run",Constants.VRUN_GROUP_OBJECT_ID));
//        query.whereLessThanOrEqualTo(Run.KEY_RUN_TIME, new Date());
        query.findInBackground(new FindCallback<Run>() {
            public void done(List<Run> runsListResult, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + runsListResult.size() + " runs");
                    if (runsListResult.size() > 0) {
                        mRunsList = runsListResult;
                        fetchRunsDetails();
                    }
                } else {
                    Log.d("run", "Error: " + e.getMessage());
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
                        currRun.getCreator().fetch();
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
                }
            }
        }.execute();
    }

    private void initializeRecycler() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRunsRecyclerAdapter = new RunsRecyclerAdapter(mRunsList, mContext);
        mRecyclerView.setAdapter(mRunsRecyclerAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
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
