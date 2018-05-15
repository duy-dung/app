package com.example.admin.myapplication;

import java.util.List;

public class ListContract {
    public interface View extends BaseView<Presenter> {

    }

    public interface Presenter  {
        void getListfromLocal(List<VideoScreen> videoScreens);
    }

}
