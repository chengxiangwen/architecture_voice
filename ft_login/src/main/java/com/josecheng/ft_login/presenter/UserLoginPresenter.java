package com.josecheng.ft_login.presenter;


import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.josecheng.ft_login.api.MockData;
import com.josecheng.ft_login.api.RequestCenter;
import com.josecheng.ft_login.inter.IUserLoginPresenter;
import com.josecheng.ft_login.inter.IUserLoginView;
import com.josecheng.ft_login.manager.UserManager;
import com.josecheng.lib_base.ft_login.LoginPluginConfig;
import com.josecheng.lib_base.service.login.user.User;
import com.josecheng.lib_network.listener.DisposeDataListener;
;

public class UserLoginPresenter implements IUserLoginPresenter, DisposeDataListener {

    private IUserLoginView mIView;
    private Context mContext;

    public UserLoginPresenter(IUserLoginView iView, Context context) {
        mIView = iView;
        mContext = context;
    }

    @Override
    public void login(String username, String password) {
        mIView.showLoadingView();
        RequestCenter.login( this);
    }

    @Override
    public void onSuccess(Object responseObj) {
        mIView.hideLoadingView();
        User user = (User) responseObj;
        UserManager.getInstance().setUser(user);
        //发送登陆Event
       // EventBus.getDefault().post(new LoginEvent());
        sendUserBroadcast(user);
        mIView.finishActivity();
    }

    private void sendUserBroadcast(User user) {
        Intent intent = new Intent();
        intent.setAction(LoginPluginConfig.ACTION.LOGIN_SUCCESS_ACTION);
        intent.putExtra(LoginPluginConfig.ACTION.KEY_DATA,new Gson().toJson(user));
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onFailure(Object reasonObj) {
        mIView.hideLoadingView();
        onSuccess(new Gson().fromJson(MockData.LOGIN_DATA, User.class));
        mIView.showLoginFailedView();
    }
}
