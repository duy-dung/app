package com.example.admin.myapplication.module.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.admin.myapplication.R;

public class PreviewImage extends Activity {




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_preview);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Intent intent =getIntent();

        SubsamplingScaleImageView imageView =findViewById(R.id.imageView);
        imageView.setImage(ImageSource.uri( intent.getStringExtra("image")));
    }

}
