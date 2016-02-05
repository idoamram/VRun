package com.drukido.vrun.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drukido.vrun.AsyncTasks.GetFetchedGroup;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.GroupEvent;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.GetPhotoCallback;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.GroupMembersActivity;
import com.drukido.vrun.ui.MainActivity;
import com.drukido.vrun.ui.PhotoViewerActivity;
import com.drukido.vrun.utils.DateHelper;
import com.drukido.vrun.utils.Duration;
import com.drukido.vrun.utils.PhotosManager;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment implements ImageChooserListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE = 3;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // UI components
    CardView mCardViewProgress;
    CardView mCardViewComingEvent;
    CardView mCardViewBestRun;
    CardView mCardViewLastRun;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView mImgvGroupPhoto;

    boolean isGroupFetched;
    boolean isLastRunFetched;
    boolean isBestRunFetched;
    boolean isComingEventFetched;

    Group mGroup;

    ImageChooserManager mImageChooserManager;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        mCardViewProgress = (CardView) rootView.findViewById(R.id.card_group_progress_cardView);
        mCardViewBestRun = (CardView) rootView.findViewById(R.id.card_group_bestRun_cardView);
        mCardViewComingEvent = (CardView) rootView.findViewById(R.id.card_group_comingEvent_cardView);
        mCardViewLastRun = (CardView) rootView.findViewById(R.id.card_group_lastRun_cardView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.group_swipeLayout);
        mImgvGroupPhoto = (ImageView) rootView.findViewById(R.id.group_imgvGroupPhoto);

        Button groupMembersBtn = (Button) rootView.findViewById(R.id.group_membersButton);
        groupMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GroupMembersActivity.class));
            }
        });

