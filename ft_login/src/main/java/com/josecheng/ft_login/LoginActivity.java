package com.josecheng.ft_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.josecheng.ft_login.api.RequestCenter;
import com.josecheng.ft_login.inter.IUserLoginView;
import com.josecheng.ft_login.manager.UserManager;
import com.josecheng.ft_login.presenter.UserLoginPresenter;
import com.josecheng.lib_base.service.ft_login.model.LoginEvent;
import com.josecheng.lib_base.service.ft_login.model.user.User;
import com.josecheng.lib_common_ui.base.BaseActivity;
import com.josecheng.lib_network.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements IUserLoginView {

    private UserLoginPresenter mUserLoginPresenter;

    public static void start(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        //初始化P层
        mUserLoginPresenter = new UserLoginPresenter(this);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserLoginPresenter.login(getUserName(),getPassword());
//                RequestCenter.login(new DisposeDataListener() {
//                    @Override
//                    public void onSuccess(Object responseObj) {
//                        //登录成功
//                        User user = (User) responseObj;
//                        UserManager.getInstance().saveUser(user);
//                        EventBus.getDefault().post(new LoginEvent());
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailure(Object reasonObj) {
//                        //登录失败
//                    }
//                });
            }
        });
    }

    @Override
    public String getUserName() {
        return "18734924592";
    }

    @Override
    public String getPassword() {
        return "999999q";
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showLoginFailedView() {

    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void hideLoadingView() {

    }
}
