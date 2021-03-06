package com.example.frank.jinding.Application;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.example.frank.jinding.Service.LocationService;
import com.example.frank.jinding.Utils.ExceptionHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.crash.PgyerCrashObservable;
import com.pgyersdk.crash.PgyerObserver;
import com.pgyersdk.feedback.PgyerFeedbackManager;
import com.tencent.smtt.sdk.QbSdk;

import cn.jpush.android.api.JPushInterface;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 * 
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class LocationApplication extends Application {
	public LocationService locationService;
    public Vibrator mVibrator;
    @Override
    public void onCreate() {
        super.onCreate();


        //初始化文件加载模块
        QbSdk.initX5Environment(this,null);
        ExceptionHandler.getInstance().initConfig(this);


        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //SDKInitializer.initialize(getApplicationContext());

        //初始化消息通知
        JPushInterface.init(this);

        PgyCrashManager.register();
        PgyerCrashObservable.get().attach(new PgyerObserver() {
            @Override
            public void receivedCrash(Thread thread, Throwable throwable) {

            }
        });

        new PgyerFeedbackManager.PgyerFeedbackBuilder()
                .setShakeInvoke(true)
//                        .setColorDialogTitle("")    //设置Dialog 标题栏的背景色，默认为颜色为#ffffff
//                        .setColorTitleBg("")        //设置Dialog 标题的字体颜色，默认为颜色为#2E2D2D
                .setDisplayType(PgyerFeedbackManager.TYPE.DIALOG_TYPE)   //设置以Dialog 的方式打开
                .setMoreParam("KEY1","VALUE1")
                .setMoreParam("KEY2","VALUE2")
                .builder()
                .register();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