//        initializeGroupPhoto();
//        initializeSwipeLayout();
//        initializeCards();
        initializeSwipeLayout();
        fetchGroup();

        return rootView;
    }

    private void fetchGroup() {

        isGroupFetched = false;

        new GetFetchedGroup(new OnAsyncTaskFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                mGroup = (Group) result;
                isGroupFetched = true;
                initializeGroupPhoto();
                initializeCards();
            }

            @Override
            public void onError(String errorMessage) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }).execute();
    }

    private void initializeGroupPhoto() {
        mImgvGroupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantDataWriting();
            }
        });
        mGroup.getPicassoGroupPhoto(mImgvGroupPhoto, getActivity(), new Callback() {
            @Override
            public void onSuccess() {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
//        PhotosManager.getCurrUserOrGroupPhotoInBackground(getActivity(),
//                PhotosManager.TYPE_GROUP_PHOTO, new GetPhotoCallback() {
//                    @Override
//                    public void onSuccess(Bitmap bitmap) {
//                        mImgvGroupPhoto.setImageBitmap(bitmap);
//                        mImgvGroupPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    }
//
//                    @Override
//                    public void onFetched(Bitmap bitmap) {
//                        mImgvGroupPhoto.setImageBitmap(bitmap);
//                        mImgvGroupPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
    }

    private void initializeSwipeLayout() {
//        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorGreenSuccess),
//                getResources().getColor(R.color.colorPrimary),
//                getResources().getColor(R.color.colorRedFailed));
        mSwipeRefreshLayout.setColorSchemeColors(Constants.getSwipeLayoutColors(getActivity()));
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                fetchGroup();
            }
        });
    }

    private void initializeCards() {

        mSwipeRefreshLayout.setRefreshing(true);

        isLastRunFetched = false;
        isBestRunFetched = false;
        isComingEventFetched = false;

        // Get last run
        Run.getLastRunInBackground(mGroup.getObjectId(), new FindCallback<Run>() {
            @Override
            public void done(List<Run> runs, ParseException e) {
                if (e == null) {
                    if (runs.size() > 0) {
                        ((LinearLayout) mCardViewLastRun.getParent()).setVisibility(View.VISIBLE);
                        ((TextView) mCardViewLastRun.findViewById(R.id.card_group_lastRun_txtvDate))
                                .setText(DateHelper.getDateStringFromDate(runs.get(0).getRunTime()));
                        ((TextView) mCardViewLastRun.findViewById(R.id.card_group_lastRun_txtvTime))
                                .setText(DateHelper.getTimeStringFromDate(runs.get(0).getRunTime()));

                        String distance = String.valueOf((((double)
                                runs.get(0).getDistance()) / 1000)) + " KM";
                        ((TextView) mCardViewLastRun
                                .findViewById(R.id.card_group_lastRun_txtvDistance))
                                .setText(distance);
                        Duration duration = Duration.fromString(runs.get(0).getDuration());
                        ((TextView) mCardViewLastRun
                                .findViewById(R.id.card_group_lastRun_txtvDuration))
                                .setText(duration.toPresentableString());

                        if (runs.get(0).getRunTitle() != null &&
                                !runs.get(0).getRunTitle().equals("")) {
                            (mCardViewLastRun
                                    .findViewById(R.id.card_group_lastRun_txtvTitle))
                                    .setVisibility(View.VISIBLE);
                            ((TextView) mCardViewLastRun
                                    .findViewById(R.id.card_group_lastRun_txtvTitle))
                                    .setText(runs.get(0).getRunTitle());
                        }

                    } else {
                        ((LinearLayout) mCardViewLastRun.getParent()).setVisibility(View.GONE);
                    }
                } else {
                    ((LinearLayout) mCardViewLastRun.getParent()).setVisibility(View.GONE);
                }

                isLastRunFetched = true;
                checkIfAllFetched();
            }
        });

        // Get best run
        Run.getBestRunInBackground(mGroup.getObjectId(), new FindCallback<Run>() {
            @Override
            public void done(final List<Run> runs, ParseException e) {
                if (e == null) {
                    if (runs.size() > 0) {
                        ((LinearLayout) mCardViewBestRun.getParent()).setVisibility(View.VISIBLE);
                        ((TextView) mCardViewBestRun.findViewById(R.id.card_group_bestRun_txtvDate))
                                .setText(DateHelper.getDateStringFromDate(runs.get(0).getRunTime()));
                        ((TextView) mCardViewBestRun.findViewById(R.id.card_group_bestRun_txtvTime))
                                .setText(DateHelper.getTimeStringFromDate(runs.get(0).getRunTime()));

                        String distance = String.valueOf((((double)
                                runs.get(0).getDistance()) / 1000)) + " KM";
                        ((TextView) mCardViewBestRun
                                .findViewById(R.id.card_group_bestRun_txtvDistance))
                                .setText(distance);
                        Duration duration = Duration.fromString(runs.get(0).getDuration());
                        ((TextView) mCardViewBestRun
                                .findViewById(R.id.card_group_bestRun_txtvDuration))
                                .setText(duration.toPresentableString());

                        if (runs.get(0).getRunTitle() != null) {
                            (mCardViewBestRun
                                    .findViewById(R.id.card_group_bestRun_txtvTitle))
                                    .setVisibility(View.VISIBLE);
                            ((TextView) mCardViewBestRun
                                    .findViewById(R.id.card_group_bestRun_txtvTitle))
                                    .setText(runs.get(0).getRunTitle());
                        }

                        // Initialize Donut progress bar
                        double targetDistance = mGroup.getTargetDistance();
                        double bestDistance = runs.get(0).getDistance();
                        double progress = (bestDistance % targetDistance) / 100;
                        if (progress > 100) {
                            progress = 100;
                        }

                        mCardViewProgress.setVisibility(View.VISIBLE);
                        ((DonutProgress) mCardViewProgress
                                .findViewById(R.id.group_donutProgress))
                                .setProgress((int) progress);
                        ((DonutProgress) mCardViewProgress
                                .findViewById(R.id.group_donutProgress))
                                .setTextColor(getResources().getColor(R.color.colorGreenSuccess));

                        // Initialize group name
                        ((TextView) mCardViewProgress
                                .findViewById(R.id.group_txtvGroupName))
                                .setText(mGroup.getName());

                        // Initialize group progress
                        ((TextView) mCardViewProgress
                                .findViewById(R.id.group_txtvProgress))
                                .setText(String.valueOf("Progress: " +
                                        (bestDistance / 1000) + " / " +
                                        (targetDistance / 1000) + " KM"));

//                        User user = (User) ParseUser.getCurrentUser();
//                        Group userGroup = user.getGroup();
//                        userGroup.fetchInBackground(new GetCallback<Group>() {
//                            @Override
//                            public void done(Group group, ParseException e) {
//                                if (e == null) {
//                                    // Initialize Donut progress bar
//                                    double targetDistance = group.getTargetDistance();
//                                    double bestDistance = runs.get(0).getDistance();
//                                    double progress = (bestDistance % targetDistance) / 100;
//                                    if (progress > 100) {
//                                        progress = 100;
//                                    }
//
//                                    ((DonutProgress) mCardViewProgress
//                                            .findViewById(R.id.group_donutProgress))
//                                            .setProgress((int) progress);
//                                    ((DonutProgress) mCardViewProgress
//                                            .findViewById(R.id.group_donutProgress))
//                                            .setTextColor(getResources().getColor(R.color.colorGreenSuccess));
//
//                                    // Initialize group name
//                                    ((TextView) mCardViewProgress
//                                            .findViewById(R.id.group_txtvGroupName))
//                                            .setText(group.getName());
//
//                                    // Initialize group progress
//                                    ((TextView) mCardViewProgress
//                                            .findViewById(R.id.group_txtvProgress))
//                                            .setText(String.valueOf("Progress: " +
//                                                    (bestDistance / 1000) + " / " +
//                                                    (targetDistance / 1000) + " KM"));
//
//                                } else {
//                                    ((LinearLayout) mCardViewProgress.getParent())
//                                            .setVisibility(View.GONE);
//                                }
//
//                                isGroupFetched = true;
//                                checkIfAllFetched();
//                            }
//                        });

                    } else {
                        ((LinearLayout) mCardViewBestRun.getParent()).setVisibility(View.GONE);
                    }
                } else {
                    ((LinearLayout) mCardViewBestRun.getParent()).setVisibility(View.GONE);
                }

                isBestRunFetched = true;
                checkIfAllFetched();
            }
        });

        // Get coming event
        GroupEvent.getComingEvent(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<GroupEvent>() {
            @Override
            public void done(List<GroupEvent> events, ParseException e) {
                if (e == null) {
                    if (events.size() > 0) {
                        ((LinearLayout) mCardViewComingEvent.getParent()).setVisibility(View.VISIBLE);
                        ((TextView) mCardViewComingEvent
                                .findViewById(R.id.card_group_comingEvent_txtvDate))
                                .setText(DateHelper.getDateStringFromDate(events
                                        .get(0).getEventTime()));
                        ((TextView) mCardViewComingEvent
                                .findViewById(R.id.card_group_comingEvent_txtvTime))
                                .setText(DateHelper.getTimeStringFromDate(events
                                        .get(0).getEventTime()));

                        String distance = String.valueOf((((double)
                                events.get(0).getTargetDistance()) / 1000)) + " KM";
                        ((TextView) mCardViewComingEvent
                                .findViewById(R.id.card_group_comingEvent_txtvDistance))
                                .setText(distance);
                        Duration duration = Duration.fromString(events.get(0).getTargetDuration());
                        ((TextView) mCardViewComingEvent
                                .findViewById(R.id.card_group_comingEvent_txtvDuration))
                                .setText(duration.toPresentableString());

                        if (events.get(0).getEventTitle() != null) {
                            (mCardViewComingEvent
                                    .findViewById(R.id.card_group_comingEvent_txtvTitle))
                                    .setVisibility(View.VISIBLE);
                            ((TextView) mCardViewComingEvent
                                    .findViewById(R.id.card_group_comingEvent_txtvTitle))
                                    .setText(events.get(0).getEventTitle());
                        }

                        if (events.get(0).getLocationTitle() != null) {
                            ((TextView) mCardViewComingEvent
                                    .findViewById(R.id.card_group_comingEvent_txtvLocationTitle))
                                    .setText(events.get(0).getLocationTitle());
                        }
                    } else {
                        ((LinearLayout) mCardViewComingEvent.getParent()).setVisibility(View.GONE);
                    }
                } else {
                    ((LinearLayout) mCardViewComingEvent.getParent()).setVisibility(View.GONE);
                }

                isComingEventFetched = true;
                checkIfAllFetched();
            }
        });
    }

    private void checkIfAllFetched() {
        if(isComingEventFetched && isBestRunFetched && isLastRunFetched && isGroupFetched) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

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
                        Bitmap bmp = ((BitmapDrawable) mImgvGroupPhoto.getDrawable()).getBitmap();

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
                    mImageChooserManager = new ImageChooserManager(GroupFragment.this,
                            ChooserType.REQUEST_CAPTURE_PICTURE);
                    mImageChooserManager.setImageChooserListener(GroupFragment.this);
                    try {
                        mImageChooserManager.choose();
                    } catch (ChooserException e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Choose from library")) {
                    mImageChooserManager = new ImageChooserManager(GroupFragment.this,
                            ChooserType.REQUEST_PICK_PICTURE);
                    mImageChooserManager.setImageChooserListener(GroupFragment.this);
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
                    mSwipeRefreshLayout.setRefreshing(true);
                    mGroup.saveGroupPhoto(image.getFilePathOriginal(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getFilePathOriginal());
                                mImgvGroupPhoto.setImageBitmap(bitmap);
                                mImgvGroupPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            } else {
                                Toast.makeText(getActivity(), "Failed to save your photo.\n" +
                                        "Please try again.", Toast.LENGTH_LONG).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
//                    final Bitmap bitmap = BitmapFactory.decodeFile(image.getFilePathOriginal());
//
//                    PhotosManager.saveCurrUserOrGroupPhotoInBackground(getActivity(),
//                            image.getFilePathOriginal(), PhotosManager.TYPE_GROUP_PHOTO,
//                            new OnAsyncTaskFinishedListener() {
//                                @Override
//                                public void onSuccess(Object result) {
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                    mImgvGroupPhoto.setImageBitmap(bitmap);
//                                }
//
//                                @Override
//                                public void onError(String errorMessage) {
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                }
//                            });
//                    PhotosManager.saveCurrUserGroupPhoto(getActivity(),
//                            image.getFilePathOriginal(),
//                            new OnAsyncTaskFinishedListener() {
//                                @Override
//                                public void onSuccess(Object result) {
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                    mImgvGroupPhoto.setImageBitmap(bitmap);
//                                }
//
//                                @Override
//                                public void onError(String errorMessage) {
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                }
//                            });
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//
//                    //this will convert image to byte[]
//                    byte[] byteArrayImage = baos.toByteArray();
//
//                    mSwipeRefreshLayout.setRefreshing(true);
//                    Group.saveCurrentUserGroupPhoto(byteArrayImage,
//                            new OnAsyncTaskFinishedListener() {
//                        @Override
//                        public void onSuccess(Object result) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            mImgvGroupPhoto.setImageBitmap(bitmap);
//                        }
//
//                        @Override
//                        public void onError(String errorMessage) {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                    });
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
                Toast.makeText(getActivity(), "Pick image failed.\nonError called", Toast.LENGTH_LONG).show();
            }
        });
    }
}
