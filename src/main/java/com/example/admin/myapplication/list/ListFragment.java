package com.example.admin.myapplication.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.myapplication.R;

public class ListFragment extends Fragment {
    private RecyclerView mRecyclerView;

    private View mContentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.activity_main, container, false);
            mRecyclerView = mContentView.findViewById(R.id.rcv_list);
        }
        return mContentView;
    }

}
