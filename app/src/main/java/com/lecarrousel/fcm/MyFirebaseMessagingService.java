package com.lecarrousel.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lecarrousel.R;
import com.lecarrousel.activity.HomeActivity;

import org.json.JSONObject;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("Tag", "Push Notification Data: " + remoteMessage.getData());
        if (!isAppInBackground(getApplicationContext())) {
            try {
                JSONObject mJsonObject = new JSONObject(remoteMessage.getData().toString());
                String message = mJsonObject.getString("alert");
                String userId = mJsonObject.getString("userId");
                String ni = mJsonObject.getString("ni");
                String nt = mJsonObject.getString("nt");
                String orderId;
                if (nt.equalsIgnoreCase("1")) {
                    orderId = mJsonObject.getString("oId");
                } else {
                    orderId = "";
                }

                Intent resultIntent = new Intent(this, HomeActivity.class);
                resultIntent.putExtra("ni", ni);
                resultIntent.putExtra("nt", nt);
                resultIntent.putExtra("oId", orderId);
                resultIntent.putExtra("userId", userId);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setContentTitle(this.getString(R.string.app_name));
                mBuilder.setContentText(message);
                mBuilder.setAutoCancel(true);
                mBuilder.setSmallIcon(R.mipmap.notification_icon);
                mBuilder.setColor(Color.parseColor("#201215"));
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(HomeActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                Notification notification = mBuilder.build();
                myNotificationManager.notify(1, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                            break;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo task : taskInfo) {
                if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName())) {
                    isInBackground = false;
                    break;
                }
            }
        }
        return isInBackground;
    }
}
