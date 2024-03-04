package com.guru.newsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.forEach { (key, value) ->
            println("$key: $value")
            Log.d("MY_NEW_MESSAGE", "$key:- $value")
        }

        // Check for permission before showing the notification
        if (checkNotificationPermission()) {
            showNotification(remoteMessage.data)
        } else {
            // Handle lack of permission (e.g., log a message or take appropriate action)
            Log.e("MyFirebaseMessagingService", "Notification permission not granted")
        }
    }

    private fun checkNotificationPermission(): Boolean {
        val notificationPermission = Manifest.permission.ACCESS_NOTIFICATION_POLICY
        val permissionStatus = checkCallingOrSelfPermission(notificationPermission)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    private fun showNotification(data: Map<String, String>) {
        // Intent to open the MainActivity when the notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("title", data["title"])
                putString("message", data["message"])
            })
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Pending intent for the notification
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.notifications_24)
            .setContentTitle(data["title"])
            .setContentText(data["message"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Handle lack of permission (e.g., log a message or take appropriate action)
                Log.e("MyFirebaseMessagingService", "Notification permission not granted")
                return
            }
            notify(1, builder.build())
        }
    }
}
