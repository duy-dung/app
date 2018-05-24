package com.example.admin.myapplication.service;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.IMyAidlRecoder;
import com.example.admin.myapplication.ListActivity;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.SettingParam;
import com.example.admin.myapplication.utils.MyPerferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class FloatingViewService extends Service {
    //https://stackoverflow.com/questions/15049041/background-video-recording-in-android-4-0
    private static final String BASE = "com.example.admin.myapplication.service.FloatingViewService.";
    public static final String EXTRA_RESULT_INTENT = "ưerewras23";
    public static final String ACTION_STOP = "action.mp3.pause";
    public static final String ACTION_RESTART = "action.mp3.pause";
    public static final String ACTION_OPEN = "action.mp3.next";
    private static final int ID_NOTI = 1323423;

    private VirtualDisplay mVirtualDisplay;
    public static final String EXTRA_RESULT_CODE = BASE + "EXTRA_RESULT_CODE";


    private WindowManager mWindowManager;
    private FloatingActionButton btnAction, btnOpen, btnShot, btnRecoder, btnExit;
    private View mFloatingView;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private boolean isOpen = true;


    private MediaProjectionManager mProjectionManager;

    private MediaProjection mMediaProjection;

    private MediaRecorder mMediaRecorder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Intent intent;
    private ImageReader mImageReader;
    private Handler handler;
    private boolean check = false;
    private MyPerferences myPerferences;


    WindowManager window;
    Display display;

    private NotificationManager notifManager = null;
    final int NOTIFY_ID = 1002;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    private String id = "zzz";
    private CharSequence name = "z";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        myPerferences = new MyPerferences(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OPEN);
        filter.addAction(ACTION_STOP);
        registerReceiver(receiver, filter);
        window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        display = window.getDefaultDisplay();
        mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        setTheme(R.style.AppTheme);

    }

    private void showView() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Add the view to the window.


        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        params.x = width;
        params.y = height / 4;
        mFloatingView.bringToFront();

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        btnAction = mFloatingView.findViewById(R.id.btn_0);
        btnOpen = mFloatingView.findViewById(R.id.btn_1);
        btnShot = mFloatingView.findViewById(R.id.btn_2);
        btnRecoder = mFloatingView.findViewById(R.id.btn_3);
        btnExit = mFloatingView.findViewById(R.id.btn_exit);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_close);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    btnAction.startAnimation(rotateForward);
                    btnOpen.startAnimation(fabClose);
                    btnShot.startAnimation(fabClose);
                    btnRecoder.startAnimation(fabClose);
                    btnExit.setAnimation(fabClose);
                    btnOpen.setClickable(false);
                    btnShot.setClickable(false);
                    btnRecoder.setClickable(false);
                    btnExit.setClickable(false);
                } else {
                    btnAction.startAnimation(rotateBackward);
                    btnOpen.startAnimation(fabOpen);
                    btnShot.startAnimation(fabOpen);
                    btnRecoder.startAnimation(fabOpen);
                    btnExit.setAnimation(fabOpen);
                    btnOpen.setClickable(true);
                    btnShot.setClickable(true);
                    btnRecoder.setClickable(true);
                    btnExit.setClickable(true);
                }
                isOpen = !isOpen;
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeView(mFloatingView, mWindowManager);
                Intent intent = new Intent(FloatingViewService.this, ListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                stopSelf();
                //close the service and remove view from the view hierarchy

            }
        });
        btnShot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
//                stopScreenSharing();
                removeView(mFloatingView, mWindowManager);
                btnShot.setClickable(false);
                ScreenShot();
