package com.josecheng.architecture_voice.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.josecheng.architecture_video.app.VideoHelper;
import com.josecheng.lib_audio.app.AudioHelper;
import com.josecheng.lib_share.share.ShareManager;

public class ArchitectureVoiceApplication extends Application {
    private static ArchitectureVoiceApplication architectureVoiceApplication = null;
    @Override
    public void onCreate() {
        super.onCreate();
        architectureVoiceApplication = this;
        VideoHelper.init(this);
        AudioHelper.init(this);
        ShareManager.initSDK(this);
        ARouter.init(this);

    }
    private static ArchitectureVoiceApplication getInstance(){return architectureVoiceApplication;}
}
