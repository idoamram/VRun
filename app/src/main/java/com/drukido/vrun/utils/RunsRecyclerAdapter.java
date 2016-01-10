package com.drukido.vrun.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.swipe.SwipeLayout;
import com.drukido.vrun.AsyncTasks.IsUserRegisterToRun;
import com.drukido.vrun.AsyncTasks.SendPush;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.AttendingActivity;
import com.drukido.vrun.ui.RunMeasureActivity;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;

public class RunsRecyclerAdapter extends RecyclerView.Adapter<RunsRecyclerAdapter.RunVH> {

    List<Run> mItemsList;
    OnItemClickListener mItemClickListener;
    Context mContext;
    Boolean isSwipable;
    Boolean mIsPastRuns;

    public RunsRecyclerAdapter (List<Run> runsList, Context context, Boolean isSwipable, Boolean isPastRuns) {
        this.mItemsList = runsList;
        this.mContext = context;
        this.isSwipable = isSwipable;
        this.mIsPastRuns = isPastRuns;
    }

    @Override
    public RunVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.runs_list_item_swipe, parent, false);
        return new RunVH(view);
    }

    @Override
    public void onBindViewHolder(final RunVH holder, final int position) {
        holder.currRun = mItemsList.get(position);

        new IsUserRegisterToRun(holder.currRun, ParseUser.getCurrentUser(),
                new OnAsyncTaskFinishedListener() {
            @Override
            public void onSuccess(Object result) {
                holder.isStatusWasFetched = true;
                switch ((int)result) {
                    case IsUserRegisterToRun.RESULT_ATTENDING:
                            holder.showAsAttending();
                        break;
                    case IsUserRegisterToRun.RESULT_NOT_ATTENDING:
                            holder.showAsNotAttending();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        }).execute();

        String strRunTitle = holder.currRun.getRunTitle();
        if(strRunTitle != null) {
            holder.txtvTitle.setVisibility(View.VISIBLE);
            holder.txtvTitle.setText(strRunTitle);
        }
        holder.txtvDate.setText(DateHelper.getDateStringFromDate(holder.currRun.getRunTime()));
        holder.txtvTime.setText(DateHelper.getTimeStringFromDate(holder.currRun.getRunTime()));
        String distance =
                String.valueOf((((double) holder.currRun.getTargetDistance()) / 1000)) + " KM";
        holder.txtvTargetDistance.setText(distance);
        Duration duration = Duration.fromString(holder.currRun.getTargetDuration());
        holder.txtvTargetDuration.setText(duration.toPresentableString());
        holder.currRun.getCreator().fetchIfNeededInBackground(new GetCallback<User>() {
            @Override
            public void done(User object, ParseException e) {
                if (e == null) {
                    holder.txtvUserName.setText(object.getName());
                }
            }
        });

        holder.showMeasuredDetailsIfNeed();
//        if(holder.currRun.getIsMeasured()){
//            String measuredDistance = String.valueOf((((double) holder.currRun.getDistance()) / 1000)) + " KM";
//            holder.txtvMeasuredDistance.setText(measuredDistance);
//            Duration measuredDuration = Duration.fromString(holder.currRun.getDuration());
//            holder.txtvMeasuredDuration.setText(measuredDuration.toPresentableString());
//            holder.lnrLayoutMesuredDetails.setVisibility(View.VISIBLE);
//        }

        if (isSwipable) {
            holder.btnAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!holder.isUserAttending) {
                        ArrayList<User> notAttendingUsers = holder.currRun.getNotAttending();
                        ArrayList<User> attendingUsers = holder.currRun.getAttending();

                        if(attendingUsers != null){
                            boolean isUserAttending = false;

                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    isUserAttending = true;
                                    break;
                                }
                            }

                            if (!isUserAttending) {
                                attendingUsers.add((User) ParseUser.getCurrentUser());
                            }
                        } else {
                            attendingUsers = new ArrayList<>();
                            attendingUsers.add((User) ParseUser.getCurrentUser());
                        }

                        if(notAttendingUsers == null){
                            notAttendingUsers = new ArrayList<>();
                        }

                        try {
                            for(ParseUser currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    notAttendingUsers.remove(notAttendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){}

                        holder.currRun.setAttending(attendingUsers);
                        holder.currRun.setNotAttending(notAttendingUsers);
                        holder.currRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
//                                    holder.isUserAttending = true;
//                                    holder.isUserNotAttending = false;
    //
    //                                holder.btnNotAttending.setImageResource(R.mipmap.ic_thumb_down_white);
    //                                holder.mainRelativeLayout
    //                                        .setBackgroundResource(R.drawable.run_item_border_attending);
    //                                holder.btnAttending.setImageResource(R.mipmap.ic_thumb_up_black);
                                    holder.showAsAttending();
                                    Toast.makeText(mContext, "You are attending!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // Already attending
                    else {
                        ArrayList<User> attendingUsers = holder.currRun.getAttending();

                        try {
                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    attendingUsers.remove(attendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){}

                        holder.currRun.setAttending(attendingUsers);
                        holder.currRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    holder.isUserAttending = false;
                                    holder.imageViewStatus
                                            .setImageResource(R.drawable.head_dont_know);
                                    holder.btnAttending
                                            .setImageResource(R.drawable.head_attenting_gray);
                                    Toast.makeText(mContext, "You've cancel your attending",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
            holder.btnNotAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!holder.isUserNotAttending) {
                        ArrayList<User> notAttendingUsers = holder.currRun.getNotAttending();
                        ArrayList<User> attendingUsers = holder.currRun.getAttending();

                        if(notAttendingUsers != null){
                            boolean isUserNotAttending = false;

                            for(User currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    isUserNotAttending = true;
                                    break;
                                }
                            }

                            if (!isUserNotAttending) {
                                notAttendingUsers.add((User) ParseUser.getCurrentUser());
                            }
                        } else {
                            notAttendingUsers = new ArrayList<>();
                            notAttendingUsers.add((User) ParseUser.getCurrentUser());
                        }

                        if(attendingUsers == null){
                            attendingUsers = new ArrayList<>();
                        }

                        try {
                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    attendingUsers.remove(attendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){}

                        holder.currRun.setAttending(attendingUsers);
                        holder.currRun.setNotAttending(notAttendingUsers);
                        holder.currRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
    //                            holder.isUserNotAttending = true;
    //                            holder.isUserAttending = false;
    //
    //                            holder.btnAttending.setImageResource(R.mipmap.ic_thumb_up_white);
    //                            holder.mainRelativeLayout
    //                                    .setBackgroundResource(R.drawable.run_item_border_not_attending);
    //                            holder.btnNotAttending.setImageResource(R.mipmap.ic_thumb_down_black);
                                holder.showAsNotAttending();
                                Toast.makeText(mContext, "You are not attending...\nthink twice",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    // User already not attending
                    else {
                        ArrayList<User> notAttendingUsers = holder.currRun.getNotAttending();

                        try {
                            for(User currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(ParseUser.getCurrentUser().getObjectId())) {
                                    notAttendingUsers.remove(notAttendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){}

                        holder.currRun.setNotAttending(notAttendingUsers);
                        holder.currRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                holder.isUserNotAttending = false;
                                holder.imageViewStatus.setImageResource(R.drawable.head_dont_know);
                                holder.btnNotAttending.
                                        setImageResource(R.drawable.head_not_attenting_gray);
                                Toast.makeText(mContext, "You've cancel your not attending respond",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
            holder.btnWatchAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, AttendingActivity.class);
                    i.putExtra(Constants.EXTRA_RUN_ID, mItemsList.get(position).getObjectId());
                    mContext.startActivity(i);
                }
            });
            //set show mode.
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,
                    holder.swipeLayout.findViewById(R.id.run_list_item_bottom_wrapper));
            holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    // When the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });
        } else {
            holder.swipeLayout.setSwipeEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public String getRunObjectId(int position){
        return mItemsList.get(position).getObjectId();
    }

    public class RunVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        Run currRun;
        boolean isUserAttending = false;
        boolean isUserNotAttending = false;
        boolean isStatusWasFetched = false;

        ImageButton btnWatchAttending;
        ImageButton btnAttending;
        ImageButton btnNotAttending;
        ImageView imageViewStatus;
        TextView txtvTitle;
        TextView txtvDate;
        TextView txtvTime;
        TextView txtvTargetDuration;
        TextView txtvTargetDistance;
        TextView txtvUserName;
        SwipeLayout swipeLayout;
        RelativeLayout mainRelativeLayout;
        LinearLayout lnrLayoutMesuredDetails;
        TextView txtvMeasuredDuration;
        TextView txtvMeasuredDistance;
        ImageView imgvMeasuredDistance;
        ImageView imgvMeasuredDuration;

        public RunVH(View itemView) {
            super(itemView);

            btnWatchAttending =
                    (ImageButton)itemView.findViewById(R.id.run_list_item_btnWatchAttending);
            btnAttending = (ImageButton)itemView.findViewById(R.id.run_list_item_btnAttending);
            btnNotAttending =
                    (ImageButton)itemView.findViewById(R.id.run_list_item_btnNotAttending);
            imageViewStatus =
                    (ImageView) itemView.findViewById(R.id.run_list_item_imgv_isAttendingIcon);
            txtvTitle = (TextView)itemView.findViewById(R.id.run_list_item_txtvTitle);
            txtvDate = (TextView)itemView.findViewById(R.id.run_list_item_txtvDate);
            txtvTime = (TextView)itemView.findViewById(R.id.run_list_item_txtvTime);
            txtvTargetDuration = (TextView)itemView.findViewById(R.id.run_list_item_txtvTargetDuration);
            txtvTargetDistance = (TextView)itemView.findViewById(R.id.run_list_item_txtvTargetDistance);
            lnrLayoutMesuredDetails = (LinearLayout)itemView.findViewById(R.id.run_list_item_layout_measured_details);
            txtvMeasuredDuration = (TextView)itemView.findViewById(R.id.run_list_item_txtvMeasuredDuration);
            txtvMeasuredDistance = (TextView)itemView.findViewById(R.id.run_list_item_txtvMeasuredDistance);
            imgvMeasuredDistance = (ImageView)itemView.findViewById(R.id.run_list_item_imgv_measuredDistance);
            imgvMeasuredDuration = (ImageView)itemView.findViewById(R.id.run_list_item_imgv_measuredDuration);
            txtvUserName = (TextView)itemView.findViewById(R.id.run_list_item_txtvUserName);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.run_list_item_swipeLayout);
            mainRelativeLayout = (RelativeLayout)
                    itemView.findViewById(R.id.run_list_item_relativeLayout_mainLayout);
            mainRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (currRun != null) {
                        if (currRun.getCreator() != null &&
                                ParseUser.getCurrentUser().getObjectId() != null) {
                            if (currRun.getCreator().getObjectId()
                                    .equals(ParseUser.getCurrentUser().getObjectId())) {
                                Snackbar.make(mainRelativeLayout, "Hello Admin!",
                                        Snackbar.LENGTH_SHORT).show();
                                showRunOptionsDialog();
                            } else {
                                Snackbar.make(mainRelativeLayout, "Only the creator can modify " +
                                        "the run", Snackbar.LENGTH_SHORT).show();
                            }

                            return true;
                        }
                    }
                    return false;
                }

            });

            if(mIsPastRuns) {
                mainRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(mContext, RunMeasureActivity.class);
                        i.putExtra(Constants.EXTRA_RUN_ID,
                                mItemsList.get(getAdapterPosition()).getObjectId());
                        mContext.startActivity(i);
                    }
                });
            }

            itemView.setOnClickListener(this);
        }

        private void showMeasuredDetailsIfNeed() {
            try {
                if (currRun.getIsMeasured()) {
                    lnrLayoutMesuredDetails.setVisibility(View.VISIBLE);
                    String distance =
                            String.valueOf((((double) currRun.getDistance()) / 1000)) + " KM";
                    txtvMeasuredDistance.setText(distance);
                    Duration duration = Duration.fromString(currRun.getDuration());
                    txtvMeasuredDuration.setText(duration.toPresentableString());
                    Duration targetDuration = Duration.fromString(currRun.getTargetDuration());

                    if (currRun.getDistance() >= currRun.getTargetDistance()) {
                        imgvMeasuredDistance.setImageResource(R.drawable.polyline_green);
                    } else {
                        imgvMeasuredDistance.setImageResource(R.drawable.polyline_red);
                    }

                    if (targetDuration.getHours() >= duration.getHours()) {
                        if (targetDuration.getMinutes() >= duration.getMinutes()) {
                            if (targetDuration.getSeconds() >= duration.getSeconds()) {
                                imgvMeasuredDuration.setImageResource(R.drawable.stopwatch_green);
                            } else {
                                imgvMeasuredDuration.setImageResource(R.drawable.stopwatch_red);
                            }
                        } else {
                            imgvMeasuredDuration.setImageResource(R.drawable.stopwatch_red);
                        }
                    } else {
                        imgvMeasuredDuration.setImageResource(R.drawable.stopwatch_red);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(Constants.LOG_TAG,"error in showMeasuredDetailsIfNeed", e);
            }
        }

        private void showRunOptionsDialog() {
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_run_options);
            dialog.setTitle("Run creator options");

            // set the custom dialog components - text, image and button
            final EditText editTextMessage =
                    (EditText) dialog.findViewById(R.id.dialog_runOptions_etxtPushMessage);

            ImageButton btnSendPush = (ImageButton) dialog.findViewById(R.id.dialog_runOptions_btnSendPush);
            btnSendPush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((editTextMessage.getText() != null) &&
                            (!editTextMessage.getText().toString().equals(""))) {
                        dialog.dismiss();
                        sendPush(editTextMessage.getText().toString());
                    } else {
                        Snackbar.make(swipeLayout, "Please type your message",
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });

            Button btnRemoveRun = (Button) dialog.findViewById(R.id.dialog_runOptions_btnRemoveRun);
            btnRemoveRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    final ProgressDialog progressDialog = new ProgressDialog(mContext);
                    progressDialog.setMessage("Removing...");
                    progressDialog.show();

                    currRun.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                mItemsList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                Snackbar.make(swipeLayout, "Run removed successfully!",
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(swipeLayout, "Run removing failed...",
                                        Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

            dialog.show();
        }

        private void sendPush(String message) {

            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Sending...");
            progressDialog.show();

            new SendPush(message, new OnAsyncTaskFinishedListener() {
                @Override
                public void onSuccess(Object result) {
                    progressDialog.dismiss();
                    Snackbar.make(mainRelativeLayout, "Push sent successfully!",
                            Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Snackbar.make(mainRelativeLayout, "Push sending failed...",
                            Snackbar.LENGTH_LONG).show();
                }
            }).execute();
        }

        public void showAsAttending() {
            isUserAttending = true;
            isUserNotAttending = false;

//            mainRelativeLayout
//                    .setBackgroundResource(R.drawable.run_item_border_attending);
            imageViewStatus.setImageResource(R.drawable.head_attenting);
            btnAttending.setImageResource(R.drawable.head_attenting);
            btnNotAttending.setImageResource(R.drawable.head_not_attenting_gray);
        }

        public void showAsNotAttending() {
            isUserNotAttending = true;
            isUserAttending = false;

//            mainRelativeLayout
//                    .setBackgroundResource(R.drawable.run_item_border_not_attending);
            imageViewStatus.setImageResource(R.drawable.head_not_attenting);
            btnNotAttending.setImageResource(R.drawable.head_not_attenting);
            btnAttending.setImageResource(R.drawable.head_attenting_gray);
        }
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
                Toast.makeText(mContext, String.valueOf(currRun.getIsMeasured()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
