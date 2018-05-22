package com.example.admin.myapplication.module.image;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.service.FloatingViewService;


import java.io.File;
import java.util.List;

public class FragmentListImage extends Fragment implements ListImageContract.View {

    private ListImagePresenter mPresenter;
    private RecyclerView mRecyclerViewl;
    private ListImageAdapter mAdapter;
    private List<String> mList;
    private ImageView mFloatingActionButton;


    public static FragmentListImage newInstance() {
        FragmentListImage fragmentListVideo = new FragmentListImage();

        return fragmentListVideo;
    }


    private View mContentView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.frm_list_image, container, false);
        }
        return mContentView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerViewl = getActivity().findViewById(R.id.rcv_list_image);
        mFloatingActionButton = getActivity().findViewById(R.id.btn_action_list);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FloatingViewService.class);
                getActivity().startService(intent);
                getActivity().finish();
            }
        });


        WindowManager window = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        mRecyclerViewl.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (mList != null && mList.size() > 0) {
            mAdapter = new ListImageAdapter(getContext(), mList, window.getDefaultDisplay());
            mRecyclerViewl.setAdapter(mAdapter);
            mAdapter.registerListerner(new ListImageAdapter.OnItemClickListener() {
                @Override
                public void reviewImage(int i) {

//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse(mList.get(i)), "image/png");
//
//                    startActivity(Intent.createChooser(intent,"zz"));
//                    File file=new File(mList.get(i));
//                    String agendaFilename = file.getAbsolutePath();
//
//                    final ContentValues values = new ContentValues(2);
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//                    values.put(MediaStore.Images.Media.DATA, agendaFilename);
//                    final Uri contentUriFile = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    final Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.setType("image/png");
//                    intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
//                    startActivity(Intent.createChooser(intent, "title"));
                }

                @Override
                public void remove(int i) {
                    File file = new File(mList.get(i));
                    if (!file.delete()) {
                        Toast.makeText(getActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                    } else {
                        mList.remove(i);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void share(int i) {
                    File file =new File(mList.get(i));
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);

                    startActivity(Intent.createChooser(intent, "Share Image"));

                }


            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ListImagePresenter(this);
        mList = mPresenter.getListfromLocal(getContext());
    }
}
