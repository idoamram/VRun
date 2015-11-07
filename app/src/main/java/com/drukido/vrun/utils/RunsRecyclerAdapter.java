package com.drukido.vrun.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drukido.vrun.R;
import com.drukido.vrun.entities.Run;

import org.w3c.dom.Text;

import java.util.List;

public class RunsRecyclerAdapter extends RecyclerView.Adapter<RunsRecyclerAdapter.RunVH> {

    List<Run> mItemsList;
    OnItemClickListener mItemClickListener;

    @Override
    public RunVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RunVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
        TextView txtvDate;
        TextView txtvDuration;
        TextView txtvDistance;
        TextView txtvUserName;

        public RunVH(View itemView) {
            super(itemView);

            btnAttendings = (ImageButton)itemView.findViewById(R.id.run_list_item_imgBtnAttendings);
            txtvDate = (TextView)itemView.findViewById(R.id.run_list_item_txtvDate);
            txtvDuration = (TextView)itemView.findViewById(R.id.run_list_item_txtvDuration);
            txtvDistance = (TextView)itemView.findViewById(R.id.run_list_item_txtvDistance);
            txtvUserName = (TextView)itemView.findViewById(R.id.run_list_item_txtvUserName);
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
