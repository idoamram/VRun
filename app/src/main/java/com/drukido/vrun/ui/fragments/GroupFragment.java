package com.drukido.vrun.ui.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Group;
import com.drukido.vrun.entities.GroupEvent;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.MainActivity;
import com.drukido.vrun.ui.PhotoViewerActivity;
import com.drukido.vrun.utils.DateHelper;
import com.drukido.vrun.utils.Duration;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
public class GroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

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

        initializeGroupPhoto();
        initializeSwipeLayout();
        initializeCards();

        return rootView;
    }

    private void initializeGroupPhoto() {
        mImgvGroupPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        Group.setGroupPhotoToImageView(((User) ParseUser.getCurrentUser()).getGroup(), mImgvGroupPhoto);
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
                        FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from library")) {
                    Intent intent =
                            new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void initializeSwipeLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorGreenSuccess),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorRedFailed));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                initializeGroupPhoto();
                initializeCards();
            }
        });
    }

    private void initializeCards() {

        isGroupFetched = false;
        isLastRunFetched = false;
        isBestRunFetched = false;
        isComingEventFetched = false;

        // Get last run
        Run.getLastRunInBackground(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<Run>() {
            @Override
            public void done(List<Run> runs, ParseException e) {
                if (e == null) {
                    if (runs.size() > 0) {
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

                        if (runs.get(0).getRunTitle() != "") {
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
        Run.getBestRunInBackground(Constants.VRUN_GROUP_OBJECT_ID, new FindCallback<Run>() {
            @Override
            public void done(final List<Run> runs, ParseException e) {
                if (e == null) {
                    if (runs.size() > 0) {
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

                        if (runs.get(0).getRunTitle() != "") {
                            (mCardViewBestRun
                                    .findViewById(R.id.card_group_bestRun_txtvTitle))
                                    .setVisibility(View.VISIBLE);
                            ((TextView) mCardViewBestRun
                                    .findViewById(R.id.card_group_bestRun_txtvTitle))
                                    .setText(runs.get(0).getRunTitle());
                        }

                        ParseUser user = ParseUser.getCurrentUser();
                        Group userGroup = (Group) user.getParseObject("group");
                        userGroup.fetchInBackground(new GetCallback<Group>() {
                            @Override
                            public void done(Group group, ParseException e) {
                                if (e == null) {
                                    // Initialize Donut progress bar
                                    double targetDistance = group.getTargetDistance();
                                    double bestDistance = runs.get(0).getDistance();
                                    double progress = (bestDistance % targetDistance) / 100;
                                    if (progress > 100) {
                                        progress = 100;
                                    }

                                    ((DonutProgress) mCardViewProgress
                                            .findViewById(R.id.group_donutProgress))
                                            .setProgress((int) progress);
                                    ((DonutProgress) mCardViewProgress
                                            .findViewById(R.id.group_donutProgress))
                                            .setTextColor(getResources().getColor(R.color.colorGreenSuccess));

                                    // Initialize group name
                                    ((TextView) mCardViewProgress
                                            .findViewById(R.id.group_txtvGroupName))
                                            .setText(group.getName());

                                    // Initialize group progress
                                    ((TextView) mCardViewProgress
                                            .findViewById(R.id.group_txtvProgress))
                                            .setText(String.valueOf("Progress: " +
                                                    (bestDistance / 1000) + " / " +
                                                    (targetDistance / 1000) + " KM"));

                                } else {
                                    ((LinearLayout) mCardViewProgress.getParent())
                                            .setVisibility(View.GONE);
                                }

                                isGroupFetched = true;
                                checkIfAllFetched();
                            }
                        });

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

                        ((TextView) mCardViewComingEvent
                                .findViewById(R.id.card_group_comingEvent_txtvLocationTitle))
                                .setText(events.get(0).getLocationTitle());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Group.saveCurrentUserGroupPhoto(byteArray, new OnAsyncTaskFinishedListener() {
                    @Override
                    public void onSuccess(Object result) {
                        mImgvGroupPhoto.setImageBitmap(thumbnail);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getActivity(), "Error while trying upload your photo",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getActivity().getContentResolver()
                        .query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                final String picturePath = c.getString(columnIndex);
                c.close();
                final Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Group.saveCurrentUserGroupPhoto(byteArray, new OnAsyncTaskFinishedListener() {
                    @Override
                    public void onSuccess(Object result) {
                        mImgvGroupPhoto.setImageBitmap(thumbnail);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getActivity(), "Error while trying upload your photo",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
