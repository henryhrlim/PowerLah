package com.se2006.teamkaydon.powerlah;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {
    public static NotificationManager notificationManager;
    public static NotificationCompat.Builder n;

    @Override
    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
//            Toast.makeText(context, "Device is charging", Toast.LENGTH_SHORT).show();
//            charging = true;
//        }
//        else{
//            intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
//            Toast.makeText(context, "Device is not charging", Toast.LENGTH_SHORT).show();
//            charging = false;
//        }
//
//        checkBatt(context);
    }


    public static boolean checkBatt(Context context){
        initializeNotificationChannel(context);
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        Intent intentBatteryChanged = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int curLevel = intentBatteryChanged.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        if((!isCharging) && (curLevel <= BatteryActivity.getThreshold())) {
            Intent notify = new Intent(context, MapsActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context,0, notify,0);
            n = new NotificationCompat.Builder(context, "battery")
                    .setContentTitle("Battery Low!")
                    .setContentText("Battery " + curLevel + "%, would you like to open Power(full) to borrow a charger?")
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pIntent)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.notify(2, n.build());
            return true;
        }
        return false;
    }

    public static void initializeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("battery", "Battery Level Notification Channel", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifies the user when the battery level is low.");
        notificationManager.createNotificationChannel(channel);
    }
}
