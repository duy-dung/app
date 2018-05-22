package com.example.admin.myapplication.module.video;

import android.content.Context;

import com.example.admin.myapplication.utils.BaseView;
import com.example.admin.myapplication.model.VideoScreen;

import java.util.List;

public class ListVideoContract {
    public interface View extends BaseView<Presenter> {

    }

    public interface Presenter  {
        List<VideoScreen> getListfromLocal(Context context);
        void backToBackground(Context context);
    }

}
