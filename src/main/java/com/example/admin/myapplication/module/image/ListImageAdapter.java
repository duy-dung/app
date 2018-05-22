package com.example.admin.myapplication.module.image;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.admin.myapplication.R;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ListVideoHolder> {
    private Context mContext;
    private List<String> mList;
    private Display display;

    public ListImageAdapter(Context mContext, List<String> mList, Display display) {
        this.mContext = mContext;
        this.mList = mList;
        this.display = display;
    }

    public void registerListerner(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }


    @NonNull
    @Override
    public ListVideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_image, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(display.getWidth()/2,display.getHeight()/2));
        ListVideoHolder viewHolder = new ListVideoHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListVideoHolder holder, int position) {
        Log.d("zzzzzz", "onBindViewHolder: "+mList.get(position));
        File imgFile = new  File(mList.get(position));
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
            holder.img.setImageBitmap(myBitmap);

        }
    }


    @Override
    public int getItemCount() {
//        return mVideoScreens.size();
        return mList.size();
    }

    public class ListVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_list)
        ImageView img;


        public ListVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            img.setOnClickListener(this);
            img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, img);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_option);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_share:
                                    listener.share(getAdapterPosition());
                                    return true;
                                case R.id.menu_xoa:
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

                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return true;
                }
            });


        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_list) {
                listener.reviewImage(getAdapterPosition());
            }
        }




    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
//        void onClickMenu(Menu menu);
        void reviewImage(int i);
        void remove(int i);
        void share(int i);
    }

}
