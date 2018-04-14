package com.se2006.teamkaydon.powerlah;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Button;
import android.widget.Toast;

public class TimerApp extends Application {

    public static TimerApp timerAppInstance;
    private SimpleDateFormat dateFormat;
    public static double startTime = 0;
    public static double timeInMilliseconds = 0;
    public static double timeSwapBuff = 0;
    public static double updatedTime = 0;

    public static int seconds;
    public static int minutes;

    public static String timerText;

    public static NotificationManager notificationManager;
    public static NotificationCompat.Builder n;

    @Override
    public void onCreate() {
        super.onCreate();

        timerAppInstance = this;

//        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        initChannels(this);
    }

    public void afficher() {
        Toast.makeText(getBaseContext(), "x", Toast.LENGTH_LONG).show();
        handler.postDelayed(runnable,1000);
    }

    public static void startTimer() {
        startTime = SystemClock.uptimeMillis();
        runnable.run();
    }

    public static void stopTimer() {
        handler.removeCallbacks(runnable);
        MapsActivity.timer.setText("");
    }

    public static Handler handler = new Handler();
    public static Runnable runnable = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            seconds = (int) (updatedTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerText = "Borrowed for: " + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

            if (seconds == 5) {
//            if((minutes % 60 == 0 || minutes % 60 == 30) && minutes != 0){
                n = new NotificationCompat.Builder(timerAppInstance, "test")
                        .setContentTitle("Power(full)")
                        .setContentText("You have borrowed the charger for " + minutes + " minutes.")
                        .setBadgeIconType(R.mipmap.ic_launcher)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.notify(1, n.build());
            }
            MapsActivity.timer.setText(timerText);
            handler.postDelayed(runnable, 0);
        }
    };

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return;
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("test", "Testing Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
        }
    }

}