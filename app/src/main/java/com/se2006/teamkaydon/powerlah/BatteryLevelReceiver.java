package com.se2006.teamkaydon.powerlah;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {
    Boolean charging = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
            Toast.makeText(context, "Device is charging", Toast.LENGTH_SHORT).show();
            charging = true;
        }
        else{
            intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
            Toast.makeText(context, "Device is not charging", Toast.LENGTH_SHORT).show();
            charging = false;
        }

        checkBatt(context,charging);
    }


    public void checkBatt(Context context, Boolean charging){
        Intent intentBatt = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int curLevel = intentBatt.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        Intent notify = new Intent(context, MapsActivity.class);
        Notification notif;
        PendingIntent pIntent = PendingIntent.getActivity(context,0,notify,0);

        if(curLevel <= BatteryActivity.getThreshold()){
            notif = new Notification.Builder(context).setTicker("Ticker Title").setContentTitle("Battery Low").setContentText("Battery " + curLevel + "%, would you like to open Power(full) to borrow a charger?").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pIntent).getNotification();
            notif.flags = Notification.FLAG_AUTO_CANCEL;
            NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(0,notif);
        }
    }
}
