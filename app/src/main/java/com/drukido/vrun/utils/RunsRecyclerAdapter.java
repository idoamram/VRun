package com.drukido.vrun.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;
import com.rey.material.widget.Button;
import java.util.List;

public class RunsRecyclerAdapter extends RecyclerView.Adapter<RunsRecyclerAdapter.RunVH> {

    List<Run> mItemsList;
    OnItemClickListener mItemClickListener;
    Context mContext;

    public RunsRecyclerAdapter (List<Run> runsList, Context context) {
        this.mItemsList = runsList;
        this.mContext = context;
    }

    @Override
    public RunVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.runs_list_item_swipe, parent, false);
        RunVH viewHolder = new RunVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RunVH holder, int position) {
        Run currRun = mItemsList.get(position);
        holder.txtvDate.setText(DateHelper.dateToString(currRun.getRunTime()));
        holder.txtvDistance.setText(String.valueOf(currRun.getDistance()));
        holder.txtvDuration.setText(currRun.getDuration());
        holder.txtvUserName.setText(currRun.getCreator().getString("name"));
        holder.btnSetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Set status dialog", Toast.LENGTH_LONG).show();
            }
        });
        holder.btnAttendings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Attending dialog", Toast.LENGTH_LONG).show();
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
                layout.findViewById(R.id.run_list_item_btnAttending)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Attending", Toast.LENGTH_LONG).show();
                    }
                });
                layout.findViewById(R.id.run_list_item_btnNotAttending)
                        .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "Not attending", Toast.LENGTH_LONG).show();
                    }
                });
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

    public class RunVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageButton btnAttendings;
        ImageButton btnSetStatus;
        TextView txtvDate;
        TextView txtvDuration;
        TextView txtvDistance;
        TextView txtvUserName;
        SwipeLayout swipeLayout;

        public RunVH(View itemView) {
            super(itemView);

            btnAttendings = (ImageButton)itemView.findViewById(R.id.run_list_item_imgBtnAttendings);
            btnSetStatus = (ImageButton)itemView.findViewById(R.id.run_list_item_imgBtnSetStatus);
            txtvDate = (TextView)itemView.findViewById(R.id.run_list_item_txtvDate);
            txtvDuration = (TextView)itemView.findViewById(R.id.run_list_item_txtvDuration);
            txtvDistance = (TextView)itemView.findViewById(R.id.run_list_item_txtvDistance);
            txtvUserName = (TextView)itemView.findViewById(R.id.run_list_item_txtvUserName);
            swipeLayout = (SwipeLayout)itemView.findViewById(R.id.run_list_item_swipeLayout);
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
