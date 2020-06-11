package com.josecheng.lib_network.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *对外提供get/post 文件上传请求
**/
public class CommonRequest {

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public static Request createPostRequest(String url , RequestParams params){
        return createPostRequest(url, params, null);
    }

    /**
     *带请求头的Post请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static Request createPostRequest(String url ,RequestParams params, RequestParams headers){
        FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
        if (params != null){
            for (Map.Entry<String , String> entry : params.urlParams.entrySet()){
                mFormBodyBuilder.add(entry.getKey(),entry.getValue());
            }
        }
        //添加请求头
        Headers.Builder mHearderBuilder = new Headers.Builder();
        if (headers != null){
            for (Map.Entry<String , String> entry : headers.urlParams.entrySet() ){
                mHearderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(mHearderBuilder.build())
                .post(mFormBodyBuilder.build())
                .build();
        return request;

    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public static  Request createGetRequest(String url, RequestParams params){
        return createGetRequest(url, params,null);

    }
    /**
     * 带请求头的Get请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static  Request createGetRequest(String url, RequestParams params, RequestParams headers){
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null){
            for (Map.Entry<String , String> entry : params.urlParams.entrySet()){
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        //添加请求头
        Headers.Builder mHearderBuilder = new Headers.Builder();
        if (headers != null){
            for (Map.Entry<String , String> entry : headers.urlParams.entrySet() ){
                mHearderBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return new Request.Builder()
                .url(urlBuilder.substring(0,urlBuilder.length()-1))
                .headers(mHearderBuilder.build())
                .get()
                .build();
    }


    public  static  final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
    /**
     *文件上传请求
     * @param url
     * @param params
     * @return
     */
    public static  Request createMultiPostRequest(String url,RequestParams params){
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null){
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build();
    }
}
