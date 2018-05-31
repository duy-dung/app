package com.example.admin.myapplication;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.myapplication.service.PicassoImageLoadingService;
import com.example.admin.myapplication.service.MainSliderAdapter;
import com.example.admin.myapplication.utils.MyPerferences;
import com.example.admin.myapplication.utils.ViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 10;
    private MediaProjectionManager mgr;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private int index =0;

    Button btnBQ, btnTT;
    private MediaProjectionManager mediaProjectionManager;
    private int REQUEST_SCREENSHOT = 2321;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_start);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        btnBQ = findViewById(R.id.btn_bq);
        btnTT = findViewById(R.id.btn_tt);



        final ViewPager viewPager = findViewById(R.id.vp);

        List<Drawable> imgs = new ArrayList<>();

        imgs.add(getDrawable(R.drawable.ss_1));
        imgs.add(getDrawable(R.drawable.ss_2));
        imgs.add(getDrawable(R.drawable.ss_3));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imgs);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                index =position;

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btnTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(index+1);
                if (index == 2) {
                    checkPermission();
                    if (ContextCompat.checkSelfPermission(StartActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                            .checkSelfPermission(StartActivity.this,
                                    Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {

                    } else {
                        MyPerferences myPerferences =new MyPerferences(getApplicationContext());
                        myPerferences.setFrist(false);
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        btnBQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                if (ContextCompat.checkSelfPermission(StartActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(StartActivity.this,
                                Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                } else {
                   gotoApp();
                }
            }
        });
    }
    void gotoApp(){
        MyPerferences myPerferences =new MyPerferences(getApplicationContext());
        myPerferences.setFrist(false);
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    gotoApp();
                } else {

                    Snackbar.make(findViewById(android.R.id.content), R.string.label_permissions,
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(StartActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 77);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(StartActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(StartActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (StartActivity.this, Manifest.permission.RECORD_AUDIO)) {

                Snackbar.make(findViewById(android.R.id.content), R.string.label_permissions,
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(StartActivity.this,
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.RECORD_AUDIO},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(StartActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSIONS);
            }
        }
    }

}
