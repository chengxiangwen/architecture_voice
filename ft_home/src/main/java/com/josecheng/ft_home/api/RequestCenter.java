package com.josecheng.ft_home.api;

import com.josecheng.ft_home.model.friend.BaseFriendModel;
import com.josecheng.lib_network.CommonOkHttpClient;
import com.josecheng.lib_network.listener.DisposeDataHandle;
import com.josecheng.lib_network.listener.DisposeDataListener;
import com.josecheng.lib_network.request.CommonRequest;
import com.josecheng.lib_network.request.RequestParams;

public class RequestCenter {

    static class HttpConstants{
       // private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";

        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";

        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";


    }

    public static void postRequest(String url, RequestParams params, DisposeDataListener listener,
                                   Class<?> clazz){
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url, params),new DisposeDataHandle(listener,clazz));
    }
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }


    public static void requestFriendData(DisposeDataListener listener) {
        RequestCenter.getRequest(HttpConstants.HOME_FRIEND, null, listener, BaseFriendModel.class);
    }
}
