package com.josecheng.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.josecheng.lib_audio.app.AudioHelper;
import com.josecheng.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioStartEvent;
import com.josecheng.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * 1.播放各种音频
 * 2.对外发送各种类型的事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,AudioFocusManager.AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0X01;
    private static final int TIME_INVAL = 100;

    //真正负责音频的播放
    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    //音频焦点监听器
    private AudioFocusManager mAudioFocusManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_MSG:
                    break;
            }
        }
    };

    public AudioPlayer(){
        init();
    }

    //初始化
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        //初始化wifiLock
        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL,TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(),this);
    }

    /**
     * 对外提供的加载音频的方法
     * @param audioBean
     */
    public void load(AudioBean audioBean){
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //发送加载音频事件，UI类型处理事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 对内提供播放方法
     */
    private void start(){
        if(!mAudioFocusManager.requestAudioFocus()){
            Log.d(TAG,"请求焦点失败");
        }
        mMediaPlayer.start();
        //启动wifi锁
        mWifiLock.acquire();
        //更新进度
        mHandler.sendEmptyMessage(TIME_MSG);
        //发送start事件，UI类型处理事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    /**
     * 对外提供pause方法
     */
    public void pause(){
        if(getStatus() == CustomMediaPlayer.Status.STARTED){
            mMediaPlayer.pause();
            if (mWifiLock.isHeld()){
                mWifiLock.release();
            }
            if (mAudioFocusManager != null){
                mAudioFocusManager.abandonAudioFocus();
            }
            //停止发送进度消息
            //mHandler.removeCallbacksAndMessages(null);
            //发送暂停事件,UI类型事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 对外提供resume方法
     */
    public void resume(){
        if (getStatus() == CustomMediaPlayer.Status.PAUSED){
            start();
        }
    }

    public void release(){
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        // 取消音频焦点
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        // 关闭wifi锁
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mWifiLock = null;
        mAudioFocusManager = null;
        mHandler.removeCallbacksAndMessages(null);
        //发送销毁播放器事件,清除通知等
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    //获取播放器状态
    public CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getStatus();
        } else {
            return CustomMediaPlayer.Status.STOPPED;
        }
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //发送播放完成事件,逻辑类型事件
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //发送当次播放实败事件,逻辑类型事件
        EventBus.getDefault().post(new AudioErrorEvent());
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void audioFocusGrant() {

    }

    @Override
    public void audioFocusLoss() {

    }

    @Override
    public void audioFocusLossTransient() {

    }

    @Override
    public void audioFocusLossDuck() {

    }
}
