package com.se2006.teamkaydon.powerfull.Control;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.se2006.teamkaydon.powerfull.Boundary.MapsActivity;
import com.se2006.teamkaydon.powerfull.R;

public class TimerApp extends Application {

    public static TimerApp timerAppInstance;
    public static double startTime = 0;
    public static double timeInMilliseconds = 0;
    public static double timeSwapBuff = 0;
    public static double updatedTime = 0;

    public static int seconds;
    public static int minutes;

    public static String timerText;

    public static NotificationManager notificationManager;
    public static Notification.Builder n24;
    public static NotificationCompat.Builder n26;

    public static Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

        timerAppInstance = this;

        initializeNotificationChannel(this);
    }


    public static void startTimer() {
        startTime = SystemClock.uptimeMillis();
        runnable.run();
    }

    public static void stopTimer() {
        handler.removeCallbacks(runnable);
        MapsActivity.timer.setText("");
        MapsActivity.timer.setAlpha(0);
    }

    public static Runnable runnable = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            seconds = (int) (updatedTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerText = "Borrowed for: " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

//            if (seconds == 5) {
            if((minutes % 60 == 0 || minutes % 60 == 30) && minutes != 0){
                if (Build.VERSION.SDK_INT < 26) {
                    n24 = new Notification.Builder(timerAppInstance)
                            .setContentTitle("Power(full)")
                            .setContentText("You have borrowed the charger for " + minutes + " minutes.")
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(Notification.PRIORITY_MAX);
                    notificationManager.notify(1, n24.build());
                }
                else {
                    n26 = new NotificationCompat.Builder(timerAppInstance, "timer")
                            .setContentTitle("Power(full)")
                            .setContentText("You have borrowed the charger for " + minutes + " minutes.")
                            .setBadgeIconType(R.mipmap.ic_launcher)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(4);
                    notificationManager.notify(1, n26.build());
                }
            }
            MapsActivity.timer.setText(timerText);
            MapsActivity.timer.setAlpha(1);
            handler.postDelayed(runnable, 0);
        }
    };

    public void initializeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("timer", "Timer Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification channel for timer notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }

}