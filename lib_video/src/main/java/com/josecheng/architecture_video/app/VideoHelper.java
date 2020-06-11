package com.josecheng.architecture_video.app;

import android.content.Context;

public final class VideoHelper {
    //SDK全局Context
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        //初始化SDK的时候，初始化Realm数据库
    }

    public static Context getContext() {
        return mContext;
    }
}
