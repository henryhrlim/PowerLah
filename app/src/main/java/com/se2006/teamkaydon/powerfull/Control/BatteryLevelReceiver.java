package com.se2006.teamkaydon.powerfull.Control;

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
import android.support.v4.app.NotificationCompat;

import com.se2006.teamkaydon.powerfull.Boundary.BatteryActivity;
import com.se2006.teamkaydon.powerfull.Boundary.MapsActivity;
import com.se2006.teamkaydon.powerfull.R;

/**
 * Provides a checker to check if the device battery level has fallen below the threshold.
 * If the device battery level is below the threshold, send a notification.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {
    public static NotificationManager notificationManager;
    public static Notification.Builder n24;
    public static NotificationCompat.Builder n26;

    @Override
    public void onReceive(Context context, Intent intent) {

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
//        if((!isCharging) && (curLevel <= 20)) {
            Intent notify = new Intent(context, MapsActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context,0, notify,0);
            if (Build.VERSION.SDK_INT < 26) {
                n24 = new Notification.Builder(context)
                        .setContentTitle("Battery Low!")
                        .setContentText("Battery " + curLevel + "%, would you like to open Power(full) to borrow a charger?")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pIntent)
                        .setPriority(Notification.PRIORITY_MAX);
                notificationManager.notify(2, n24.build());
            }
            else {
                n26 = new NotificationCompat.Builder(context, "battery")
                        .setContentTitle("Battery Low!")
                        .setContentText("Battery " + curLevel + "%, would you like to open Power(full) to borrow a charger?")
                        .setBadgeIconType(R.mipmap.ic_launcher)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pIntent)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.notify(2, n26.build());
            }
            return true;
        }
        return false;
    }

    public static void initializeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("battery", "Battery Level Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifies the user when the battery level is low.");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
