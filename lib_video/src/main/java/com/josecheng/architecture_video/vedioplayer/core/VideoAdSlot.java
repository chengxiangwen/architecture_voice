package com.josecheng.architecture_video.vedioplayer.core;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.josecheng.architecture_video.vedioplayer.core.view.CustomVideoView;
import com.josecheng.architecture_video.vedioplayer.core.view.VideoFullDialog;
import com.josecheng.lib_base.ft_audio.service.AudioService;
import com.josecheng.lib_base.ft_audio.service.impl.AudioImpl;


/**
 * 视频业务逻辑层
 */
public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {

    private Context mContext;
    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    /**
     * Data
     */
    private String mVedioUrl;

    private SDKSlotListener mSlotListener;
//    @Autowired(name = "/audio/audio_service")
//    protected AudioService mAudioService;

    public VideoAdSlot(String vedioUrl, SDKSlotListener slotLitener) {
        ARouter.getInstance().inject(this);
        mVedioUrl = vedioUrl;
        mSlotListener = slotLitener;
        mParentView = slotLitener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mVideoView = new CustomVideoView(mContext);
        if (mVedioUrl != null) {
            mVideoView.setDataSource(mVedioUrl);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        //将视频播放器添加到容器中
        mParentView.addView(mVideoView);
    }

    private boolean isRealPause(){
        if (mVideoView != null){
            return mVideoView.isRealPause();
        }
        return false;
    }

    private boolean isComplete(){
        if (mVideoView != null){
            return mVideoView.isComplete();
        }
        return false;
    }

    private void pauseVedio(){
        if (mVideoView != null){
            mVideoView.seekAndPause(0);
        }
    }

    private void resumeVedio(){
        if (mVideoView != null){
            mVideoView.resume();
        }
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mVedioUrl = null;
    }

    /**
     * 实现play层接口
     */
    @Override
    public void onClickFullScreenBtn() {

        mParentView.removeView(mVideoView);
        VideoFullDialog dialog =
                new VideoFullDialog(mContext, mVideoView, mVedioUrl, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
        dialog.setSlotListener(mSlotListener);
        dialog.show();
        //全屏暂停音乐播放
        //mAudioService.pauseAudio();
        AudioImpl.getInstance().pauseAudio();
    }

    /**
     * 全屏返回小屏继续播放事件
     * @param position
     */
    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        //mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position);
        //小屏恢复音乐播放
        //mAudioService.resumeAudio();
        AudioImpl.getInstance().resumeAudio();
    }

    /**
     * 全屏播放完毕返回小屏事件
     */
    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
    }

    @Override
    public void onClickVideo() {
    }

    @Override
    public void onClickBackBtn() {
    }

    @Override
    public void onClickPlay() {
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onVideoFailed();
        }
    }

    @Override
    public void onAdVideoLoadComplete() {
        if (mSlotListener != null) {
            mSlotListener.onVideoComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    //传递消息到appcontext层
    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}
