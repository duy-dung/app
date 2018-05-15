package com.example.admin.myapplication.service;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.example.admin.myapplication.MainActivity;

import com.example.admin.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FloatingViewService extends Service  {
    //https://stackoverflow.com/questions/15049041/background-video-recording-in-android-4-0
    private static final String BASE = "com.example.admin.myapplication.service.FloatingViewService.";
    private VirtualDisplay mVirtualDisplay;
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";


    private WindowManager mWindowManager;
    private FloatingActionButton btn0,btn1,btn2,btn3;
    private View mFloatingView;
    private Animation fabOpen,fabClose,rotateForward,rotateBackward;
    private boolean isOpen = true;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;

    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private NotificationManager notifManager = null;
    final int NOTIFY_ID = 1002;
    Intent intent;
    Intent intent12;
    PendingIntent pendingIntent;
    private MediaProjectionManager getmProjectionManager;


    private WindowManager windowManager;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        mProjectionManager =  (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //Inflate the floating view layout we created
//        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        setTheme(R.style.AppTheme);
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.type =  Build.VERSION.SDK_INT < Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_PHONE :
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format =    PixelFormat.TRANSLUCENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;




        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        params.x =width;
        params.y = height/4;
        mFloatingView.bringToFront();

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
                stopSelf();
                //close the service and remove view from the view hierarchy
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            stopScreenSharing();
//                stopSelf();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mWindowManager.removeView(mFloatingView);
//                initRecorder();
                startRecord();
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
                        params.flags = params.flags&~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecord() {
        initNotification();
        initRecorder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intent12 = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    private void initNotification() {

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.


        NotificationCompat.Builder builder;


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
                    .setOngoing(true)
                    .setTicker("zzzzcccccc");
//                    .setContent(bigView)
//                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
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
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setTicker("zzzzzzccccccc")
//                    .setContent(bigView)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_DEFAULT);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        Notification notification = new Notification(icon, tickerText, when);
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notifManager.notify(NOTIFY_ID, notification);
//        new Runnable() {
//            @Override
//            public void run() {
//
//                long t =0;
//                do {
//                    try {
//                        Thread.sleep(1000);
//                        updateNoti(t);
//                        t +=1000;
//                        Log.d(TAG, "run: 111111111");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }while (true);
//            }
//        }.run();
    }
    private void updateNoti(long sec) {
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String timeStamp =String.valueOf(new SimpleDateFormat("hh:mm:ss").format(sec));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(timeStamp);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notifManager.createNotificationChannel(mChannel);
        }else {
            NotificationCompat.Builder builder;
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentTitle(timeStamp+"")                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(getApplication().getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
//                    .setContent(bigView)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_DEFAULT);
            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID,notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initRecorder() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setAudioEncodingBitRate(7* 1000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video.mp4");
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = window.getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
//            final int resultCode = intent12.getIntExtra(EXTRA_RESULT_CODE, 0);
//            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, intent12);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int mDensityDpi = metrics.densityDpi;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopScreenSharing() {
        mVirtualDisplay.release();
        try{
            mMediaRecorder.stop();
        }catch(RuntimeException stopException){
            //handle cleanup here
        }

//        destroyMediaProjection();
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


}


