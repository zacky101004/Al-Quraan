package com.example.appal_quranv1.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.appal_quranv1.R;
import java.util.concurrent.TimeUnit;

public class DailyReminderWorker extends Worker {

    private static final String CHANNEL_ID = "daily_reminder_channel";
    private static final int NOTIFICATION_ID = 1;

    public DailyReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        return Result.success();
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Daily Reminder", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Pengingat Harian")
                .setContentText("Jangan lupa membaca Al-Quran hari ini!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void scheduleDailyReminder(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(DailyReminderWorker.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}