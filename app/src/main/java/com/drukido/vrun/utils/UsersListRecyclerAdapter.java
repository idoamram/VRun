package com.drukido.vrun.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drukido.vrun.Constants;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ido on 11/21/2015.
 */
public class UsersListRecyclerAdapter extends
        RecyclerView.Adapter<UsersListRecyclerAdapter.UserVH> {

    List<User> mItemsList;
    OnItemClickListener mItemClickListener;
    Context mContext;

    public UsersListRecyclerAdapter(List<User> usersList, Context context) {
        this.mItemsList = usersList;
        this.mContext = context;
    }

    @Override
    public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_item, parent, false);
        UserVH viewHolder = new UserVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserVH holder, int position) {

        if(mItemsList.get(position).getBoolean(User.KEY_IS_IPHONE_USER)) {
            holder.imgvProfilePhoto.setImageResource(R.drawable.apple);
        } else {
            mItemsList.get(position).getPicassoProfilePhoto(holder.imgvProfilePhoto, mContext);
        }

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
