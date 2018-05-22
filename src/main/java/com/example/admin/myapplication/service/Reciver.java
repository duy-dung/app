package com.example.admin.myapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class Reciver extends BroadcastReceiver {
    public static   String NOTIFICATION_ID = "notification-id";
    public  static String NOTIFICATION = "notification";
    private Temo temp;

    public Reciver(Temo temp) {
        this.temp = temp;
    }

    public Reciver() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("qqqqqqq111", "onReceive: zzzzzz");
        temp.callbackService();

    }
    interface Temo {
        void callbackService();
    }
}