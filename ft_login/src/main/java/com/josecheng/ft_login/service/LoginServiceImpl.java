package com.josecheng.ft_login.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.josecheng.ft_login.LoginActivity;
import com.josecheng.ft_login.manager.UserManager;
import com.josecheng.lib_base.service.ft_login.model.user.User;
import com.josecheng.lib_base.service.ft_login.service.LoginService;

@Route(path = "/login/login_service")
public class LoginServiceImpl implements LoginService {
    @Override
    public User getUserInfo() {
        return UserManager.getInstance().getUser();
    }

    @Override
    public void removeUser() {
        UserManager.getInstance().removeUser();
    }

    @Override
    public boolean hasLogin() {
        return UserManager.getInstance().hasLogin();
    }

    @Override
    public void login(Context context) {
        LoginActivity.start(context);
    }

    @Override
    public void init(Context context) {
        Log.i(LoginServiceImpl.class.getSimpleName(), "init()");
    }
}
