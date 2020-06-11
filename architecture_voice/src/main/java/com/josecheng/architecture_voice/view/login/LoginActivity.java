package com.josecheng.architecture_voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.josecheng.architecture_voice.R;
import com.josecheng.architecture_voice.api.RequestCenter;
import com.josecheng.architecture_voice.model.user.LoginEvent;
import com.josecheng.architecture_voice.model.user.User;
import com.josecheng.architecture_voice.utils.UserManager;
import com.josecheng.lib_common_ui.base.BaseActivity;
import com.josecheng.lib_network.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        //登录成功
                        User user = (User) responseObj;
                        UserManager.getInstance().saveUser(user);
                        EventBus.getDefault().post(new LoginEvent());
                        finish();
                    }

                    @Override
                    public void onFailure(Object reasonObj) {
                        //登录失败
                    }
                });
            }
        });
    }
}
