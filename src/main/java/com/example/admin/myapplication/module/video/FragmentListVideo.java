package com.example.admin.myapplication.module.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.VideoScreen;
import com.example.admin.myapplication.service.FloatingViewService;

import java.io.File;
import java.util.List;

public class FragmentListVideo extends Fragment implements ListVideoContract.View {

    private ListVideoPresenter mPresenter;
    private RecyclerView mRecyclerViewl;
    private ListVideoAdapter mAdapter;
    private List<VideoScreen> mList;
    private ProgressDialog progressDialog;
    private ImageView mFloatingActionButton;



    public static FragmentListVideo newInstance() {
        FragmentListVideo fragmentListVideo = new FragmentListVideo();
        return fragmentListVideo;
    }


    private View mContentView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setIndeterminate(true);
        progressDialog.show();

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.frm_list_video, container, false);
        }
        return mContentView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFloatingActionButton = getActivity().findViewById(R.id.btn_action);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getContext(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });


        mRecyclerViewl = getActivity().findViewById(R.id.rcv_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerViewl.setLayoutManager(linearLayoutManager);
        if (mList != null && mList.size() > 0) {
            mAdapter = new ListVideoAdapter(getContext(), mList);
            mRecyclerViewl.setAdapter(mAdapter);
            mAdapter.registerListerner(new ListVideoAdapter.OnItemClickListener() {

                @Override
                public void share(int i) {
                    File file =new File(mList.get(i).getPathFile());
                    Uri uri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);

                    startActivity(Intent.createChooser(intent, "Share Video"));
//
//                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                    Uri screenshotUri = Uri.parse(mList.get(i).getPathFile());
//
//                    sharingIntent.setType("video/mp4");
//                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.chiasevoi)));

                }

                @Override
                public void remove(int i) {
                    File file = new File(mList.get(i).getPathFile());
                    if (!file.delete()) {
                        Toast.makeText(getActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                    } else {
                        mList.remove(i);
                        mAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void reviewVideo(VideoScreen videoScreen) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(videoScreen.getPathFile()), "video/*");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                }
            });

        }

        progressDialog.dismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ListVideoPresenter(this);
        mList = mPresenter.getListfromLocal(getContext());


    }

}
