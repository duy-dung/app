package com.example.admin.myapplication.module.video;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.VideoScreen;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.ListVideoHolder> {
    private Context mContext;
    private List<VideoScreen> mVideoScreens;

    public ListVideoAdapter(Context mContext, List<VideoScreen> mVideoScreens) {
        this.mContext = mContext;
        this.mVideoScreens = mVideoScreens;
    }

    public void registerListerner(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
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
        VideoScreen videoScreen = mVideoScreens.get(position);
        if (videoScreen != null) {
            if ((position + 1) % 2 != 0) {
                holder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.xam));
            } else {
                holder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.wi));
            }
            holder.tvTitle.setText(videoScreen.getTitle().substring(11));
            holder.tvTime.setText(videoScreen.getTime());
            holder.tvDate.setText(videoScreen.getTitle().substring(0,10));
            Glide.with(mContext).load(videoScreen.getPathFile()).into(holder.simpleDraweeView);
        }
    }


    @Override
    public int getItemCount() {
//        return mVideoScreens.size();
        return mVideoScreens.size();
    }

    public class ListVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.simpleDraweeView)
        ImageView simpleDraweeView;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.textView)
        TextView tvTime;
        @BindView(R.id.im_remove)
        ImageView imgRemove;
        @BindView(R.id.ctl_item)
        ConstraintLayout constraintLayout;
        @BindView(R.id.img_share)
        ImageView imgShare;
        @BindView(R.id.tv_time)
        TextView tvDate;


        public ListVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgRemove.setOnClickListener(this);
            simpleDraweeView.setOnClickListener(this);
            constraintLayout.setOnClickListener(this);
            imgShare.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_remove:
                    new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.xacnhan)
                            .setMessage(R.string.mess_xacnhan)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.remove(getAdapterPosition());
                                }

                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                    break;
                case R.id.ctl_item:
                    listener.reviewVideo(mVideoScreens.get(getAdapterPosition()));
                    break;
                case R.id.simpleDraweeView:
                    listener.reviewVideo(mVideoScreens.get(getAdapterPosition()));
                    break;
                case R.id.img_share:
                   listener.share(getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {

        void share(int i);

        void remove(int i);

        void reviewVideo(VideoScreen videoScreen);

    }

}
