package org.hyperskill.phrases

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val phrase = intent.getStringExtra("phrase") ?: "Here is your motivational phrase!"
        val mainIntent = Intent(context, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_phrase)
            .setContentTitle("Your phrase of the day")
            .setContentText(phrase)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Here is your motivational phrase!"))
            .setAutoCancel(true)
            .setContentIntent(pIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationBuilder.flags = Notification.FLAG_INSISTENT or Notification.FLAG_ONLY_ALERT_ONCE

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(393939, notificationBuilder)
    }
}
