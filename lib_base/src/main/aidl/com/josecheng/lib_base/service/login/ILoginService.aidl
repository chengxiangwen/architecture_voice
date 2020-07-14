package com.josecheng.lib_base.service.login;

import com.josecheng.lib_base.service.login.user.User;

interface ILoginService {

    boolean hasLogin();

    void removeUser();

    User getUserInfo();
}
