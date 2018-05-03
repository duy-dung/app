package com.example.admin.myapplication.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.R;


public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private FloatingActionButton btn0,btn1,btn2,btn3;
    private View mFloatingView;
    private Animation fabOpen,fabClose,rotateForward,rotateBackward;
    private boolean isOpen = true;
    private final IMyAidlInterface.Stub iMyAidlInterfacel =new IMyAidlInterface.Stub() {
        @Override
        public void startRecord() throws RemoteException {
            Toast.makeText(FloatingViewService.this, "Start", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void stopRecord() throws RemoteException {
            Toast.makeText(FloatingViewService.this, "Stop", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void pauseRecord() throws RemoteException {
            Toast.makeText(FloatingViewService.this, "Pause", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        setTheme(R.style.AppTheme);
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY :
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        params.x =width;
        params.y = height/4;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        btn0=mFloatingView.findViewById(R.id.btn_0);
        btn1=mFloatingView.findViewById(R.id.btn_1);
        btn2=mFloatingView.findViewById(R.id.btn_2);
        btn3=mFloatingView.findViewById(R.id.btn_3);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_close);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    btn0.startAnimation(rotateForward);
                    btn1.startAnimation(fabClose);
                    btn2.startAnimation(fabClose);
                    btn3.startAnimation(fabClose);
                    btn1.setClickable(false);
                    btn2.setClickable(false);
                    btn3.setClickable(false);
                }else {
                    btn0.startAnimation(rotateBackward);
                    btn1.startAnimation(fabOpen);
                    btn2.startAnimation(fabOpen);
                    btn3.startAnimation(fabOpen);
                    btn1.setClickable(true);
                    btn2.setClickable(true);
                    btn3.setClickable(true);
                }
                isOpen =!isOpen;
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                RemoteViews bigView = new RemoteViews(getApplicationContext().getPackageName(),
                        R.layout.remote_views);
                initPendingIntent(bigView);
                

                final int NOTIFY_ID = 1002;

                // There are hardcoding only for show it's just strings
                String name = "my_package_channel";
                String id = "my_package_channel_1"; // The user-visible name of the channel.
                String description = "my_package_first_channel"; // The user-visible description of the channel.

                Intent intent;
                PendingIntent pendingIntent;
                NotificationCompat.Builder builder;
                NotificationManager notifManager = null;

                if (notifManager == null) {
                    notifManager =
                            (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = notifManager.getNotificationChannel(id);
                    if (mChannel == null) {
                        mChannel = new NotificationChannel(id, name, importance);
                        mChannel.setDescription(description);
                        mChannel.enableVibration(true);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        notifManager.createNotificationChannel(mChannel);
                    }
                    builder = new NotificationCompat.Builder(getApplicationContext(), id);

                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    builder.setContentTitle("zzzz")  // required
                            .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                            .setContentText(getApplication().getString(R.string.app_name))  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setTicker("zzzzcccccc")
                            .setContent(bigView)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                } else {

                    builder = new NotificationCompat.Builder(getApplicationContext());

                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    builder.setContentTitle("zzz23")                           // required
                            .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                            .setContentText(getApplication().getString(R.string.app_name))  // required
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setTicker("zzzzzzccccccc")
                            .setContent(bigView)
                            .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                            .setPriority(Notification.PRIORITY_LOW);
                } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                Notification notification = builder.build();
                notifManager.notify(NOTIFY_ID, notification);
            }
        });

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.d("zzzzzzzzzzzz", "DOWN"  +event.getX()+"---------"+event.getY());

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
//                        Log.d("zzzzzzzzzzzz", "UP"  +event.getRawX()+"---------"+event.getRawY());
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
//                        if (Xdiff < 10 && Ydiff < 10) {
//                            if (isViewCollapsed()) {
//                                //When user clicks on the image view of the collapsed layout,
//                                //visibility of the collapsed layout will be changed to "View.GONE"
//                                //and expanded view will become visible.
//                                collapsedView.setVisibility(View.GONE);
////                                expandedView.setVisibility(View.VISIBLE);
//                            }
//                        }
//                        return true;
                    case MotionEvent.ACTION_MOVE:
//                        Log.d("zzzzzzzzzzzz", "MOVE"  +(initialX + (int) (event.getRawX() - initialTouchX))+"---------"+(initialY + (int) (event.getRawY() - initialTouchY)));
                        Log.d("zzzzzzzzzzzz", "MOVE"  +(event.getRawX() - initialTouchX)+"____"+((event.getRawY() - initialTouchY)));
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    private void initPendingIntent(RemoteViews remoteViews) {

        Intent openAppIntent = new Intent(this, MainActivity.class);

        // call broadcast when any control of notification is clicked.
        Intent closeNotification = new Intent("close_notification");
        Intent playPauseIntent = new Intent("play_pause");
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(this, 0, closeNotification, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(this, 1, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Using RemoteViews to bind custom layouts into Notification
        remoteViews.setOnClickPendingIntent(R.id.btn_play, pendingCloseIntent);
        remoteViews.setOnClickPendingIntent(R.id.btn_stop, pendingPlayPauseIntent);
    }


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
    private BroadcastReceiver mPreviousReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


        }
    };

}
