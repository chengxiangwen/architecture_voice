package com.josecheng.lib_audio.mediaplayer.core;

import com.josecheng.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.josecheng.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.josecheng.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.josecheng.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.josecheng.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

/**
 * 控制播放逻辑
 */
public class AudioController {
    /**
     * 播放方式
     */
    public enum PlayMode{
        /**
         * 循环播放
         */
        LOOP,
        /**
         * 随机播放
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    public static AudioController getInstance(){
        return AudioController.SingletonHolder.instance;
    }

    private static class SingletonHolder {
       private static AudioController instance = new AudioController();
    }
    private AudioPlayer mAudioPlayer;
    private ArrayList<AudioBean> mQueue = new ArrayList<>();//歌曲列表;
    private int mQueueIndex = 0;
    private PlayMode mPlayMode = PlayMode.LOOP;//播放模式;
    private AudioController(){
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();//核心播放器
    }

    /**
     * 获取播放列表
     * @return
     */
    public ArrayList<AudioBean> getQueue(){
        return mQueue == null ? new ArrayList<AudioBean>(): mQueue;
    }

    /**
     * 设置播放队列
     * @param queue
     */
    public void setQueue(ArrayList<AudioBean> queue){
        this.setQueue(queue,0);
    }

    /**
     * 设置播放队列
     * @param queue
     * @param queueIndex
     */
    public void setQueue(ArrayList<AudioBean> queue,int queueIndex){
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }
    /**
     * 队列头添加播放哥曲
     */
    public void addAudio(AudioBean bean) {
        this.addAudio(0, bean);
    }

    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没添加过此id的歌曲，添加且直播番放
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean currentBean = getNowPlaying();
            if (!currentBean.id.equals(bean.id)) {
                //添加过且不是当前播放，播，否则什么也不干
                setPlayIndex(query);
            }
        }
    }

    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, bean);
    }

    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }

    /**
     * 获取播放模式
     * @return
     */
    public PlayMode getPlayMode(){
        return mPlayMode;
    }

    /**
     * 设置播放模式
     * @param playMode
     */
    public void setPlayMode(PlayMode playMode){
        mPlayMode = playMode;
    }

    /**
     * 获取播放索引
     * @return
     */
    public int getQueueIndex(){
        return mQueueIndex;
    }

    /**
     * 设置播放索引
     * @param index
     */
    public void setPlayIndex(int index) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueueIndex = index;
        play();
    }

    private AudioBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
    }

    /**
     * 对外提供的获取当前歌曲信息
     */
    public AudioBean getNowPlaying() {
        return getPlaying(mQueueIndex);
    }

    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
        }
        return null;
    }

    private AudioBean getPreviosPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + mQueue.size() - 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
        }
        return null;
    }


    /**
     * 对外提供Play方法
     */
    public void play() {
        AudioBean audioBean = getPlaying(mQueueIndex);
        mAudioPlayer.load(audioBean);
    }


    public void pause(){
        mAudioPlayer.pause();
    }

    /**
     * 切换播放/暂停
     */
    public void playOrPause(){
        if (isStartState()){
            pause();
        }else if (isPauseState()){
            resume();
        }
    }

    public void resume(){
        mAudioPlayer.resume();
    }

    public void release(){
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 播放下一首歌曲
     */
    public void next(){
        AudioBean audioBean = getNextPlaying();
        mAudioPlayer.load(audioBean);
    }

    /**
     * 播放前一首歌曲
     */
    public void previous(){
        AudioBean audioBean = getPreviosPlaying();
        mAudioPlayer.load(audioBean);
    }

    /**
     * 添加/移除到收藏
     */
    public void changeFavourite() {
        if (null != GreenDaoHelper.selectFavourite(getNowPlaying())) {
            //已收藏，移除
            GreenDaoHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        } else {
            //未收藏，添加收藏
            GreenDaoHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }

    /**
     * 是否是播放状态
     * @return
     */
    public boolean isStartState(){
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 是否是暂停状态
     * @return
     */
    public boolean isPauseState(){
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    /**
     * 获取播放状态
     * @return
     */
    public CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    //插放完毕事件处理
    @Subscribe(threadMode = ThreadMode.MAIN) public void onAudioCompleteEvent(
            AudioCompleteEvent event) {
        next();
    }

    //播放出错事件处理
    @Subscribe(threadMode = ThreadMode.MAIN) public void onAudioErrorEvent(AudioErrorEvent event) {
        next();
    }
}
