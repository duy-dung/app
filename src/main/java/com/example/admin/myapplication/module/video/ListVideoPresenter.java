package com.example.admin.myapplication.module.video;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.admin.myapplication.model.VideoScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListVideoPresenter implements ListVideoContract.Presenter {
    private ListVideoContract.View view;

    public ListVideoPresenter(ListVideoContract.View view) {
        this.view = view;
    }

    @Override
    public List<VideoScreen> getListfromLocal(Context context) {
        List<VideoScreen> mList = new ArrayList<>();
        File directory = Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS+"/video");
        directory.mkdirs();
        File[] files = directory.listFiles();

        if (files!=null&&files.length > 0) {
            for (int i = 0; i < files.length; i++)
                try {
                    if (files[i].getName().endsWith(".mp4")) {
                        VideoScreen videoScreen = new VideoScreen();
                        Log.d("Files", "FileName:" + files[i].getName());
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
                        retriever.setDataSource(context, Uri.fromFile(files[i]));
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInMillisec = Long.parseLong(time);
//            retriever.release();
                        videoScreen.setTitle(files[i].getName());
                        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMillisec),
                                TimeUnit.MILLISECONDS.toMinutes(timeInMillisec) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillisec)),
                                TimeUnit.MILLISECONDS.toSeconds(timeInMillisec) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec)));

                        videoScreen.setTime(hms);
                        videoScreen.setPathFile(files[i].getPath());
                        mList.add(videoScreen);
                        Log.d("Files", "getListfromLocal: " + hms);
                    }

                } catch (Exception e) {

                }
        }

        return mList;
    }

    @Override
    public void backToBackground(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }
}
