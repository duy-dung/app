package com.example.admin.myapplication;

import java.util.List;

public class ListPresenter implements ListContract.Presenter {
    private ListContract.View view;

    public ListPresenter(ListContract.View view) {
        this.view = view;
    }

    @Override
    public void getListfromLocal(List<VideoScreen> videoScreens) {

    }
}
