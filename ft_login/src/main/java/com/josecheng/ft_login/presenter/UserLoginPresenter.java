package com.josecheng.ft_login.presenter;


import com.google.gson.Gson;
import com.josecheng.ft_login.api.MockData;
import com.josecheng.ft_login.api.RequestCenter;
import com.josecheng.ft_login.inter.IUserLoginPresenter;
import com.josecheng.ft_login.inter.IUserLoginView;
import com.josecheng.ft_login.manager.UserManager;
import com.josecheng.lib_base.service.ft_login.model.LoginEvent;
import com.josecheng.lib_base.service.ft_login.model.user.User;
import com.josecheng.lib_network.listener.DisposeDataListener;
;import org.greenrobot.eventbus.EventBus;

public class UserLoginPresenter implements IUserLoginPresenter, DisposeDataListener {

    private IUserLoginView mIView;

    public UserLoginPresenter(IUserLoginView iView) {
        mIView = iView;
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
        EventBus.getDefault().post(new LoginEvent());
        mIView.finishActivity();
    }

    @Override
    public void onFailure(Object reasonObj) {
        mIView.hideLoadingView();
        onSuccess(new Gson().fromJson(MockData.LOGIN_DATA, User.class));
        mIView.showLoginFailedView();
    }
}
