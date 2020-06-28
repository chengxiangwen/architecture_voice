package com.josecheng.lib_base.service.ft_home.service;

import android.content.Context;
import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 首页模块对外暴露接口
 */
public interface HomeService extends IProvider {

  void startHomeActivity(Context context);
}
