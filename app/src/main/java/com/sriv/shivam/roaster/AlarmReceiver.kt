package com.sriv.shivam.roaster

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private lateinit var mediaPlayer: MediaPlayer

        fun playMedia(context: Context) {
            // Code to play a custom sound when alarm is invoked and running in loop
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        fun stopMedia() {
            // Code to stop the sound when the question is answered correctly
            mediaPlayer.stop()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val i = Intent(context, DestinationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context, 0, i,0)

        val builder = NotificationCompat.Builder(context, "roaster")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Roaster")
            .setContentText("Solve a question to close the alarm")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(101, builder.build())

        // Call playMedia() function to play audio in loop
        playMedia(context)
    }
}