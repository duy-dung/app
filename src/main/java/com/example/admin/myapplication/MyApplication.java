package com.example.admin.myapplication;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.example.admin.myapplication.utils.Constrant;
import com.example.admin.myapplication.utils.LocaleHelper;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }
    public static void updateAppLanguage(Context ctx, String languageCode) {
        LocaleHelper.setLocale(ctx, languageCode);
        LocaleHelper.onAttach(ctx, languageCode);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, Constrant.LANGUAGE_VI));
    }
}
