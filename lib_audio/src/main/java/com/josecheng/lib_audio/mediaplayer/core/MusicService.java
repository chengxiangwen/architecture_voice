package com.josecheng.lib_audio.mediaplayer.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.josecheng.lib_audio.app.AudioHelper;
import com.josecheng.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioStartEvent;
import com.josecheng.lib_audio.mediaplayer.model.AudioBean;
import com.josecheng.lib_audio.mediaplayer.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.josecheng.lib_audio.mediaplayer.view.NotificationHelper.NOTIFICATION_ID;

/**
 * 音乐后台服务，更新notification状态
 */
public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {

    //常量
    private static String DATA_AUDIOS = "AUDIOS";
    private static String ACTION_START = "ACTION_START";
    //data
    private ArrayList<AudioBean> mAudioBeans;
    private NotificationReceiver mReceiver;

    /**
     * 外部直接service方法
     */
    public static void startMusicService(ArrayList<AudioBean> audioBeans) {
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        intent.setAction(ACTION_START);
        //还需要传list数据进来
        intent.putExtra(DATA_AUDIOS, audioBeans);
        AudioHelper.getContext().startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioBeans = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
        if (ACTION_START.equals(intent.getAction())){
            playMusic();
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playMusic() {
        AudioController.getInstance().setQueue(mAudioBeans);
        AudioController.getInstance().play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }

    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNotificationInit() {
        //service与Notification绑定
        startForeground(NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notifacation为load状态
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新notifacation为start状态
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新notifacation为load状态
        NotificationHelper.getInstance().showPauseStatus();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //更新notifacation为load状态
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    }


    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String extra = intent.getStringExtra(EXTRA);
            switch (extra) {
                case EXTRA_PLAY:
                    //处理播放暂停事件,可以封到AudioController中
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous(); //不管当前状态，直接播放
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
                    AudioController.getInstance().changeFavourite();
                    break;
            }
        }
    }
}