//                btnShot.setClickable(false);
            }
        });
        btnRecoder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                btnRecoder.setClickable(false);
                int time = myPerferences.getSetting().getTgc() + 3;
                final ConstraintLayout viewCountDown = (ConstraintLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_wait, null);
                final TextView tvTime = viewCountDown.findViewById(R.id.tv_time);
                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
                }
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSPARENT);
                mWindowManager.addView(viewCountDown, params);
                new CountDownTimer(time * 1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d("z", "onTick: " + millisUntilFinished);
                        tvTime.setText((millisUntilFinished / 1000) + "");
                    }

                    @Override
                    public void onFinish() {
                        btnRecoder.setClickable(true);
                        mWindowManager.removeView(viewCountDown);
                        startRecord();
                    }
                }.start();

            }

        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(mFloatingView, mWindowManager);
                stopSelf();
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
                        Log.d("zzzzzzzzzzzz", "MOVE" + (event.getRawX() - initialTouchX) + "____" + ((event.getRawY() - initialTouchY)));
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    public void removeView(View v, WindowManager window) {
        window.removeView(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ScreenShot() {
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                btnShot.setClickable(true);
                showView();
            }
        }.start();
        mImageReader = ImageReader.newInstance(display.getWidth(), display.getHeight(), PixelFormat.RGBA_8888, 2);
        mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mProjectionManager.getMediaProjection(intent.getIntExtra(EXTRA_RESULT_CODE, -1), (Intent) intent.getParcelableExtra(EXTRA_RESULT_INTENT));
        mMediaProjection.registerCallback(new MediaProjectionStopCallback(), handler);
        mVirtualDisplay = createVirtualDisplayS();


        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecord() {
        check = true;
        removeView(mFloatingView, mWindowManager);
        mProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mMediaProjection = mProjectionManager.getMediaProjection(intent.getIntExtra(EXTRA_RESULT_CODE, -1),
                (Intent) intent.getParcelableExtra(EXTRA_RESULT_INTENT));

        initNotification();

        initRecorder();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;
        return mMediaProjection.createVirtualDisplay("FloatingViewService",
                display.getWidth(), display.getHeight(), screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplayS() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;
        return mMediaProjection.createVirtualDisplay("FloatingViewService",
                display.getWidth(), display.getHeight(), screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        showView();
        return START_NOT_STICKY;
    }


    private void initNotification() {
        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notif_layout);

        Intent intentStop = new Intent(ACTION_STOP);
        Intent intentOpen = new Intent(ACTION_OPEN);
        Intent intentRestart = new Intent(ACTION_RESTART);
        PendingIntent stop = PendingIntent.getBroadcast(this, 0, intentStop, 0);
        PendingIntent open = PendingIntent.getBroadcast(this, 0, intentOpen, 0);
        PendingIntent restart = PendingIntent.getBroadcast(this, 0, intentRestart, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_stop, stop);
        remoteViews.setOnClickPendingIntent(R.id.img_Open, open);
        remoteViews.setOnClickPendingIntent(R.id.img_Restart, restart);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        Intent notificationIntent = new Intent(getApplicationContext(), ListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{300, 400, 500});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), id);


            builder.setContentTitle("zzzz")  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(getApplication().getString(R.string.app_name))  // required

                    .setAutoCancel(false)
                    .setCustomContentView(remoteViews)
                    .setOngoing(true)
                    .setTicker("zzzzcccccc");
//                    .setContent(bigView)
//                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder.setContentTitle(getResources().getString(R.string.dangquay))                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(getApplication().getString(R.string.app_name))  // required
                    .setCustomBigContentView(remoteViews)
                    .setCustomContentView(remoteViews)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setTicker("zzzzzzccccccc")
//                    .setContent(bigView)
                    .setVibrate(new long[]{300, 400, 500})
                    .setPriority(Notification.PRIORITY_DEFAULT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notifManager.notify(ID_NOTI, builder.build());
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initRecorder() {


        SettingParam param = myPerferences.getSetting();
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        Date date = new Date();
        String title = dateFormat.format(date);
        mMediaRecorder = new MediaRecorder();
        Point size = new Point();
        display.getSize(size);

        if (param.getNat() == 0) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } else {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        }

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        if (Build.VERSION.SDK_INT <= 23) {
//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setAudioEncodingBitRate(8000);
            mMediaRecorder.setAudioSamplingRate(8000);

            mMediaRecorder.setVideoSize(size.x, size.y);
//            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//
//            profile.videoFrameWidth = size.x;
//            profile.videoFrameHeight = size.y;
//            mMediaRecorder.setProfile(profile)

        } else {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setVideoSize(size.x, size.y);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoSize(size.x, size.y);
        }
        if (Build.VERSION.SDK_INT <= 23) {
            File path = new File(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video/");
            path.mkdirs();
            try {
                File video = File.createTempFile(title, ".3gp", path);
                mMediaRecorder.setOutputFile(video.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            File path = new File(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/video/");
            path.mkdirs();
            try {
                File video = File.createTempFile(title, ".mp4", path);
                mMediaRecorder.setOutputFile(video.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }catch (IllegalStateException e){
                Log.e("zzz", "initRecorder: "+e.toString());
            }

        }
        int fps;
        if (param.getTdq() == 0) {
            fps = 30;
        } else if (param.getTdq() == 1) {
            fps = 30;
        } else fps = 30;
        mMediaRecorder.setVideoFrameRate(fps);
        int rotation = window.getDefaultDisplay().getRotation();
        int orientation = ORIENTATIONS.get(rotation + 90);
        mMediaRecorder.setOrientationHint(orientation);

        int cl = param.getCl();
        if (cl == 0) {
            mMediaRecorder.setVideoEncodingBitRate(3000000);
        } else if (cl == 1) {
            mMediaRecorder.setVideoEncodingBitRate(3000000);
        } else {
            mMediaRecorder.setVideoEncodingBitRate(3000000);
        }



//

        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                System.out.println("wwwwwwww" + what + "zzzzzzzzzzzzzzzzzzzzzz" + extra);
            }
        });

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            System.out.println(e.getCause());
//            releaseMediaRecorder();
        }catch ( IOException e){;
            System.out.println("zzzzzzz"+e.getMessage());
//            releaseMediaRecorder();
        }catch (Exception r){

        }

        mVirtualDisplay = createVirtualDisplay();
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopScreenSharing() {

        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mProjectionManager = null;
        mMediaProjection.stop();
        mVirtualDisplay.release();

    }


    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onImageAvailable(ImageReader reader) {
            java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss" +
                    "");
            Date date = new Date();
            String title = dateFormat.format(date);
            File theDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS) + "/Screen");

// if the directory does not exist, create it
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;

                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    System.out.println("DIR created");
                }
            }
            mMediaProjection.stop();
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * display.getWidth();

                    // create bitmap
                    bitmap = Bitmap.createBitmap(display.getWidth() + rowPadding / pixelStride, display.getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    // write bitmap to a file
                    fos = new FileOutputStream(Environment
                            .getExternalStoragePublicDirectory(Environment
                                    .DIRECTORY_DOWNLOADS) + "/Screen/myscreen_" + title + ".png");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.dachup), Toast.LENGTH_SHORT).show();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {

            if (mVirtualDisplay != null) mVirtualDisplay.release();
            if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
//                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
            mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);


        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            notifManager.cancel(ID_NOTI);
            if (action.equals(ACTION_OPEN)) {
                Intent intent1 = new Intent(FloatingViewService.this, ListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                stopSelf();
            } else if (action.equals(ACTION_STOP)) {
                stopScreenSharing();
                showView();
            } else if (action.equals(ACTION_RESTART)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopScreenSharing();
                showView();

            }
        }
    };

}



