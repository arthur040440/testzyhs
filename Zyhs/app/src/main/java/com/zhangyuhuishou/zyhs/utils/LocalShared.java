package com.zhangyuhuishou.zyhs.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalShared {


    private static final String sharedName = "LocalShared";
    private SharedPreferences mShared;
    private static LocalShared mInstance;


    private LocalShared(Context context) {
        mShared = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
    }


    public static LocalShared getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LocalShared.class) {
                if (mInstance == null) {
                    mInstance = new LocalShared(context);
                }
            }
        }
        return mInstance;
    }


    public void setFlag(boolean flag) {

        SharedPreferences.Editor editor = mShared.edit();
        editor.putBoolean("flag", flag);
        editor.commit();
    }


    public boolean getFlag() {
        return mShared.getBoolean("flag", true);
    }


    public void clearLocalData() {
        
        SharedPreferences.Editor editor = mShared.edit();
        editor.clear();
        editor.commit();
    }
}