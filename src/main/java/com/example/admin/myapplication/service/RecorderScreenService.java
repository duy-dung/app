package com.example.admin.myapplication.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.RemoteViews;
import com.example.admin.myapplication.MainActivity;
import com.example.admin.myapplication.R;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class RecorderScreenService extends Service {


    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_PERMISSIONS = 10;
    private NotificationManager notifManager = null;
    final int NOTIFY_ID = 1002;

    Intent intent;
    PendingIntent pendingIntent;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private NotificationCompat.Builder builder;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

//        Intent intent =new Intent(getApplicationContext(),FloatingViewService.class);
//        stopService(intent);
//        init();

        mMediaRecorder = new MediaRecorder();

        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);


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

    private void init() {
        RemoteViews bigView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.remote_views);
        initPendingIntent(bigView);



        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.


        NotificationCompat.Builder builder =null;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription("zzz");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notifManager.createNotificationChannel(mChannel);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());

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
            Notification notification = builder.build();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
            notifManager.notify(NOTIFY_ID, notification);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        Notification notification = new Notification(icon, tickerText, when);
//        notification.flags = Notification.FLAG_ONGOING_EVENT;

        new Runnable() {
            @Override
            public void run() {
                long t =0;
                do {
                    try {
                        Thread.sleep(1000);
                        updateNoti(t);
                        t +=1000;
                        Log.d(TAG, "run: 111111111");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (true);
            }
        }.run();

    }
    private void updateNoti(long sec) {
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String timeStamp =String.valueOf(new SimpleDateFormat("hh:mm:ss").format(sec));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
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

    private void initRecorder() {
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video.mp4");
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int rotation = window.getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {

            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }
    
}
