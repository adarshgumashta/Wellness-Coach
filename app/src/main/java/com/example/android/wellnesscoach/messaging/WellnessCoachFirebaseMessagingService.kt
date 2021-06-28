package com.example.android.wellnesscoach.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.android.wellnesscoach.R
import com.example.android.wellnesscoach.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class WellnessCoachFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.let {
            Log.d(TAG, "payload: " + remoteMessage.data)
        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body!!)
        }

    }

    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        val contentIntent = Intent(applicationContext, MainActivity::class.java)

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "App Default Channel"
            val channel = NotificationChannel(
                channelId,
                "App Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            val builder = NotificationCompat.Builder(
                applicationContext,
                applicationContext.getString(R.string.app_notification_channel_id)
            )
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(applicationContext.getString(R.string.app_name))
                .setContentText(messageBody)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setChannelId(channelId)
            notificationManager.notify(0, builder.build())
        }
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }
}