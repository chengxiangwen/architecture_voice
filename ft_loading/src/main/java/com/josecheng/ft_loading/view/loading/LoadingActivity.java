package com.josecheng.ft_loading.view.loading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.josecheng.ft_loading.R;
import com.josecheng.lib_base.ft_home.HomePluginConfig;
import com.josecheng.lib_common_ui.base.BaseActivity;
import com.josecheng.lib_common_ui.base.constant.Constant;
import com.josecheng.lib_pullalive.app.AliveJobService;
import com.qihoo360.replugin.RePlugin;

public class LoadingActivity extends BaseActivity {

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //HomeImpl.getInstance().startHomActivity(LoadingActivity.this);
            //startActivity(new Intent(LoadingActivity.this, HomeActivity.class));
            Intent intent = RePlugin.createIntent(HomePluginConfig.PLUGIN_NAME,HomePluginConfig.PAGE.PAGE_LOGIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            RePlugin.startActivity(LoadingActivity.this,intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avoidLauncherAgain();
        setContentView(R.layout.activity_loading_layout);
        pullAliveService();
        if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
            doSDCardPermission();
        } else {
            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
        }
    }

    private void avoidLauncherAgain() {
        //避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) { // 判断当前activity是不是所在任务栈的根
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();

                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                }
            }
        }
    }

    private void pullAliveService() {
        AliveJobService.start(this);
    }

    @Override
    public void doSDCardPermission() {
        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
