package com.drukido.vrun.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.drukido.vrun.AsyncTasks.IsUserRegisterToRun;
import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.drukido.vrun.entities.User;
import com.drukido.vrun.interfaces.OnAsyncTaskFinishedListener;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ido on 11/21/2015.
 */
public class AttendingsRecyclerAdapter extends
        RecyclerView.Adapter<AttendingsRecyclerAdapter.UserVH> {

    List<ParseUser> mItemsList;
    OnItemClickListener mItemClickListener;
    Context mContext;

    public AttendingsRecyclerAdapter (List<ParseUser> usersList, Context context) {
        this.mItemsList = usersList;
        this.mContext = context;
    }

    @Override
    public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendings_list_item, parent, false);
        UserVH viewHolder = new UserVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserVH holder, int position) {

//        if(mItemsList.get(position).getBoolean(Constants.KEY_IS_IPHONE_USER)) {
//            holder.imgvProfilePhoto.setImageResource(R.drawable.apple);
//        }
        User.setUserProfilePhoto((User)mItemsList.get(position), holder.imgvProfilePhoto);
        holder.imgvProfilePhoto
                .setBorderColor(mContext.getResources().getColor(R.color.colorAccent));
        holder.txtvUserName.setText(mItemsList.get(position).getString(Constants.KEY_NAME));
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

    public String getUserObjectId(int position){
        return mItemsList.get(position).getObjectId();
    }

    public class UserVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtvUserName;
        CircleImageView imgvProfilePhoto;

        public UserVH(View itemView) {
            super(itemView);

            txtvUserName =
                    (TextView) itemView.findViewById(R.id.attendings_list_item_txtv_userName);
            imgvProfilePhoto =
                    (CircleImageView) itemView.findViewById(R.id.attendings_list_item_imgvProfilePhoto);

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
