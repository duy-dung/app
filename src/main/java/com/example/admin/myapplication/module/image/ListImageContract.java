package com.example.admin.myapplication.module.image;

import android.content.Context;

import com.example.admin.myapplication.model.VideoScreen;
import com.example.admin.myapplication.utils.BaseView;

import java.util.List;

public class ListImageContract {
    public interface View extends BaseView<Presenter> {

    }

    public interface Presenter  {
        List<String> getListfromLocal(Context context);
        void backToBackground(Context context);
    }

}
