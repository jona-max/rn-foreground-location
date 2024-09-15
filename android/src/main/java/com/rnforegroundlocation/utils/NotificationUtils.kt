package com.rnforegroundlocation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.content.pm.PackageManager

class NotificationUtils(private val context: Context) {
  init {
    createNotificationChannel()
  }

  fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT

      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

      val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)

      notificationManager.createNotificationChannel(channel)
    }
  }

  fun createBaseNotificationBuilder(
    title: String?,
    description: String?,
    longDescription: String?
  ): NotificationCompat.Builder {
    val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)

    val mainPendingIntent: PendingIntent =
      PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setContentTitle(title)
      .setContentText(description)
      .setStyle(NotificationCompat.BigTextStyle().bigText(longDescription))
      .setContentIntent(mainPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setOngoing(true)
      .setAutoCancel(true)
      .setOnlyAlertOnce(true)

    getAppIcon()?.let {
      builder.setSmallIcon(it)
    }

    return builder;
  }

  private fun getAppIcon(): Int? {
    return try {
      context.packageManager.getApplicationInfo(context.packageName, 0).icon

      val packageManager = context.packageManager
      val applicationInfo =
        packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)

      applicationInfo.icon
    } catch (e: PackageManager.NameNotFoundException) {
      null
    }
  }

  companion object {
    const val CHANNEL_ID = "ForegroundLocation"
    const val CHANNEL_NAME = "Foreground Location"
    const val DESCRIPTION_TEXT = "Foreground Location Service"
    const val NOTIFICATION_ID = 1

    private const val REQUEST_CODE_STOP = 0
    private const val REQUEST_CODE_PAUSE = 1
    private const val REQUEST_CODE_RESUME = 2

    private const val ACTION_STOP = "ACTION_STOP"
    private const val ACTION_PAUSE = "ACTION_PAUSE"
    private const val ACTION_RESUME = "ACTION_RESUME"
  }
}
