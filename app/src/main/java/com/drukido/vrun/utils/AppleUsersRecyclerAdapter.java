package com.drukido.vrun.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.parse.ParseException;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;

public class AppleUsersRecyclerAdapter extends
        RecyclerView.Adapter<AppleUsersRecyclerAdapter.UserVH> {

    List<User> mUsersList;
    List<User> mAttendingList;
    List<User> mNotAttendingList;
    Run mCurrRun;
    OnItemClickListener mItemClickListener;
    Context mContext;

    public AppleUsersRecyclerAdapter(Context context,
                                     Run currRun,
                                     List<User> appleUsersList,
                                     List<User> attendingUsers,
                                     List<User> notAttendingUsers) {
        this.mContext = context;
        this.mCurrRun = currRun;
        this.mUsersList = appleUsersList;
        this.mAttendingList = attendingUsers;
        this.mNotAttendingList = notAttendingUsers;
    }

    @Override
    public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apple_user_list_item, parent, false);
        UserVH viewHolder = new UserVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserVH holder, final int position) {

        // User name
        holder.txtvUserName.setText(mUsersList.get(position).getName());

        // Get and show attending status
        new AsyncTask<Void,Void,Integer>() {

            public final int RESULT_FAILED = 0;
            public final int RESULT_ATTENDING = 1;
            public final int RESULT_NOT_ATTENDING = 2;
            public final int RESULT_NO_STATUS = 3;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    if (mAttendingList != null) {
                        for(User user : mAttendingList) {
                            if (user.getObjectId().equals(mUsersList.get(position).getObjectId())) {
                                return RESULT_ATTENDING;
                            }
                        }
                    }

                    if (mNotAttendingList != null) {
                        for(User user : mNotAttendingList) {
                            if (user.getObjectId().equals(mUsersList.get(position).getObjectId())) {
                                return RESULT_NOT_ATTENDING;
                            }
                        }
                    }

                    return RESULT_NO_STATUS;

                } catch (Exception e) {
                    e.printStackTrace();
                    return RESULT_FAILED;
                }
            }

            @Override
            protected void onPostExecute(Integer resultCode) {
                super.onPostExecute(resultCode);

                if (resultCode != RESULT_FAILED) {
                    holder.progressBar.setVisibility(View.GONE);

                    if (resultCode == RESULT_ATTENDING) {
                        holder.showAsAttending();
                    }
                    else if (resultCode == RESULT_NOT_ATTENDING) {
                        holder.showAsNotAttending();
                    }
                }
            }
        }.execute();

        if (mCurrRun != null) {
            holder.imgBtnAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.isUserAttending) {
                        ArrayList<User> notAttendingUsers = mCurrRun.getNotAttending();
                        ArrayList<User> attendingUsers = mCurrRun.getAttending();

                        if(attendingUsers != null){
                            boolean isUserAttending = false;

                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    isUserAttending = true;
                                    break;
                                }
                            }

                            if (!isUserAttending) {
                                attendingUsers.add(mUsersList.get(position));
                            }
                        } else {
                            attendingUsers = new ArrayList<>();
                            attendingUsers.add(mUsersList.get(position));
                        }

                        if(notAttendingUsers == null){
                            notAttendingUsers = new ArrayList<>();
                        }

                        try {
                            for(User currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    notAttendingUsers.remove(notAttendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        mCurrRun.setAttending(attendingUsers);
                        mCurrRun.setNotAttending(notAttendingUsers);
                        mCurrRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    holder.showAsAttending();
                                    Toast.makeText(mContext, "Attending!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // Already attending
                    else {
                        ArrayList<User> attendingUsers = mCurrRun.getAttending();

                        try {
                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    attendingUsers.remove(attendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        mCurrRun.setAttending(attendingUsers);
                        mCurrRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    holder.isUserAttending = false;
                                    holder.imgBtnAttending
                                            .setImageResource(R.drawable.head_attending_gray);
                                    Toast.makeText(mContext, "Attending was canceled",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });

            holder.imgBtnNotAttending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.isUserNotAttending) {
                        ArrayList<User> notAttendingUsers = mCurrRun.getNotAttending();
                        ArrayList<User> attendingUsers = mCurrRun.getAttending();

                        if(notAttendingUsers != null){
                            boolean isUserNotAttending = false;

                            for(User currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    isUserNotAttending = true;
                                    break;
                                }
                            }

                            if (!isUserNotAttending) {
                                notAttendingUsers.add(mUsersList.get(position));
                            }
                        } else {
                            notAttendingUsers = new ArrayList<>();
                            notAttendingUsers.add(mUsersList.get(position));
                        }

                        if(attendingUsers == null){
                            attendingUsers = new ArrayList<>();
                        }

                        try {
                            for(User currUser:attendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    attendingUsers.remove(attendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        mCurrRun.setAttending(attendingUsers);
                        mCurrRun.setNotAttending(notAttendingUsers);
                        mCurrRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                holder.showAsNotAttending();
                                Toast.makeText(mContext, "Not attending...\n" +
                                                "Tell him / her to think twice",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    // User already not attending
                    else {
                        ArrayList<User> notAttendingUsers = mCurrRun.getNotAttending();

                        try {
                            for(User currUser:notAttendingUsers) {
                                if(currUser.getObjectId()
                                        .equals(mUsersList.get(position).getObjectId())) {
                                    notAttendingUsers.remove(notAttendingUsers.indexOf(currUser));
                                    break;
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        mCurrRun.setNotAttending(notAttendingUsers);
                        mCurrRun.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                holder.isUserNotAttending = false;
                                holder.imgBtnNotAttending.
                                        setImageResource(R.drawable.head_not_attending_gray);
                                Toast.makeText(mContext, "So, you're coming?",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } else {
            View.OnClickListener errorClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Cannot set status" +
                            "\n right now...", Toast.LENGTH_LONG).show();
                }
            };

            holder.imgBtnAttending.setOnClickListener(errorClickListener);
            holder.imgBtnNotAttending.setOnClickListener(errorClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
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

    public String getUserObjectId(int position){
        return mUsersList.get(position).getObjectId();
    }

    public class UserVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtvUserName;
        ImageView imgvProfilePhoto;
        ImageButton imgBtnAttending;
        ImageButton imgBtnNotAttending;
        ProgressBar progressBar;

        public boolean isUserAttending = false;
        public boolean isUserNotAttending = false;

        public UserVH(View itemView) {
            super(itemView);

            txtvUserName =
                    (TextView) itemView.findViewById(R.id.apple_user_item_txtvUserName);
            imgvProfilePhoto =
                    (ImageView) itemView.findViewById(R.id.apple_user_item_imgvProfilePhoto);
            imgBtnAttending =
                    (ImageButton) itemView.findViewById(R.id.apple_user_item_btnAttending);
            imgBtnNotAttending =
                    (ImageButton) itemView.findViewById(R.id.apple_user_item_btnNotAttending);
            progressBar =
                    (ProgressBar) itemView.findViewById(R.id.apple_user_item_progressBar);

            itemView.setOnClickListener(this);
        }

        public void showAsAttending() {
            isUserAttending = true;
            isUserNotAttending = false;

            imgBtnAttending.setImageResource(R.drawable.head_attending);
            imgBtnNotAttending.setImageResource(R.drawable.head_not_attending_gray);
        }

        public void showAsNotAttending() {
            isUserNotAttending = true;
            isUserAttending = false;

            imgBtnAttending.setImageResource(R.drawable.head_attending_gray);
            imgBtnNotAttending.setImageResource(R.drawable.head_not_attending);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }
    }
}
