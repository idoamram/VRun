package com.drukido.vrun.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.ui.PhotoViewerActivity;
import com.drukido.vrun.utils.RunsAnalyzer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.FileOutputStream;
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
public class ProfileFragment extends Fragment implements ImageChooserListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE = 3;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    User mCurrUser;
    Group mGroup;
    CircleImageView mImgvUserPhoto;
    RunsAnalyzer mRunsAnalyzer;

    // Cards
    CardView mCardUserDetails;
    CardView mCardProgress;
    CardView mCardOverall;

    SwipeRefreshLayout mSwipeLayout;
    ElasticDownloadView mElasticDownloadView;
    FloatingActionButton mFabEditProfilePicture;

    ImageChooserManager mImageChooserManager;

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
        mImgvUserPhoto =
                (CircleImageView) rootView.findViewById(R.id.profile_circularImageView_profilePicture);

        mCardUserDetails = (CardView) rootView.findViewById(R.id.card_profile_user_details_cardView);
        mCardProgress = (CardView) rootView.findViewById(R.id.card_profile_progress_cardView);
        mCardOverall = (CardView) rootView.findViewById(R.id.card_profile_overall_cardView);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.profile_swipeRefreshLayout);
        mElasticDownloadView = (ElasticDownloadView) rootView.findViewById(R.id.card_profile_progress_elastic_progress_view);

        mFabEditProfilePicture = (FloatingActionButton) rootView.findViewById(R.id.profile_fab_editProfilePhoto);
        mFabEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantDataWriting();
            }
        });

        initializeSwipeLayout();
        fetchUser();
        return rootView;
    }

    private void initializeSwipeLayout() {
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorGreenSuccess),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorRedFailed));
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                fetchUser();
            }
        });
        mSwipeLayout.setRefreshing(true);
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
                            } else {
                                mSwipeLayout.setRefreshing(false);
                            }
                        }
                    });
                } else {
                    mSwipeLayout.setRefreshing(false);
                }
            }
        });
    }

    private void initializeProfilePicture(){
        try {
            if (mCurrUser.getProfilePhoto() != null) {
                mCurrUser.getPicassoProfilePhoto(mImgvUserPhoto, getActivity());
            } else {
                mCurrUser.fetchInBackground(new GetCallback<User>() {
                    @Override
                    public void done(User user, ParseException e) {
                        if (e == null) {
                            mCurrUser = user;
                            mCurrUser.getPicassoProfilePhoto(mImgvUserPhoto, getActivity());
                        } else {
                            mSwipeLayout.setRefreshing(false);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            mSwipeLayout.setRefreshing(false);
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
            mSwipeLayout.setRefreshing(false);
        }
    }

    private void getRuns() {
        mCurrUser.getPastAttendingRuns(new FindCallback<Run>() {
            @Override
            public void done(List<Run> objects, ParseException e) {
                if (e == null) {
                    mRunsAnalyzer = new RunsAnalyzer(objects);
                    initializeRunsCard();
                } else {
                    mSwipeLayout.setRefreshing(false);
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
            mSwipeLayout.setRefreshing(false);
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

        mSwipeLayout.setRefreshing(false);
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

        mSwipeLayout.setRefreshing(false);
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    /***** Image Chooser *****/
    private void grantDataWriting() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //RUNTIME PERMISSION Android M
            if(PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA)){
                selectImage();
            }else{
                requestPermission(getActivity());
            }
        }
    }

    private void requestPermission(final Context context){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.

            new AlertDialog.Builder(context)
                    .setMessage("You must grant VRun to writing external data for picking a photo")
                    .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)context,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE);
                        }
                    }).show();

        } else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE: {
                if ((grantResults.length == 2 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) && (grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED)
                        ) {
                    Toast.makeText(getActivity(),
                            "get permissions success", Toast.LENGTH_SHORT).show();
                    selectImage();
                } else {
                    Toast.makeText(getActivity(),
                            "get permissions failed", Toast.LENGTH_SHORT).show();
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "View photo", "Take photo", "Choose from library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Group photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("View photo")) {

                    try {
                        Bitmap bmp = ((BitmapDrawable) mImgvUserPhoto.getDrawable()).getBitmap();

                        //Write file
                        String filename = "bitmap.png";
                        FileOutputStream stream =
                                getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        //Cleanup
                        stream.close();

                        //Pop intent
                        Intent startPhotoViewerIntent =
                                new Intent(getActivity(), PhotoViewerActivity.class);
                        startPhotoViewerIntent
                                .putExtra(PhotoViewerActivity.EXTRA_PHOTO_KEY, filename);
                        startActivity(startPhotoViewerIntent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                    }
                } else if (items[item].equals("Take photo")) {
                    mImageChooserManager = new ImageChooserManager(ProfileFragment.this,
                            ChooserType.REQUEST_CAPTURE_PICTURE);
                    mImageChooserManager.setImageChooserListener(ProfileFragment.this);
                    try {
                        mImageChooserManager.choose();
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Choose from library")) {
                    mImageChooserManager = new ImageChooserManager(ProfileFragment.this,
                            ChooserType.REQUEST_PICK_PICTURE);
                    mImageChooserManager.setImageChooserListener(ProfileFragment.this);
                    try {
                        mImageChooserManager.choose();
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK &&
                (requestCode == ChooserType.REQUEST_PICK_PICTURE||
                        requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            mImageChooserManager.submit(requestCode, data);
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    mSwipeLayout.setRefreshing(true);
                    mCurrUser.saveProfilePhoto(image.getFilePathOriginal(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getFilePathOriginal());
                                mImgvUserPhoto.setImageBitmap(bitmap);
                                mImgvUserPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                Toast.makeText(getActivity(), "Failed to save your photo.\n" +
                                        "Please try again.", Toast.LENGTH_LONG).show();
                            }
                            mSwipeLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onError(final String reason) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Show error message
                mSwipeLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Pick image failed.\nonError called", Toast.LENGTH_LONG).show();
            }
        });
    }
}