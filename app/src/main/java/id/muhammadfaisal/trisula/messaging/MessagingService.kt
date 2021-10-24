package id.muhammadfaisal.trisula.messaging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.muhammadfaisal.trisula.R
import id.muhammadfaisal.trisula.activity.MainActivity
import id.muhammadfaisal.trisula.activity.MemberActivity
import id.muhammadfaisal.trisula.utils.Constant
import id.muhammadfaisal.trisula.utils.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MessagingService : FirebaseMessagingService() {

    companion object {
        var token: String? = null
        private const val Key =
            "AAAAt7jotfw:APA91bEcPLDxShFuykXLCNxxIABXYDz1bvG_Lk_bCEQBMcqrkzlBLtduPcPw0CMvI4p5pfzPdZ0vN1HqNwFxkpyfel3JFPyusnu2Ol6Fbo-dB00v7zhsK1tgBjVyUIPfkpDNRGIyK9-U"

        fun sendMessage(title: String, content: String, topic: String) {
            GlobalScope.launch {
                val endpoint = "https://fcm.googleapis.com/fcm/send"
                try {
                    val url = URL(endpoint)
                    val httpsURLConnection: HttpsURLConnection =
                        url.openConnection() as HttpsURLConnection
                    httpsURLConnection.readTimeout = 10000
                    httpsURLConnection.connectTimeout = 15000
                    httpsURLConnection.requestMethod = "POST"
                    httpsURLConnection.doInput = true
                    httpsURLConnection.doOutput = true

                    // Adding the necessary headers
                    httpsURLConnection.setRequestProperty("authorization", "key=$Key")
                    httpsURLConnection.setRequestProperty("Content-Type", "application/json")

                    // Creating the JSON with post params
                    val body = JSONObject()

                    val data = JSONObject()
                    data.put("title", title)
                    data.put("content", content)
                    body.put("data", data)

                    body.put("to", "/topics/$topic")

                    val outputStream: OutputStream =
                        BufferedOutputStream(httpsURLConnection.outputStream)
                    val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                    writer.write(body.toString())
                    writer.flush()
                    writer.close()
                    outputStream.close()
                    val responseCode: Int = httpsURLConnection.responseCode
                    val responseMessage: String = httpsURLConnection.responseMessage
                    Log.d("Response:", "$responseCode $responseMessage")
                    var result = String()
                    var inputStream: InputStream? = null
                    inputStream = if (responseCode in 400..499) {
                        httpsURLConnection.errorStream
                    } else {
                        httpsURLConnection.inputStream
                    }

                    if (responseCode == 200) {
                        Log.e("Success:", "notification sent $title \n $content")
                        // The details of the user can be obtained from the result variable in JSON format
                    } else {
                        Log.e("Error", "Error Response")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e("onMessageReceived", p0.data.toString())

        val title = p0.data["title"]
        val content = p0.data["content"]
        var channelId = 0

        var sound: Uri?

        when (title) {
            getString(R.string.apel_kesiapsiagaan) -> {
                channelId = 1001
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.apel_kesiapsiagaan)
            }
            getString(R.string.alarm_stelling) -> {
                channelId = 10020
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.alarm_stelling)
            }
            getString(R.string.apel_luar_biasa) -> {
                channelId = 1003
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.apel_luar_biasa)
            }
            getString(R.string.bahaya_kebakaran) -> {
                channelId = 1004
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.bahaya_kebakaran)
            }
            getString(R.string.bantuan_kepolisian) -> {
                channelId = 1005
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.bantuan_kepolisian)
            }
            getString(R.string.bencana_alam) -> {
                channelId = 10060
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.bencana_alam)
            }
            getString(R.string.tanda_aman) -> {
                channelId = 10070
                sound =
                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + packageName+"/"+R.raw.tanda_aman)
            }else -> {
                channelId = 101001
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
        }

        val roleId = SharedPreferences.get(this, Constant.Key.ROLE_ID)

        val intent: Intent?
        when (roleId) {
            Constant.Role.MEMBER -> {
                intent = Intent(this, MemberActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            else -> {
                intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            checkNotificationChannel(channelId.toString(), sound)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId.toString())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound, AudioManager.STREAM_NOTIFICATION)

        val notificationBuild = notification.build()
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(channelId, notificationBuild)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNotificationChannel(CHANNEL_ID: String, sound: Uri) {
        val audioAttribute = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description = "CHANNEL_DESCRIPTION"
        notificationChannel.setSound(sound, audioAttribute)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onNewToken(p0: String) {
        token = p0
        super.onNewToken(p0)
    }
}