package com.josecheng.ft_home.service;

import android.content.Context;
import android.util.Log;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.josecheng.ft_home.view.home.HomeActivity;
import com.josecheng.lib_base.service.ft_home.service.HomeService;

/**
 * 首页模块对外接口实现类
 */
@Route(path = "/home/home_service")
public class HomeServiceImpl implements HomeService {

  @Override
  public void startHomeActivity(Context context) {
    HomeActivity.start(context);
  }

  @Override
  public void init(Context context) {
    Log.i(HomeServiceImpl.class.getSimpleName(), "init()");
  }
}
