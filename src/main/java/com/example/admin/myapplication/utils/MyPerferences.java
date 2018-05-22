package com.example.admin.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.admin.myapplication.model.SettingParam;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MyPerferences {
    private SharedPreferences mSharedPreferences;
    private Gson gson;
    private final String KEY_SETTINGS = "key setting";
    private final String KEY_LANGUAGE = "key language";
    private final String KEY_CHANGE_LANGUAGE = "key change language";


    public MyPerferences(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(Constrant.KEY_SHARE, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveSetting(SettingParam param) {
        mSharedPreferences.edit().putString(KEY_SETTINGS,gson.toJson(param)).commit();
    }

    public SettingParam getSetting(){
        Type type = new TypeToken<SettingParam>() {
        }.getType();
        String jsonValue = mSharedPreferences.getString(KEY_SETTINGS, null);
        return gson.fromJson(jsonValue, type)!=null? (SettingParam) gson.fromJson(jsonValue, type) :new SettingParam(0,0,0,0,0);
    }
    public void setFrist(boolean param) {
        mSharedPreferences.edit().putBoolean(Constrant.KEY_FRIST_OPEN,param).commit();
    }
    public boolean isFrist(){
        return mSharedPreferences.getBoolean(Constrant.KEY_FRIST_OPEN,true);
    }

    public void setLanguage(String param) {
        mSharedPreferences.edit().putString(KEY_LANGUAGE,param).commit();
    }
    public String getLanguage(){
        return mSharedPreferences.getString(KEY_LANGUAGE,Constrant.LANGUAGE_VI);
    }

    public void setChangeLanguage(boolean param) {
        mSharedPreferences.edit().putBoolean(KEY_CHANGE_LANGUAGE,param).commit();
    }
    public boolean isChangeLanguage(){
        return mSharedPreferences.getBoolean(KEY_CHANGE_LANGUAGE,false);
    }

}
