package com.example.admin.myapplication.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.admin.myapplication.R;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    Context c;
    private List<Drawable> _imagePaths;
    private LayoutInflater inflater;
    public ViewPagerAdapter (Context c, List<Drawable> imagePaths) {
        this._imagePaths = imagePaths;
        this.c = c;
    }
    @Override    public int getCount() {
        return this._imagePaths.size();
    }
    @Override    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }
    @Override    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.product_viewpager_image, container,
                false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.image);
        Glide.with(c).load(_imagePaths.get(position)).into(imgDisplay);
        (container).addView(viewLayout);
        return viewLayout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
