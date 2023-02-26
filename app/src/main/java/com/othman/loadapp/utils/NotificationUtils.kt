package com.othman.loadapp.utils


import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Notification.EXTRA_TITLE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.othman.loadapp.DetailActivity
import com.othman.loadapp.R

object NotificationUtils {

    private const val CHANNEL_ID = "load"
    private const val CHANNEL_NAME = "Download Status"
    private const val CHANNEL_Des = "This channel for showing the download status"
    private const val REQUEST_CODE = 1000

    @SuppressLint("InlinedApi")
    fun getChannel(context: Context): ChannelDetails {
        return ChannelDetails(
            CHANNEL_ID,
            CHANNEL_NAME,
            CHANNEL_Des,
            NotificationManager.IMPORTANCE_HIGH,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.VISIBILITY_PUBLIC
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channelDetails: ChannelDetails) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannel(
            channelDetails.id,
            channelDetails.name,
            channelDetails.importance
        ).apply {
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
            lightColor = context.getColor(R.color.colorSecondary)
            description = channelDetails.description
            lockscreenVisibility = channelDetails.visibility
            notificationManager.createNotificationChannel(this)
        }

    }

    fun sendNotification(
        context: Context,
        title: String,
        status: String,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notifyIntent = Intent(context, DetailActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val bundle = Bundle()
        bundle.putString(EXTRA_TITLE, title)
        bundle.putString(EXTRA_MESSAGE, status)
        bundle.putInt("NOTIFICATION_ID", notificationId)
        notifyIntent.putExtras(bundle)

        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context,
                    REQUEST_CODE,
                    notifyIntent,
                    PendingIntent.FLAG_MUTABLE
                )
            } else
                PendingIntent.getActivity(
                    context,
                    REQUEST_CODE,
                    notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )


        val notificationChannel = getChannel(context)

        val notification = NotificationCompat.Builder(context, notificationChannel.id)
            .setContentTitle(title)
            .setContentText(status)
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setLights(ContextCompat.getColor(context, R.color.colorSecondary), 1000, 3000)
            .setVisibility(notificationChannel.visibility)
            .setPriority(notificationChannel.priority)
            .addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.show_details),
                    pendingIntent
                )
            )
            .build()
        if (ContextCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        )
            notificationManager.notify(notificationId, notification)

    }

    fun clearNotification(context: Context, notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(notificationId)
    }
}