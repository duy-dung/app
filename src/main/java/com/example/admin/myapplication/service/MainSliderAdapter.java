package com.example.admin.myapplication.service;

import com.example.admin.myapplication.R;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.ss_1);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.ss_2);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.ss_3);
                break;
        }
    }

}