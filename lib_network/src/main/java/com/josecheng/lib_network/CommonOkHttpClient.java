package com.josecheng.lib_network;

import com.josecheng.lib_network.cookie.SimpleCookieJar;
import com.josecheng.lib_network.https.HttpsUtils;
import com.josecheng.lib_network.listener.DisposeDataHandle;
import com.josecheng.lib_network.response.CommomJsonCallback;
import com.josecheng.lib_network.response.CommonFileCallback;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author qndroid
 * @function 用来发送get, post请求的工具类，包括设置一些请求的共用参数
 */
public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;

    /**
     *完成对okHttpClient的初始化
     */
    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("User-Agent", "Imooc-Mobile")
                        .build();
                return chain.proceed(request);
            }
        });
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        okHttpClientBuilder.connectionSpecs(Arrays.asList(
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.COMPATIBLE_TLS,
                ConnectionSpec.CLEARTEXT)).build();
        okHttpClientBuilder.connectTimeout(TIME_OUT,TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(TIME_OUT,TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(TIME_OUT,TimeUnit.SECONDS);
        okHttpClientBuilder.followRedirects(true);
        /**
         * trust all the https point
         */
//        okHttpClientBuilder.sslSocketFactory(HttpsUtils.initSSLSocketFactory(),
//                HttpsUtils.initTrustManager());
        mOkHttpClient = okHttpClientBuilder.build();
    }

    /**
     * get请求
     */
    public static Call get(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommomJsonCallback(handle));
        return call;
    }

    /**
     * pot请求
     */
    public static Call post(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommomJsonCallback(handle));
        return call;
    }

    /**
     * 文件下载
     */
    public static Call downLoadFile(Request request, DisposeDataHandle handle){
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(handle));
        return call;
    }

}