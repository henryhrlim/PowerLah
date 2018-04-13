package com.se2006.teamkaydon.powerlah;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {
    private Button startButton;
    private Button stopButton;

    private TextView timerValue;
    private TextView payment;

    private Handler customHandler = new Handler();

    private double startTime = 0;
    private double timeInMilliseconds = 0;
    private double timeSwapBuff = 0;
    private double updatedTime = 0;

    private int seconds;
    private int minutes;

    private String timerText;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.timer_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        timerValue = (TextView) findViewById(R.id.timerValue);
        payment = (TextView) findViewById(R.id.payment);
        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                payment.setText("");
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                n = new NotificationCompat.Builder(TimerActivity.this, "test")
                        .setContentTitle(getPackageName())
                        .setContentText("NOTIFICATION!")
                        .setBadgeIconType(R.mipmap.ic_launcher)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(4);
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);

        stopButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                calculatePayment(timeInMilliseconds);
                timeInMilliseconds = 0;
                timerValue.setText("00:00");
                customHandler.removeCallbacks(updateTimerThread);
            }
        });

        initChannels(this);

    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            seconds = (int) (updatedTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerText = "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
            timerValue.setText(timerText);
            customHandler.postDelayed(this, 0);

            if(updatedTime / 1000 > 5){
                notificationManager.notify(1, n.build());
            }

        }
    };

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("test", "Testing Channel", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    public void calculatePayment(double timeInMilliseconds) {
        payment = (TextView) (findViewById(R.id.payment));
        double rate = 1;
        double total = timeInMilliseconds / 1000 / 60 * rate;
        String paymentString = "Please pay $" + String.format("%.2f", total);
        payment.setText(paymentString);
    }
}
