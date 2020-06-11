package com.josecheng.lib_image_loader.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.josecheng.lib_image_loader.R;
import com.josecheng.lib_image_loader.image.CustomRequestListener;
import com.josecheng.lib_image_loader.image.Utils;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * 图处加载类，外界唯一调用类,直持为view,notifaication,appwidget加载图片
 */
public class ImageLoaderManager {

    private ImageLoaderManager(){

    }

    private static class SingletonHolder {
        private static  ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance(){
        return SingletonHolder.instance;
    }

    /**
     *为ImageView加载图片,带回调
     */
    public void displayImageForView(ImageView imageView,String url){
        this.displayImageForView(imageView,url,null);
    }

    /**
     *为ImageView加载图片
     */
    public void displayImageForView(ImageView imageView,String url, CustomRequestListener requestListener){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .listener(requestListener)
                .into(imageView);
    }

    /**
     *为ImageView加载圆形图片
     */

    public void displayImageForCircle(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                                .create(imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
    * 完成为ViewGroup设置背景并模糊处理
    * */
    public void displayImageForViewGroup(final ViewGroup group, String url){
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {//设置宽高
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap res = resource;
                        Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @Override
                                    public Drawable apply(Bitmap bitmap) {
                                        Drawable drawable = new BitmapDrawable(
                                                Utils.doBlur(res, 100, true)
                                        );
                                        return drawable;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread(),false,100)
                                .subscribe(new Consumer<Drawable>() {
                                    @Override
                                    public void accept(Drawable drawable) throws Exception {
                                        group.setBackground(drawable);
                                    }
                                });
                    }
                });
    }

    /**
     * 为notification加载图
     */
    public void displayImageForNotification(Context context, RemoteViews remoteViews,
                                            int id, Notification notification,
                                            int NOTIFICATION_ID, String url){
        this.displayImageForTarget(context,
                initNotificationTarget(context,remoteViews,id,notification,NOTIFICATION_ID),url);
    }

    private NotificationTarget initNotificationTarget(Context context, RemoteViews remoteViews,
                                                      int id, android.app.Notification notification, int notification_id) {
        NotificationTarget notificationTarget = new NotificationTarget(context,
                id,remoteViews,notification,notification_id);
        return notificationTarget;
    }

    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target , String url){
        this.displayImageForTarget(context,target,url,null);
    }

    /**
     * 为非view加载图片，带回调
     */
    private void displayImageForTarget(Context context, Target target , String url, CustomRequestListener requestListener){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .listener(requestListener)
                .into(target);
    }

    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }
}
