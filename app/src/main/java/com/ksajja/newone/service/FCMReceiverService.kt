package com.ksajja.newone.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ksajja.newone.BuildConfig
import com.ksajja.newone.R
import com.ksajja.newone.activity.MainActivity
import com.ksajja.newone.model.Payload
import java.util.*

/**
 * Created by ksajja on 2/4/18.
 */

class FCMReceiverService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage!!.data
        var payload: Payload?
        var alertMessage: String? = null
        if (data != null && data["message"] != null) {
            payload = Gson().fromJson(data["message"] as String, Payload::class.java)
            alertMessage = payload!!.alertMessage.toString()
        }
        val mBuilder = getBuilder(alertMessage)
        // Creates an explicit int¬ent for an Activity in your app
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(Random().nextInt(), mBuilder.build())
    }


    private fun getBuilder(alertMessage: String?): NotificationCompat.Builder {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL,
                    "Default", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(notificationChannel)
            val builder = NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL)

            //icon appears in device notification bar and right hand corner of notification
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(alertMessage))
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.color = Color.parseColor("#00a7ce")
            builder.setContentTitle(resources.getString(R.string.app_name))
            builder.setContentText(alertMessage)
            //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setAutoCancel(true)
            builder.setDefaults(Notification.DEFAULT_ALL)
            return builder
        } else {
            // Use NotificationCompat.Builder to set up our notification.
            val builder = NotificationCompat.Builder(this)
            //icon appears in device notification bar and right hand corner of notification
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(alertMessage))
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setSmallIcon(R.mipmap.ic_launcher)
            builder.color = Color.parseColor("#00A6CF")
            builder.setContentTitle(resources.getString(R.string.app_name))
            builder.setContentText(alertMessage)
            //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setAutoCancel(true)
            builder.setDefaults(Notification.DEFAULT_ALL)
            return builder
        }
    }

    companion object {

        val DEFAULT_NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + "default"
    }
}
