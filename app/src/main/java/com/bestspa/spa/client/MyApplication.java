package com.bestspa.spa.client;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.PicassoImageLoadingService;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import ss.com.bannerslider.Slider;

public class MyApplication extends Application {


    public static final String TAG = MyApplication.class.getSimpleName();
    static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppEventsLogger.activateApp(this);
        Slider.init(new PicassoImageLoadingService(this));
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
        {
            NotificationChannel notificationChannel = new NotificationChannel(Constant.NOTIFICATION_CHANNEL_ID_1
                    ,Constant.NOTIFICATION_CHANNEL_ID_1_NAME, NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription(Constant.NOTIFICATION_CHANNEL_ID_1_DECRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }

}
