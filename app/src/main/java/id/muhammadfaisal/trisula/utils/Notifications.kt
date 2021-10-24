package id.muhammadfaisal.trisula.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.MainActivity
import id.muhammadfaisal.trisula.activity.MemberActivity

class Notifications {
    companion object {
        private const val NAME = "Notification_Channel_Trisula_08"

        @RequiresApi(Build.VERSION_CODES.O)
        fun createChannel(channelId: Long, context: Context) {
            val name = NAME
            val description = NAME
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId.toString(), name, importance)
            channel.description = description

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        fun show(channelId: Long, context: Context): NotificationCompat.Builder {

            val intent = when (SharedPreferences.get(context, Constant.Key.ROLE_ID)) {
                Constant.Role.MEMBER -> {
                    Intent(
                        context,
                        MemberActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                else -> {
                    Intent(
                        context,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            }

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            return NotificationCompat.Builder(context, channelId.toString())
                .setSmallIcon(R.drawable.ic_done)
                .setContentTitle("Anda Mendapatkan Notifikasi Penting.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        }
    }
}