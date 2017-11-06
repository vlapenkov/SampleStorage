package com.yst.sklad.tsd.services;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by lapenkov on 03.11.2017.
 * Сервисный обработчик для показа прогресса
 */


public class ProgressNotificationCallback {
    private NotificationCompat.Builder builder;
    private NotificationManager nm;
    private int id, prev;

    public ProgressNotificationCallback(
            Context ctx, int id, String title, String msg) {
        this.id = id;
        prev = 0;
        builder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                .setContentTitle(title)
                .setContentText(msg)
                .setProgress(100, 0, false);
        nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());
    }

    public void onProgress(int max, int progress) {
        int percent = (int) ((100f * progress) / max);
        if (percent > (prev + 5)) {
            builder.setProgress(100, percent, false);
            nm.notify(id, builder.build());
            prev = percent;
        }
    }
    public void onComplete(String msg) {
        builder.setProgress(0, 0, false);
        builder.setContentText(msg)
                .setSmallIcon(android.R.drawable.stat_notify_sync);
        nm.notify(id, builder.build());
    }
}
