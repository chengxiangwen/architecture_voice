package com.josecheng.architecture_voice.view.home.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.josecheng.architecture_voice.view.discory.DiscoryFragment;
import com.josecheng.architecture_voice.view.friend.FriendFragment;
import com.josecheng.architecture_voice.view.mine.MineFragment;

import com.josecheng.architecture_voice.model.CHANNEL;

/**
 * 首页ViewPagerAdapter
 * */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private CHANNEL[] mList;
    public HomePagerAdapter(FragmentManager fm, CHANNEL[] datas) {
        super(fm);
        mList = datas;
    }

    //    初始化对应的Fragment
    @Override
    public Fragment getItem(int position) {
        int type = mList[position].getValue();
        switch (type) {
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();
//            case CHANNEL.VIDEO_ID:
//                return VideoFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.length;
    }
}
