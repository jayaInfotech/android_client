package com.bestspa.spa.client.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bestspa.spa.client.API.APIClient;
import com.bestspa.spa.client.API.APIInterface;
import com.bestspa.spa.client.MainActivity;
import com.bestspa.spa.client.Model.User;
import com.bestspa.spa.client.R;
import com.bestspa.spa.client.Utiles.Constant;
import com.bestspa.spa.client.Utiles.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Map<String,String> firebaseData;
    SessionManager sessionManager;
    APIInterface apiInterface;
    User user,test;
    String title,message,type;
    NotificationManagerCompat notificationManager;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
        context = this;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        firebaseData = remoteMessage.getData();
        Log.d("remoteMessage",new Gson().toJson(firebaseData));
        if(remoteMessage.getData().size() > 0)
        {
            title = firebaseData.get("title");
            message = firebaseData.get("message");
            type = firebaseData.get("type");

            firebaseData = remoteMessage.getData();
            Intent activityIntent = new Intent(this, MainActivity.class);
            activityIntent.putExtra(Constant.FromNotiUserType,true);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, activityIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, Constant.NOTIFICATION_CHANNEL_ID_1)
                    .setSmallIcon(R.drawable.u_icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build();

            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sessionManager = new SessionManager(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Log.d("REFRESH_TOKEN", s);
        try {
            user = sessionManager.getUserDetails();
            Log.d("onMyFirebaseMessaging",user.get_id());
            Log.d("REFRESH_TOKEN", test.get_id());
            if(!TextUtils.isEmpty(user.get_id()))
            apiInterface.UpdateToken(s,user.get_id()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200)
                    {
                        user = response.body();
                        sessionManager.createLoginSession(user);
                    }else {
                        Toast.makeText(context,"Token refresh failed",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(context,"Token refresh failer",Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
