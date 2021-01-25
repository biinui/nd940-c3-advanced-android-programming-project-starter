package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.detail.DetailActivity
import com.udacity.R

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0

fun NotificationManager.sendNotification(messageBody: String, status: String, applicationContext: Context) {

    val seeDetailsIntent = Intent(applicationContext, DetailActivity::class.java)
    seeDetailsIntent.putExtra(applicationContext.getString(R.string.file_name), messageBody)
    seeDetailsIntent.putExtra(applicationContext.getString(R.string.status), status)
    val seeDetailsPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        seeDetailsIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        .setContentIntent(seeDetailsPendingIntent)
        .setAutoCancel(true)

        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.see_details),
            seeDetailsPendingIntent
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}