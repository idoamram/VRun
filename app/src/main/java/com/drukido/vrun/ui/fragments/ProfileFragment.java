package com.drukido.vrun.ui.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.utils.RunsAnalyzer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.michelelacorte.elasticprogressbar.ElasticDownloadView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    User mCurrUser;
    Group mGroup;
    CircleImageView mCircleImageView;
    RunsAnalyzer mRunsAnalyzer;

    // Cards
    CardView mCardUserDetails;
    CardView mCardProgress;
    CardView mCardOverall;

    ElasticDownloadView mElasticDownloadView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrUser = (User) User.getCurrentUser();
        mCircleImageView =
                (CircleImageView) rootView.findViewById(R.id.profile_circularImageView_profilePicture);

        mCardUserDetails = (CardView) rootView.findViewById(R.id.card_profile_user_details_cardView);
        mCardProgress = (CardView) rootView.findViewById(R.id.card_profile_progress_cardView);
        mCardOverall = (CardView) rootView.findViewById(R.id.card_profile_overall_cardView);

        mElasticDownloadView = (ElasticDownloadView) rootView.findViewById(R.id.card_profile_progress_elastic_progress_view);

        fetchUser();
        return rootView;
    }

    private void fetchUser() {
        mCurrUser.fetchIfNeededInBackground(new GetCallback<User>() {
            @Override
            public void done(User user, ParseException e) {
                if (e == null) {
                    mCurrUser = user;
                    mGroup = mCurrUser.getGroup();
                    mGroup.fetchInBackground(new GetCallback<Group>() {
                        @Override
                        public void done(Group group, ParseException e) {
                            if (e == null) {
                                mGroup = group;
                                initializeProfilePicture();
                                initializeUserDetailsCard();
                                getRuns();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initializeProfilePicture(){
        try {
            if (mCurrUser.getProfilePhoto() != null) {
                mCurrUser.getPicassoProfilePhoto(mCircleImageView, getActivity());
            } else {
                mCurrUser.fetchInBackground(new GetCallback<User>() {
                    @Override
                    public void done(User user, ParseException e) {
                        if (e == null) {
                            mCurrUser = user;
                            mCurrUser.getPicassoProfilePhoto(mCircleImageView, getActivity());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUserDetailsCard() {
        try {
            mCardUserDetails.findViewById(R.id.card_profile_user_details_mainLinearLayout)
                    .setVisibility(View.INVISIBLE);
            mCardUserDetails.findViewById(R.id.card_profile_user_details_progressBar)
                    .setVisibility(View.GONE);

            ((TextView) mCardUserDetails.findViewById(R.id.card_profile_user_details_txtView_userName))
                    .setText(mCurrUser.getName());
            ((TextView) mCardUserDetails.findViewById(R.id.card_profile_user_details_txtView_email))
                    .setText(mCurrUser.getEmail());
            ((TextView) mCardUserDetails.findViewById(R.id.card_profile_user_details_txtView_phoneNumber))
                    .setText(mCurrUser.getPhoneNumber());

            mCardUserDetails.findViewById(R.id.card_profile_user_details_mainLinearLayout)
                    .setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRuns() {
        mCurrUser.getPastAttendingRuns(new FindCallback<Run>() {
            @Override
            public void done(List<Run> objects, ParseException e) {
                if (e == null) {
                    mRunsAnalyzer = new RunsAnalyzer(objects);
                    initializeRunsCard();
                }
            }
        });
    }

    private void initializeRunsCard() {

        initializeProgressCard();

        if (mRunsAnalyzer.isMeasuredRunsAvailable()) {
            initializeOverallCard();
        } else {
            mCardOverall.setVisibility(View.GONE);
        }
    }

    private void initializeProgressCard() {
        mElasticDownloadView.startIntro();

        ((TextView)(mCardProgress.findViewById(R.id.card_profile_progress_textView_allRunsCount)))
                .setText(mRunsAnalyzer.getAllRunsCount() + " Runs");

        ((TextView)(mCardProgress.findViewById(R.id.card_profile_progress_textView_measuredRunsCount)))
                .setText(mRunsAnalyzer.getMeasuredRunsCount() + " Measured");

        if (mRunsAnalyzer.getBestRun().getDistance() < mGroup.getTargetDistance()) {
            float progress = (float)((((double)(mRunsAnalyzer.getBestRun().getDistance())) /
                    ((double)mGroup.getTargetDistance())) * 100);
            mElasticDownloadView.setProgress(progress);

            ((TextView)(mCardProgress.findViewById(R.id.card_profile_progress_textView_bestDistance)))
                    .setText(String.valueOf(((double)mRunsAnalyzer.getBestRun().getDistance()) / 1000) + "\nkm");
            ((TextView)(mCardProgress.findViewById(R.id.card_profile_progress_textView_groupTarget)))
                    .setText(String.valueOf(((double) mGroup.getTargetDistance()) / 1000) + "\nkm");
        } else {
            mElasticDownloadView.success();
        }
    }

    private void initializeOverallCard() {
        ((TextView)(mCardOverall.findViewById(R.id.card_profile_overall_textView_distance)))
                .setText(String.valueOf(roundTwoDecimals(((double)mRunsAnalyzer.getOverAllDistance()) / 1000)) + " km");
        ((TextView)(mCardOverall.findViewById(R.id.card_profile_overall_textView_distance_avg)))
                .setText(String.valueOf(roundTwoDecimals((mRunsAnalyzer.getOverAllDistanceAverage()) / 1000)) + " km");

        ((TextView)(mCardOverall.findViewById(R.id.card_profile_overall_textView_duration)))
                .setText(mRunsAnalyzer.getOverAllRunTime().toPresentableString());
        ((TextView)(mCardOverall.findViewById(R.id.card_profile_overall_textView_duration_avg)))
                .setText(mRunsAnalyzer.getOverAllRunTimeAverage().toPresentableString());

        ((TextView)(mCardOverall.findViewById(R.id.card_profile_overall_textView_speed_avg)))
                .setText(String.valueOf(roundTwoDecimals(mRunsAnalyzer.getAverageSpeed())) + " km/h");
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}