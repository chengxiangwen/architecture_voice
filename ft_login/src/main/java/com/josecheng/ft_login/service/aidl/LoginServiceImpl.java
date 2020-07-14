package com.josecheng.ft_login.service.aidl;

import android.os.RemoteException;

import com.josecheng.ft_login.manager.UserManager;
import com.josecheng.lib_base.service.login.ILoginService;
import com.josecheng.lib_base.service.login.user.User;

/**
 * IloginService的具体实现
 */
public class LoginServiceImpl extends ILoginService.Stub {
    @Override
    public boolean hasLogin()   {
        return UserManager.getInstance().hasLogin();
    }

    @Override
    public void removeUser(){
        UserManager.getInstance().removeUser();
    }

    @Override
    public User getUserInfo() {
        return UserManager.getInstance().getUser();
    }
}
