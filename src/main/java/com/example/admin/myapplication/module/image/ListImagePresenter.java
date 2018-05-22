package com.example.admin.myapplication.module.image;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.admin.myapplication.model.VideoScreen;
import com.example.admin.myapplication.module.video.ListVideoContract;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListImagePresenter implements ListImageContract.Presenter {
    private ListImageContract.View view;

    public ListImagePresenter(ListImageContract.View view) {
        this.view = view;
    }

    @Override
    public List<String> getListfromLocal(Context context) {
        List<String> mList = new ArrayList<>();
//        File directory = Environment
//                .getExternalStoragePublicDirectory(Environment
//                        .DIRECTORY_DOWNLOADS+"/Screen");
//        File[] files = directory.listFiles();
//        for (int i = 0; i < files.length; i++)
//            try {
//                String s = files[i].getPath();
//                Log.d("zzzz", "getListfromLocal: "+s);
//                mList.add(s);
//
//            } catch (Exception e) {
//
//
//            }
//        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();


        try {
                    File directory = Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_DOWNLOADS+"/Screen");
            File[] files1 = directory.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files1) {
              if (file.getName().endsWith(".png")) {
                   mList.add(file.getPath());
                }
            }
        } catch (Exception e) {
            return null;
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
