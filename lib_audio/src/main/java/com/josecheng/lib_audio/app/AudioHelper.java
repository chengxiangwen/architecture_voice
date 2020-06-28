package com.josecheng.lib_audio.app;

import android.app.Activity;
import android.content.Context;

import com.josecheng.lib_audio.mediaplayer.core.AudioController;
import com.josecheng.lib_audio.mediaplayer.core.MusicService;
import com.josecheng.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.josecheng.lib_audio.mediaplayer.model.AudioBean;
import com.josecheng.lib_audio.mediaplayer.view.MusicPlayerActivity;
import com.josecheng.lib_audio.utils.Utils;
import com.josecheng.lib_base.service.ft_audio.model.CommonAudioBean;

import java.util.ArrayList;

/**
 * 唯一与外界通信的帮助类
 */
public  final class AudioHelper {

    private static Context mContext;

    //SDK全局Context, 供子模块用
    public static void init(Context context){
        mContext = context;
        //初始化本地数据库
        GreenDaoHelper.initDatabase();
    }

    //外部启动MusicService方法
    public static void startMusicService(ArrayList<CommonAudioBean> audios) {
        MusicService.startMusicService(Utils.convertFrom(audios));
    }
    public static void addAudio(Activity activity, CommonAudioBean bean) {
        AudioController.getInstance().addAudio(Utils.convertFrom(bean));
        MusicPlayerActivity.start(activity);
    }

    public static void pauseAudio() {
        AudioController.getInstance().pause();
    }

    public static void resumeAudio() {
        AudioController.getInstance().resume();
    }

    public static Context getContext(){
        return mContext;
    }
}
