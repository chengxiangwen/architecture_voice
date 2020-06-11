package com.josecheng.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
*带状态的MediaPlayer
*/
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    public enum Status{
        IDEL,INITIALIZED,STARTED,PAUSED,STOPPED,COMPLETED;
    }

    private OnCompletionListener onCompletionListener;

    private Status mStatus;
    public CustomMediaPlayer(){
        super();
        mStatus = Status.IDEL;
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        mStatus = Status.IDEL;
    }

    @Override public void setDataSource(String path)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        super.setDataSource(path);
        mStatus = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mp);
        }
    }

    public Status getStatus(){
        return mStatus;
    }

    public boolean isCompleted(){
        return mStatus == Status.COMPLETED;
    }

    public void setOnCompletionListener(OnCompletionListener listener){
        this.onCompletionListener = listener;
    }
}
