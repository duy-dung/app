package com.example.admin.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReciver  extends BroadcastReceiver{

    private ListActivity musicPlayerActivity;

    public MyReciver(ListActivity activity) {
        musicPlayerActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        musicPlayerActivity.onReceiveChanged();
    }
}
