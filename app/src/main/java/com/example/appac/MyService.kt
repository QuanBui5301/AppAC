package com.example.appac

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.appac.MyNotification.Companion.CHANNEL_ID
import com.example.appac.databinding.FragmentSettingBinding
import com.example.appac.databinding.NotificationLayoutBinding


open class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.e("TAGTAGTAG", "My Service Create")
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var bundle : Bundle = intent?.getExtras()!!
        var error: String = bundle.getString("error")!!
        var tip: String = bundle.getString("tip")!!
        sendNotification(error, tip)
        return START_STICKY
    }


    private fun sendNotification(error : String, tip : String) {
        var intent1:Intent = Intent(this, MainActivity2::class.java)
        var pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0 , intent1, PendingIntent.FLAG_UPDATE_CURRENT)
        var remoteViews: RemoteViews = RemoteViews(getPackageName(), R.layout.notification_layout)
        remoteViews.setTextViewText(R.id.error, error)
        remoteViews.setTextViewText(R.id.tip, tip)
        var notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_error_24)
            .setSound(null)
            .setCustomContentView(remoteViews)
            .build()
        startForeground(1, notification)
        stopForeground(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAGTAGTAG", "My Service Destroy")
    }
}