package com.drukido.vrun.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.drukido.vrun.AsyncTasks.IsUserRegisterToRun;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.drukido.vrun.ui.WatchAttendingActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class RunsRecyclerAdapter extends RecyclerView.Adapter<RunsRecyclerAdapter.RunVH> {

    List<Run> mItemsList;
    OnItemClickListener mItemClickListener;
    Context mContext;
    Boolean isSwipable;

    public RunsRecyclerAdapter (List<Run> runsList, Context context, Boolean isSwipable) {
        this.mItemsList = runsList;
        this.mContext = context;
        this.isSwipable = isSwipable;
    }

    @Override
    public RunVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.runs_list_item_swipe, parent, false);
        RunVH viewHolder = new RunVH(view);
        return viewHolder;
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
                        holder.isUserAttending = true;

                        holder.mainRelativeLayout
                                .setBackgroundResource(R.drawable.run_item_border_attending);
                        holder.btnAttending.setImageResource(R.mipmap.ic_thumb_up_black);
                        break;
                    case IsUserRegisterToRun.RESULT_NOT_ATTENDING:
                        holder.isUserNotAttending = true;

                        holder.mainRelativeLayout
                                .setBackgroundResource(R.drawable.run_item_border_not_attending);
                        holder.btnNotAttending.setImageResource(R.mipmap.ic_thumb_down_black);
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
        String distance = String.valueOf((((double) holder.currRun.getTargetDistance()) / 1000)) + " KM";
        holder.txtvDistance.setText(distance);
        Duration duration = Duration.fromString(holder.currRun.getTargetDuration());
        holder.txtvDuration.setText(duration.toPresentableString());
        holder.txtvUserName.setText(holder.currRun.getCreator().getString("name"));
        if (isSwipable) {
            holder.btnAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<ParseUser> notAttendingUsers = holder.currRun.getNotAttending();
                    ArrayList<ParseUser> attendingUsers = holder.currRun.getAttending();

                    if(attendingUsers != null){
                        boolean isUserAttending = false;

                        for(ParseUser currUser:attendingUsers) {
                            if(currUser.getObjectId()
                                    .equals(ParseUser.getCurrentUser().getObjectId())) {
                                isUserAttending = true;
                                break;
                            }
                        }

                        if (!isUserAttending) {
                            attendingUsers.add(ParseUser.getCurrentUser());
                        }
                    } else {
                        attendingUsers = new ArrayList<>();
                        attendingUsers.add(ParseUser.getCurrentUser());
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
                                holder.isUserAttending = true;
                                holder.isUserNotAttending = false;

                                holder.btnNotAttending.setImageResource(R.mipmap.ic_thumb_down_white);
                                holder.mainRelativeLayout
                                        .setBackgroundResource(R.drawable.run_item_border_attending);
                                holder.btnAttending.setImageResource(R.mipmap.ic_thumb_up_black);
                                Toast.makeText(mContext, "You are attending!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            holder.btnNotAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<ParseUser> notAttendingUsers = holder.currRun.getNotAttending();
                    ArrayList<ParseUser> attendingUsers = holder.currRun.getAttending();

                    if(notAttendingUsers != null){
                        boolean isUserNotAttending = false;

                        for(ParseUser currUser:notAttendingUsers) {
                            if(currUser.getObjectId()
                                    .equals(ParseUser.getCurrentUser().getObjectId())) {
                                isUserNotAttending = true;
                                break;
                            }
                        }

                        if (!isUserNotAttending) {
                            notAttendingUsers.add(ParseUser.getCurrentUser());
                        }
                    } else {
                        notAttendingUsers = new ArrayList<>();
                        notAttendingUsers.add(ParseUser.getCurrentUser());
                    }

                    if(attendingUsers == null){
                        attendingUsers = new ArrayList<>();
                    }

                    try {
                        for(ParseUser currUser:attendingUsers) {
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
                            holder.isUserNotAttending = true;
                            holder.isUserAttending = false;

                            holder.btnAttending.setImageResource(R.mipmap.ic_thumb_up_white);
                            holder.mainRelativeLayout
                                    .setBackgroundResource(R.drawable.run_item_border_not_attending);
                            holder.btnNotAttending.setImageResource(R.mipmap.ic_thumb_down_black);
                            Toast.makeText(mContext, "You are not attending...\nthink twice",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            holder.btnWatchAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, WatchAttendingActivity.class);
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
        TextView txtvTitle;
        TextView txtvDate;
        TextView txtvTime;
        TextView txtvDuration;
        TextView txtvDistance;
        TextView txtvUserName;
        SwipeLayout swipeLayout;
        RelativeLayout mainRelativeLayout;

        public RunVH(View itemView) {
            super(itemView);

            btnWatchAttending =
                    (ImageButton)itemView.findViewById(R.id.run_list_item_btnWatchAttending);
            btnAttending = (ImageButton)itemView.findViewById(R.id.run_list_item_btnAttending);
            btnNotAttending =
                    (ImageButton)itemView.findViewById(R.id.run_list_item_btnNotAttending);
            txtvTitle = (TextView)itemView.findViewById(R.id.run_list_item_txtvTitle);
            txtvDate = (TextView)itemView.findViewById(R.id.run_list_item_txtvDate);
            txtvTime = (TextView)itemView.findViewById(R.id.run_list_item_txtvTime);
            txtvDuration = (TextView)itemView.findViewById(R.id.run_list_item_txtvDuration);
            txtvDistance = (TextView)itemView.findViewById(R.id.run_list_item_txtvDistance);
            txtvUserName = (TextView)itemView.findViewById(R.id.run_list_item_txtvUserName);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.run_list_item_swipeLayout);
            mainRelativeLayout = (RelativeLayout)
                    itemView.findViewById(R.id.run_list_item_relativeLayout_mainLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
    }
}
