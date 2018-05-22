package com.example.admin.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListVideoHolder> {
    private Context mContext;
    private List<VideoScreen> mVideoScreens;

    public ListAdapter(Context mContext, List<VideoScreen> mVideoScreens) {
        this.mContext = mContext;
        this.mVideoScreens = mVideoScreens;
    }
    public void registerListerner(OnItemClickListener onItemClickListener){
        this.listener= onItemClickListener;
    }


    @NonNull
    @Override
    public ListVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ListVideoHolder viewHolder = new ListVideoHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListVideoHolder holder, int position) {
        VideoScreen videoScreen =mVideoScreens.get(position);
        if (videoScreen != null) {
            if (position % 2 != 0) {

                holder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.xam));
            }
            holder.tvTitle.setText(videoScreen.getTitle());
            holder.tvTime.setText(videoScreen.getTime());
//            holder.simpleDraweeView.se(videoScreen.getPathFile());
        }
    }



    @Override
    public int getItemCount() {
//        return mVideoScreens.size();
        return 5;
    }

    public class ListVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.simpleDraweeView)
        ImageView simpleDraweeView;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.im_remove)
        ImageView imgRemove;
        @BindView(R.id.im_save)
        ImageView imgSave;
        @BindView(R.id.ctl_item)
        ConstraintLayout constraintLayout;


        public ListVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgRemove.setOnClickListener(this);
            imgSave.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_remove:
                    listener.remove(mVideoScreens.get(getAdapterPosition()));
                    break;
                case R.id.im_save:
                    listener.save(mVideoScreens.get(getAdapterPosition()));
                    break;
                default:
                    listener.reviewVideo(mVideoScreens.get(getAdapterPosition()));
            }
        }
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void save(VideoScreen videoScreen);

        void remove(VideoScreen videoScreen);

        void reviewVideo(VideoScreen videoScreen);

    }

}
