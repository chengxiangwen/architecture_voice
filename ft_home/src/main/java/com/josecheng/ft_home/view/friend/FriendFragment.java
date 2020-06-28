package com.josecheng.ft_home.view.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.josecheng.ft_home.R;
import com.josecheng.ft_home.api.MockData;
import com.josecheng.ft_home.api.RequestCenter;
import com.josecheng.ft_home.model.friend.BaseFriendModel;
import com.josecheng.ft_home.model.friend.FriendBodyValue;
import com.josecheng.ft_home.view.friend.adapter.FriendRecyclerAdapter;
import com.josecheng.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.josecheng.lib_network.listener.DisposeDataListener;
import com.josecheng.lib_network.utils.ResponseEntityToModule;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener{

    private Context mContext;
    /*
     * UI
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FriendRecyclerAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    /*
     * data
     */
    private BaseFriendModel mRecommandData;
    protected List<FriendBodyValue> mDatas = new ArrayList<>();

    public static Fragment  newInstance(){
        FriendFragment friendFragment  = new FriendFragment();
        return friendFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_layout, null);
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //发请求更新UI
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }
    private void loadMore() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseFriendModel moreData = (BaseFriendModel) responseObj;
                //追加数据到adapter中
                mDatas.addAll(moreData.data.list);
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object reasonObj) {
                //显示请求失败View,显示mock数据
                onSuccess(
                        ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA, BaseFriendModel.class));
            }
        });
    }

    //请求数据
    private void requestData() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommandData = (BaseFriendModel) responseObj;
                //更新UI
                updateView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                //显示请求失败View,显示mock数据
                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA,
                        BaseFriendModel.class));
            }
        });
    }

    //更新UI
    private void updateView() {
        mSwipeRefreshLayout.setRefreshing(false); //停止刷新
        mDatas = mRecommandData.data.list;
        mAdapter = new FriendRecyclerAdapter(mContext, mDatas);
        //加载更多初始化
        mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }
}
